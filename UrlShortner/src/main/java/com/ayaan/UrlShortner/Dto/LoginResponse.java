package com.ayaan.UrlShortner.Dto;

public record LoginResponse(
        String token,
        String email,
        String planType
) {}