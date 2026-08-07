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
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/urls")
@Tag(
        name = "URL Management",
        description = """
                APIs for creating and managing shortened URLs.

                Features

                • Random Short URLs

                • Premium Custom Alias

                • URL Expiration

                • Click Analytics

                • JWT Authentication

                • Bucket4j Rate Limiting
                """
)
public class UrlController {

    private final UrlService urlService;
    private final UrlRepo urlRepo;
    @Value("${app.base-url}")
    private String baseUrl;


    public UrlController(UrlService urlService, UrlRepo urlRepo) {
        this.urlService = urlService;
        this.urlRepo = urlRepo;
    }

    @Operation(
            summary = "Create Short URL",
            description = """
                Creates a new shortened URL.

                Business Rules

                FREE PLAN
                • Maximum 10 active URLs
                • Random short code only

                PREMIUM PLAN
                • Unlimited URLs
                • Custom aliases supported
                • Optional expiration date

                Security
                • JWT Authentication required

                Rate Limit
                • 10 requests per minute
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Short URL created successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid URL or validation failed"),
            @ApiResponse(responseCode = "401", description = "JWT token missing or invalid"),
            @ApiResponse(responseCode = "403", description = "Premium subscription required"),
            @ApiResponse(responseCode = "409", description = "Custom alias already exists"),
            @ApiResponse(responseCode = "429", description = "Rate limit exceeded")
    })
    @SecurityRequirement(name = "Bearer Authentication")

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

    @Operation(
            summary = "Get URL Statistics",
            description = """
                Retrieves analytics for a shortened URL.

                Returns

                • Original URL

                • Short URL

                • Total Clicks

                • Current Status

                • Expiration Date
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Statistics retrieved successfully"),
            @ApiResponse(responseCode = "404", description = "Short URL not found"),
            @ApiResponse(responseCode = "401", description = "Authentication required")
    })
    @SecurityRequirement(name = "Bearer Authentication")

    // UrlController
    @GetMapping("/{shortCode}/stats")
    public ResponseEntity<UrlResponseDto> getStats(@PathVariable String shortCode) {
        UrlEntity entity = urlRepo.findByShortUrl(shortCode)
                .orElseThrow(() -> new CustomExceptions.UrlNotFoundException("Not found"));
        return ResponseEntity.ok(UrlResponseDto.from(entity, baseUrl));
    }
}
