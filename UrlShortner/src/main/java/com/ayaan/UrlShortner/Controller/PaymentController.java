package com.ayaan.UrlShortner.Controller;

import com.ayaan.UrlShortner.Security.CustomUserDetailsService;
import com.ayaan.UrlShortner.Service.PaymentService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;

@RestController
@RequestMapping("/api/payments")
public class PaymentController {

    private final PaymentService paymentService;

    private static final int PREMIUM_PRICE_PAISE = 499; // ₹499, fixed server-side

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @PostMapping("/create-order")
    public ResponseEntity<String> createOrder(@AuthenticationPrincipal CustomUserDetailsService principal) throws Exception {
        String orderJson = paymentService.createOrder(principal.getUser(), PREMIUM_PRICE_PAISE);
        return ResponseEntity.ok(orderJson);
    }

    @PostMapping("/webhook")
    public ResponseEntity<Void> webhook(HttpServletRequest request,
                                        @RequestHeader("X-Razorpay-Signature") String signature) throws Exception {
        String payload = readRawBody(request);
        paymentService.handleWebhook(payload, signature);
        return ResponseEntity.ok().build();
    }

    private String readRawBody(HttpServletRequest request) throws Exception {
        StringBuilder sb = new StringBuilder();
        try (BufferedReader reader = request.getReader()) {
            String line;
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        }
        return sb.toString();
    }
}