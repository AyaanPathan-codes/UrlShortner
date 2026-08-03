package com.ayaan.UrlShortner.Entity;

import jakarta.persistence.Entity;
import jakarta.persistence.Table;

import java.time.LocalDateTime;

@Entity
@Table(name = "payment_orders")
public class PaymentEntity {

        private Long id;
        private String razorpayOrderId;   // ← the join key between Razorpay and your DB
        private Users user;                // ← who this order belongs to
        private Integer amountInPaise;
        private String status;             // CREATED / PAID / FAILED
        private LocalDateTime createdAt;
    }
}
