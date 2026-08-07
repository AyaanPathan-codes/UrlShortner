package com.ayaan.UrlShortner.Dto;

import com.ayaan.UrlShortner.Entity.Enums.PlanType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.Size;

@Schema(
        name = "Register Request",
        description = "Request body used to register a new user."
)
public record RegisterRequest(



        @Schema(
                description = "User email address. Must be unique.",
                example = "ayaan@example.com"
        )
        @NotBlank(message = "Email is required")
        @Email(message = "Must be a valid email")
        String email,


        @Schema(
                description = """
                        User password.

                        Requirements:
                        • Minimum 8 characters
                        • Password is securely encrypted using BCrypt before storage.
                        """,
                example = "Password@123"
        )
        @NotBlank(message = "Password is required")
        @Size(min = 8, message = "Password must be at least 8 characters")
        String password
) {}