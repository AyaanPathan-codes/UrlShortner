package com.ayaan.UrlShortner.Controller;

import com.ayaan.UrlShortner.Dto.CreateUrlReqDto;
import com.ayaan.UrlShortner.Dto.UrlResponseDto;
import com.ayaan.UrlShortner.Entity.UrlEntity;
import com.ayaan.UrlShortner.Entity.Users;
import com.ayaan.UrlShortner.Service.UrlService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/urls")
public class UrlController {

    private final UrlService urlService;

    @Value("${app.base-url}")
    private String baseUrl;

    public UrlController(UrlService urlService) {
        this.urlService = urlService;
    }
    @PostMapping
    public ResponseEntity<UrlResponseDto> createUrl(@RequestBody @Valid CreateUrlReqDto request,
                                                    Users user) {

        UrlEntity created = urlService.createShortUrlSafely(
                request.longUrl(),
                request.customAlias(),
                request.expiresAt(),
                user
        );

        return ResponseEntity.ok(UrlResponseDto.from(created, baseUrl));
    }
}
