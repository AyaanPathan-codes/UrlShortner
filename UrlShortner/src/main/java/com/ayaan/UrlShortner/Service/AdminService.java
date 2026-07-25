package com.ayaan.UrlShortner.Service;

import com.ayaan.UrlShortner.Entity.Enums.UrlStatus;
import com.ayaan.UrlShortner.Entity.Enums.UserStatus;
import com.ayaan.UrlShortner.Entity.UrlEntity;
import com.ayaan.UrlShortner.Entity.Users;
import com.ayaan.UrlShortner.Exceptions.CustomExceptions;
import com.ayaan.UrlShortner.Repo.UrlRepo;
import com.ayaan.UrlShortner.Repo.UsersRepo;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class AdminService {

    private final UsersRepo usersRepo;
    private final UrlRepo urlRepo;

    public AdminService(UsersRepo usersRepo, UrlRepo urlRepo) {
        this.usersRepo = usersRepo;
        this.urlRepo = urlRepo;
    }

    // ---------- USER MANAGEMENT ----------

    @Transactional(readOnly = true)
    public List<Users> getAllUsers() {
        return usersRepo.findAll();
    }

    @Transactional
    public void suspendUser(Long userId) {
        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new CustomExceptions.UserNotFoundException(
                        "No user found with id: " + userId));

        user.setStatus(UserStatus.SUSPENDED);
        usersRepo.save(user);
    }

    @Transactional
    public void reactivateUser(Long userId) {
        Users user = usersRepo.findById(userId)
                .orElseThrow(() -> new CustomExceptions.UserNotFoundException(
                        "No user found with id: " + userId));

        user.setStatus(UserStatus.ACTIVE);
        usersRepo.save(user);
    }

    // ---------- URL MODERATION ----------

    @Transactional(readOnly = true)
    public List<UrlEntity> getAllUrls() {
        return urlRepo.findAll();
    }

    @CacheEvict(value = "urlCache", key = "#shortCode")
    @Transactional
    public void disableUrl(String shortCode) {
        UrlEntity entity = urlRepo.findByShortUrl(shortCode)
                .orElseThrow(() -> new CustomExceptions.UrlNotFoundException(
                        "No URL found for code: " + shortCode));

        entity.setStatus(UrlStatus.DISABLED);
        urlRepo.save(entity);
    }

    @CacheEvict(value = "urlCache", key = "#shortCode")
    @Transactional
    public void flagUrl(String shortCode) {
        UrlEntity entity = urlRepo.findByShortUrl(shortCode)
                .orElseThrow(() -> new CustomExceptions.UrlNotFoundException(
                        "No URL found for code: " + shortCode));

        entity.setStatus(UrlStatus.FLAGGED);
        urlRepo.save(entity);
    }

    @CacheEvict(value = "urlCache", key = "#shortCode")
    @Transactional
    public void reactivateUrl(String shortCode) {
        UrlEntity entity = urlRepo.findByShortUrl(shortCode)
                .orElseThrow(() -> new CustomExceptions.UrlNotFoundException(
                        "No URL found for code: " + shortCode));

        entity.setStatus(UrlStatus.ACTIVE);
        urlRepo.save(entity);
    }
}