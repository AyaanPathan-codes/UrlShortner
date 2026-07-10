package com.ayaan.UrlShortner.Entity;

import com.ayaan.UrlShortner.Entity.Enums.PlanType;
import com.ayaan.UrlShortner.Entity.Enums.Role;
import com.ayaan.UrlShortner.Entity.Enums.UserStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.cglib.core.Local;

import java.time.LocalDateTime;

@Entity
@Data
@AllArgsConstructor
@NoArgsConstructor
public class Users {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @Column(unique = true, nullable = false)
    private String email;

    private String passwordHash;

    @Enumerated(EnumType.STRING)
    private PlanType planType = PlanType.FREE;

    @Enumerated(EnumType.STRING)
    private Role role = Role.USER;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private UserStatus status = UserStatus.ACTIVE;

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

}
