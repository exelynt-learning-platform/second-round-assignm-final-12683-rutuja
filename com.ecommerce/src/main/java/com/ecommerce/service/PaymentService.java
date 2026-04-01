package com.ecommerce.service;

import com.ecommerce.dto.request.PaymentRequest;
import com.ecommerce.dto.response.PaymentResponse;

public interface PaymentService {
    PaymentResponse createPaymentIntent(Long userId, PaymentRequest request);
    PaymentResponse confirmPayment(Long userId, Long orderId, String paymentIntentId);
    void handleWebhookEvent(String payload, String sigHeader);
}

