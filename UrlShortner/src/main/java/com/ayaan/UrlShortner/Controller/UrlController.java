package com.ayaan.UrlShortner.Controller;

import com.ayaan.UrlShortner.Dto.CreateUrlReqDto;
import com.ayaan.UrlShortner.Dto.UrlResponseDto;
import com.ayaan.UrlShortner.Entity.CustomUserDetails;
import com.ayaan.UrlShortner.Entity.UrlEntity;
import com.ayaan.UrlShortner.Entity.Users;
import com.ayaan.UrlShortner.Exceptions.CustomExceptions;
import com.ayaan.UrlShortner.Repo.UrlRepo;
import com.ayaan.UrlShortner.Service.UrlService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/urls")
public class UrlController {

    private final UrlService urlService;
    private final UrlRepo urlRepo;
    @Value("${app.base-url}")
    private String baseUrl;


    public UrlController(UrlService urlService, UrlRepo urlRepo) {
        this.urlService = urlService;
        this.urlRepo = urlRepo;
    }
    @PostMapping
    public ResponseEntity<UrlResponseDto> createUrl(@RequestBody @Valid CreateUrlReqDto request,
                                                    @AuthenticationPrincipal CustomUserDetails principal) {
        Users user = principal.getUser();
        UrlEntity created = urlService.createShortUrlSafely(
                request.longUrl(),
                request.customAlias(),
                request.expiresAt(),
                user
        );

        return ResponseEntity.ok(UrlResponseDto.from(created, baseUrl));
    }

    // UrlController
    @GetMapping("/{shortCode}/stats")
    public ResponseEntity<UrlResponseDto> getStats(@PathVariable String shortCode) {
        UrlEntity entity = urlRepo.findByShortUrl(shortCode)
                .orElseThrow(() -> new CustomExceptions.UrlNotFoundException("Not found"));
        return ResponseEntity.ok(UrlResponseDto.from(entity, baseUrl));
    }
}
