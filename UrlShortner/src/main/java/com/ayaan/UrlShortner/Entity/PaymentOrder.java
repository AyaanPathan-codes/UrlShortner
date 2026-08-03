package com.ayaan.UrlShortner.Entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_orders")
@Getter
@Setter
@NoArgsConstructor

public class PaymentOrder {

        private Long id;
        private String razorpayOrderId; // ← the join key between Razorpay and your DB

        @ManyToOne(fetch = FetchType.LAZY)
        @JoinColumn(name = "user_id")
        private Users user;                // ← who this order belongs to
        private Integer amountInPaise;
        private String status;             // CREATED / PAID / FAILED
        private LocalDateTime createdAt;
    }

