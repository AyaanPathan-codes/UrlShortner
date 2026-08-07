package com.ayaan.UrlShortner.Controller;

import com.ayaan.UrlShortner.Dto.CreateOrderResponse;
import com.ayaan.UrlShortner.Entity.CustomUserDetails;
import com.ayaan.UrlShortner.Service.PaymentService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.io.BufferedReader;

@RestController
@RequestMapping("/api/payments")

@Tag(
        name = "Payments",
        description = """
                Premium Subscription APIs powered by Razorpay.

                Features

                • Secure Razorpay Order Creation

                • Webhook Verification

                • Automatic Premium Upgrade

                • Server-side Amount Validation

                • Signature Verification

                • Replay Attack Protection
                """
)
public class PaymentController {

    private final PaymentService paymentService;

    private static final int PREMIUM_PRICE_PAISE = 49900; // ₹499, fixed server-side

    public PaymentController(PaymentService paymentService) {
        this.paymentService = paymentService;
    }

    @Operation(
            summary = "Create Premium Subscription Order",
            description = """
                Creates a Razorpay order for purchasing the Premium subscription.

                Features

                • Requires JWT Authentication

                • Amount is fixed on the server (₹499)

                • Prevents client-side price manipulation

                • Stores Razorpay Order before payment

                Payment Flow

                Login
                    ↓
                Create Order
                    ↓
                Razorpay Checkout
                    ↓
                Webhook Verification
                    ↓
                User upgraded to PREMIUM
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Order created successfully"),
            @ApiResponse(responseCode = "401", description = "Authentication required"),
            @ApiResponse(responseCode = "403", description = "User already has Premium"),
            @ApiResponse(responseCode = "500", description = "Unable to create Razorpay order")
    })
    @SecurityRequirement(name = "Bearer Authentication")
    @PostMapping("/create-order")
    public ResponseEntity<CreateOrderResponse> createOrder(
            @AuthenticationPrincipal CustomUserDetails principal) throws Exception {

        return ResponseEntity.ok(
                paymentService.createOrder(
                        principal.getUser(),
                        PREMIUM_PRICE_PAISE
                )
        );
    }


    @Operation(
            summary = "Razorpay Webhook",
            description = """
                Receives webhook events from Razorpay.

                Used for

                • Payment Success

                • Payment Failure

                • Signature Verification

                • Automatic Premium Upgrade

                This endpoint is intended to be called ONLY by Razorpay.
                """
    )
    @ApiResponses({
            @ApiResponse(responseCode = "200", description = "Webhook processed successfully"),
            @ApiResponse(responseCode = "400", description = "Invalid webhook payload"),
            @ApiResponse(responseCode = "401", description = "Invalid Razorpay signature")
    })
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