package com.ayaan.UrlShortner.Dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import io.swagger.v3.oas.annotations.media.Schema;

@Schema(
        name = "Login Request",
        description = "Request body used to authenticate an existing user."
)

public record LoginRequest(


        @Schema(
                description = "Registered email address.",
                example = "ayaan@example.com"
        )
        @NotBlank(message = "Email is required")
        @Email(message = "Must be a valid email")
        String email,


        @Schema(
                description = "Registered account password.",
                example = "Password@123"
        )
        @NotBlank(message = "Password is required")
        String password
) {}