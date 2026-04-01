package com.ecommerce.controller;

import com.ecommerce.dto.request.PaymentRequest;
import com.ecommerce.dto.response.ApiResponse;
import com.ecommerce.dto.response.PaymentResponse;
import com.ecommerce.service.PaymentService;
import com.ecommerce.util.SecurityUtils;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;
    private final SecurityUtils securityUtils;

    @PostMapping("/create-intent")
    public ResponseEntity<ApiResponse<PaymentResponse>> createPaymentIntent(
            @Valid @RequestBody PaymentRequest request) {
        Long userId = securityUtils.getCurrentUserId();
        PaymentResponse response = paymentService.createPaymentIntent(userId, request);
        return ResponseEntity.ok(ApiResponse.success("Payment intent created", response));
    }

    @PostMapping("/confirm/{orderId}")
    public ResponseEntity<ApiResponse<PaymentResponse>> confirmPayment(
            @PathVariable Long orderId,
            @RequestParam String paymentIntentId) {
        Long userId = securityUtils.getCurrentUserId();
        PaymentResponse response = paymentService.confirmPayment(userId, orderId, paymentIntentId);
        return ResponseEntity.ok(ApiResponse.success("Payment confirmed", response));
    }

    /**
     * Stripe webhook endpoint — must be publicly accessible (no auth).
     * Stripe sends signed POST requests here on payment events.
     */
    @PostMapping("/webhook")
    public ResponseEntity<String> handleWebhook(
            @RequestBody String payload,
            @RequestHeader("Stripe-Signature") String sigHeader) {
        paymentService.handleWebhookEvent(payload, sigHeader);
        return ResponseEntity.ok("Webhook processed");
    }
}