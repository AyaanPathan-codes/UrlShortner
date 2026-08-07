package com.ayaan.UrlShortner.Controller;

import com.ayaan.UrlShortner.Dto.LoginRequest;
import com.ayaan.UrlShortner.Dto.LoginResponse;
import com.ayaan.UrlShortner.Dto.RegisterRequest;
import com.ayaan.UrlShortner.Service.AuthService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;

@RestController
@RequestMapping("/api/auth")
@Tag(
        name = "Authentication",
        description = """
                APIs for user registration and authentication.
                
                Features:
                • User Registration
                • JWT Login
                • BCrypt Password Encryption
                • Role-based Authentication
                • Premium & Free User Support
                """
)
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }


    @Operation(
            summary = "Register a new user",
            description = """
                Creates a new account.

                Business Rules:
                • Email must be unique.
                • Password is encrypted using BCrypt.
                • Every new user is assigned the FREE plan.
                • JWT access token is returned after successful registration.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Registration successful"),
            @ApiResponse(responseCode = "400", description = "Validation failed"),
            @ApiResponse(responseCode = "409", description = "Email already exists")
    })
    @PostMapping("/register")
    public ResponseEntity<LoginResponse> register(@RequestBody @Valid RegisterRequest request) {
        return ResponseEntity.ok(authService.register(request));
    }


    @Operation(
            summary = "Authenticate user",
            description = """
                Authenticates an existing user.

                Returns:
                • JWT Access Token
                • User Role
                • Current Subscription Plan

                Use the returned JWT token in the Authorize button.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Login successful"),
            @ApiResponse(responseCode = "401", description = "Invalid email or password"),
            @ApiResponse(responseCode = "400", description = "Validation failed")
    })
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody @Valid LoginRequest request) {
        return ResponseEntity.ok(authService.login(request));
    }
}