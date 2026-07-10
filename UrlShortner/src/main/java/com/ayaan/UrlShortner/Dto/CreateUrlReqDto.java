package com.ayaan.UrlShortner.Dto;

import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import java.time.LocalDateTime;

public record CreateUrlReqDto(@NotBlank(message = "Long URL is required")
                                @URL(message = "Must be a valid URL")
                                String longUrl,

                              String customAlias,

                              LocalDateTime expiresAt) {
}
