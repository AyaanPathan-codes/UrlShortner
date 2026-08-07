package com.ayaan.UrlShortner.Dto;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;
import java.time.LocalDateTime;

public record CreateUrlReqDto(
        @NotBlank(message = "Long URL is required")
        @URL(message = "Must be a valid URL")
        String longUrl,

        @Schema(
                description = "Optional. Available only for PREMIUM users. FREE users will receive 403 Forbidden if this field is provided.",
                example = "my-custom-link"
        )
        String customAlias,

        @Schema(
                description = "Optional expiration date. Leave null for a permanent link.",
                example = "2026-12-31T23:59:59"
        )
        LocalDateTime expiresAt) {
}
