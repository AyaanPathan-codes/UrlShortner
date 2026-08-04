package com.ayaan.UrlShortner.Controller;

import com.ayaan.UrlShortner.Entity.CustomUserDetails;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("/api/users")
public class UserController {

    @GetMapping("/me")
    public ResponseEntity<Map<String, Object>> getCurrentUser(
            @AuthenticationPrincipal CustomUserDetails principal) {

        var user = principal.getUser();

        return ResponseEntity.ok(Map.of(
                "email", user.getEmail(),
                "planType", user.getPlanType().name(),
                "role", user.getRole().name(),
                "status", user.getStatus().name()
        ));
    }
}