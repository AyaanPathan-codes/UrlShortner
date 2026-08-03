package com.ayaan.UrlShortner.Repo;

import com.ayaan.UrlShortner.Entity.PaymentOrder;
import com.ayaan.UrlShortner.Entity.Users;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface PaymentOrderRepo extends JpaRepository<PaymentOrder, Long> {
    Optional<PaymentOrder> findByRazorpayOrderId(String razorpayOrderId);
    List<PaymentOrder> findByUser(Users user);
}