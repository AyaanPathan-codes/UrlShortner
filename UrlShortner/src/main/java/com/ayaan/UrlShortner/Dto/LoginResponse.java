package com.ayaan.UrlShortner.Dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "Authentication Response",
        description = "Returned after successful registration or login."
)
public record LoginResponse(

        @Schema(
                description = "JWT Access Token. Use this token in the Authorization header.",
                example = "eyJhbGciOiJIUzI1NiJ9..."
        )
        String token,

        @Schema(
                description = "Authenticated user's email address.",
                example = "ayaan@example.com"
        )
        String email,

        @Schema(
                description = "Current subscription plan.",
                example = "FREE",
                allowableValues = {"FREE", "PREMIUM"}
        )
        String planType
) {}