package com.ayaan.UrlShortner.Controller;

import com.ayaan.UrlShortner.Entity.UrlEntity;
import com.ayaan.UrlShortner.Entity.Users;
import com.ayaan.UrlShortner.Service.AdminService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    private final AdminService adminService;

    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    // ---------- USER MANAGEMENT ----------

    @GetMapping("/users")
    public ResponseEntity<List<Users>> getAllUsers() {
        System.out.println("AdminController getAllUsers");
        return ResponseEntity.ok(adminService.getAllUsers());
    }

    @PatchMapping("/users/{userId}/suspend")
    public ResponseEntity<Void> suspendUser(@PathVariable Long userId) {
        adminService.suspendUser(userId);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/users/{userId}/reactivate")
    public ResponseEntity<Void> reactivateUser(@PathVariable Long userId) {
        adminService.reactivateUser(userId);
        return ResponseEntity.noContent().build();
    }

    // ---------- URL MODERATION ----------

    @GetMapping("/urls")
    public ResponseEntity<List<UrlEntity>> getAllUrls() {
        return ResponseEntity.ok(adminService.getAllUrls());
    }

    @PatchMapping("/urls/{shortCode}/disable")
    public ResponseEntity<Void> disableUrl(@PathVariable String shortCode) {
        adminService.disableUrl(shortCode);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/urls/{shortCode}/flag")
    public ResponseEntity<Void> flagUrl(@PathVariable String shortCode) {
        adminService.flagUrl(shortCode);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/urls/{shortCode}/reactivate")
    public ResponseEntity<Void> reactivateUrl(@PathVariable String shortCode) {
        adminService.reactivateUrl(shortCode);
        return ResponseEntity.noContent().build();
    }
}