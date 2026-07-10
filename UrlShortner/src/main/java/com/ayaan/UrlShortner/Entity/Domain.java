package com.ayaan.UrlShortner.Entity;

import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;

import java.time.LocalDateTime;

public class Domain {
    private Long id;
    private String domain_name;

    @JoinColumn(name = "user_id",nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Users user;
    private Boolean verified;
    private LocalDateTime created_at;
    private LocalDateTime updated_at;
}
