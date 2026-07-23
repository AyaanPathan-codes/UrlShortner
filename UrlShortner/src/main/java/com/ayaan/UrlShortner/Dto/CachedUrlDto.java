package com.ayaan.UrlShortner.Dto;

import com.ayaan.UrlShortner.Entity.Enums.UrlStatus;

import java.io.Serializable;
import java.time.LocalDateTime;

public record CachedUrlDto( Long id,
                            String url,
                            UrlStatus status,
                            LocalDateTime expiresAt) implements Serializable{}
