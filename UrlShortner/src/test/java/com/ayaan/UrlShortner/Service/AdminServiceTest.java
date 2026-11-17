package com.ayaan.UrlShortner.Service;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;

import static org.junit.jupiter.api.Assertions.*;

class AdminServiceTest {
    @Mock
    AdminService adminService;
    @Test
    void getAllUsers() {
        adminService.getAllUsers();
    }

    @Test
    void suspendUser() {
    }

    @Test
    void reactivateUser() {
    }

    @Test
    void getAllUrls() {
    }

    @Test
    void disableUrl() {
    }

    @Test
    void flagUrl() {
    }

    @Test
    void reactivateUrl() {
    }
}