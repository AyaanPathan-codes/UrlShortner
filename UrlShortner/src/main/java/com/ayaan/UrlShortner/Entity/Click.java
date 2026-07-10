package com.ayaan.UrlShortner.Entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Click {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name="url_id")
    private UrlEntity url;

    private LocalDateTime clickedAt;
    private String ipAddress;
    private String country;
    private String deviceType;
    private String browser;
    private String os;
    private String referrer;

}
