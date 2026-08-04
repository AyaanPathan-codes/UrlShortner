package com.ayaan.UrlShortner.Repo;

import com.ayaan.UrlShortner.Entity.PaymentOrder;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PaymentRepo extends JpaRepository<PaymentOrder, Long> {
}
