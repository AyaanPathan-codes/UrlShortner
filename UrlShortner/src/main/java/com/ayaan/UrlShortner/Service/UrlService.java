package com.ayaan.UrlShortner.Service;

import com.ayaan.UrlShortner.Dto.CachedUrlDto;
import com.ayaan.UrlShortner.Entity.Enums.PlanType;
import com.ayaan.UrlShortner.Entity.Enums.UrlStatus;
import com.ayaan.UrlShortner.Entity.UrlEntity;
import com.ayaan.UrlShortner.Entity.Users;
import com.ayaan.UrlShortner.Exceptions.CustomExceptions;
import com.ayaan.UrlShortner.Repo.UrlRepo;
import com.ayaan.UrlShortner.Utils.ShortCodeGenerator;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;

@Service
public class UrlService {

    private final UrlRepo urlRepo;
    private final ShortCodeGenerator shortCodeGenerator;

    public UrlService(UrlRepo urlRepo, ShortCodeGenerator shortCodeGenerator) {
        this.urlRepo = urlRepo;
        this.shortCodeGenerator = shortCodeGenerator;
    }

    private static final int MAX_RETRIES = 5;
    private static final int INITIAL_LENGTH = 6;
    private static final int FALLBACK_LENGTH = 7;


    @Transactional
    public UrlEntity createShortUrlSafely(String longUrl, String customAlias,
                                          LocalDateTime expiresAt, Users user) {
        validateCustomAliasPermission(customAlias, user);
        for (int attempt = 0; attempt < MAX_RETRIES; attempt++) {
            try {
                String shortCode = resolveShortCode(customAlias, attempt);
                return saveNewUrl(longUrl, shortCode, expiresAt, user);

            } catch (DataIntegrityViolationException e) {
                // DB-level unique constraint caught a race — a concurrent
                // request took this code between our check and our save.
                if (customAlias != null && !customAlias.isBlank()) {
                    // custom alias collided — don't silently regenerate,
                    // that's a user-facing error, not a retry case
                    throw new CustomExceptions.DuplicateAliasException(
                            "Alias already taken: " + customAlias);
                }
                // random code collided — loop again and try a fresh one
            }
        }

        throw new IllegalStateException(
                "Could not generate a unique short URL after " + MAX_RETRIES + " attempts");
    }


    private String resolveShortCode(String customAlias, int attempt) {
        if (customAlias != null && !customAlias.isBlank()) {
            if (urlRepo.existsByShortUrl(customAlias)) {
                throw new CustomExceptions.DuplicateAliasException("Alias already taken: " + customAlias);
            }
            return customAlias;
        }

        int length = attempt >= 2 ? FALLBACK_LENGTH : INITIAL_LENGTH;
        String candidate = shortCodeGenerator.generate(length);

        // optimization only — the real guarantee is the DB unique constraint
        // caught in the catch block above
        while (urlRepo.existsByShortUrl(candidate)) {
            candidate = shortCodeGenerator.generate(length);
        }
        return candidate;

        }

    private UrlEntity saveNewUrl(String longUrl, String shortCode,
                                 LocalDateTime expiresAt, Users user) {
        UrlEntity entity = new UrlEntity();
        entity.setUrl(longUrl);
        entity.setShortUrl(shortCode);
        entity.setExpiresAt(expiresAt);
        entity.setUser(user);
        entity.setStatus(UrlStatus.ACTIVE);
        entity.setClickCount(0L);

        return urlRepo.save(entity);
    }

    // ---------- READ / RESOLVE ----------

    @Cacheable(value = "urlCache", key = "#shortCode")
    @Transactional(readOnly=true)
    public CachedUrlDto getActiveUrlOrThrow(String shortCode) {
        UrlEntity entity = urlRepo.findByShortUrl(shortCode)
                .orElseThrow(() -> new CustomExceptions.UrlNotFoundException(
                        "No URL found for code: " + shortCode));

        if (entity.getStatus() == UrlStatus.DISABLED
                || entity.getStatus() == UrlStatus.FLAGGED) {
            throw new CustomExceptions.UrlNotFoundException("This link is no longer available");
        }

        if (entity.getExpiresAt() != null
                && entity.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new CustomExceptions.UrlExpiredException("This link has expired");
        }

        return new CachedUrlDto(entity.getId(),entity.getUrl(),entity.getStatus(),entity.getExpiresAt());
    }


    @CacheEvict(value = "urlCache", key = "#shortCode")
    public void disableUrl(String shortCode) {
        UrlEntity entity = urlRepo.findByShortUrl(shortCode)
                .orElseThrow(() -> new CustomExceptions.UrlNotFoundException(
                        "No URL found for code: " + shortCode));
        entity.setStatus(UrlStatus.DISABLED);
        urlRepo.save(entity);
    }
    private void validateCustomAliasPermission(String customAlias, Users user) {
        boolean wantsCustomAlias = customAlias != null && !customAlias.isBlank();

        if (wantsCustomAlias && user.getPlanType() == PlanType.FREE) {
            throw new CustomExceptions.PlanRestrictionException(
                    "Custom aliases are only available on paid plans");
        }
    }


    // UrlService — new method, NOT cached, always runs
    @Transactional
    public void incrementClickCount(String shortCode) {
        urlRepo.incrementClickCount(shortCode);
    }
}
