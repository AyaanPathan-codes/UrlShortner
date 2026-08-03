package com.ayaan.UrlShortner.Repo;

import com.razorpay.Payment;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepo extends JpaRepository<Payment, String> {
}
