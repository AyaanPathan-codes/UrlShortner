package com.ayaan.UrlShortner.Entity;

import com.ayaan.UrlShortner.Entity.Enums.AdminActionType;
import com.ayaan.UrlShortner.Entity.Enums.TargetType;
import com.ayaan.UrlShortner.Entity.Enums.UrlStatus;
import jakarta.persistence.*;
import org.hibernate.annotations.CreationTimestamp;

import java.time.LocalDateTime;

public class Admin_Action_Log {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name= "admin_id",nullable = false)
    private Users admin;

    @Enumerated(EnumType.STRING)
    private AdminActionType action_type;

    @Enumerated(EnumType.STRING)
    private TargetType targetType;

    private Long targetId;

    @Column(length = 500)
    private String reason;

    @CreationTimestamp
    private LocalDateTime createdAt;
}
