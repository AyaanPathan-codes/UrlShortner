package com.ayaan.UrlShortner.Dto;

import io.swagger.v3.oas.annotations.media.Schema;

@Schema(description = "Razorpay order creation response")
public record CreateOrderResponse(

        @Schema(
                description = "Unique Razorpay Order ID",
                example = "order_Q5YqKqR0xW8abc"
        )
        String orderId,

        @Schema(
                description = "Amount in paise",
                example = "49900"
        )
        Integer amount,

        @Schema(
                description = "Currency",
                example = "INR"
        )
        String currency,

        @Schema(
                description = "Razorpay public key",
                example = "rzp_test_xxxxxxxxx"
        )
        String key
) {}