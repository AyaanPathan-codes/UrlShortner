package com.ayaan.UrlShortner.Dto;

import com.ayaan.UrlShortner.Entity.UrlEntity;

import java.time.LocalDateTime;

public record UrlResponseDto(Long id,
                             String shortUrl,
                             String longUrl,
                             LocalDateTime createdAt,
                             LocalDateTime expiresAt,
                             Long clickCount,
                             String status) {


    public static UrlResponseDto from(UrlEntity entity, String baseUrl) {
        return new UrlResponseDto(
                entity.getId(),
                baseUrl + "/" + entity.getShortUrl(),
                entity.getUrl(),
                entity.getCreatedDate(),
                entity.getExpiresAt(),
                entity.getClickCount(),
                entity.getStatus().name()
        );
    }
}
