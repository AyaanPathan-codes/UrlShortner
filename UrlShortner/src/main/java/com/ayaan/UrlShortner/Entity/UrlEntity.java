package com.ayaan.UrlShortner.Entity;

import com.ayaan.UrlShortner.Entity.Enums.UrlStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor

@Table(name = "urls")
public class UrlEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String url;

    @Column(nullable = false,unique = true)
    private String shortUrl;

    @CreationTimestamp
    @Column(nullable = false,updatable = false)
    private LocalDateTime createdDate;

    @UpdateTimestamp
    @Column(nullable = false)
    private LocalDateTime updatedDate;

    private LocalDateTime expiresAt;

    private Long clickCount = 0L;
    private String Status;
    @JoinColumn(name = "userId",nullable = false)
    @ManyToOne(fetch = FetchType.LAZY)
    private Users user;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UrlStatus urlStatus = UrlStatus.ACTIVE;
}
