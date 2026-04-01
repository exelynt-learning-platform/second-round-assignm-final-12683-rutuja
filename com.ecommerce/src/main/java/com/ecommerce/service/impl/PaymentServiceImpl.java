package com.ecommerce.service.impl;

import com.ecommerce.dto.request.PaymentRequest;
import com.ecommerce.dto.response.PaymentResponse;
import com.ecommerce.entity.Order;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.UnauthorizedException;
import com.ecommerce.exception.PaymentException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.service.PaymentService;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.Event;
import com.stripe.model.EventDataObjectDeserializer;
import com.stripe.model.PaymentIntent;
import com.stripe.model.StripeObject;
import com.stripe.net.Webhook;
import com.stripe.param.PaymentIntentCreateParams;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.Optional;

@Service
@RequiredArgsConstructor
@Slf4j
public class PaymentServiceImpl implements PaymentService {

    private final OrderRepository orderRepository;

    @Value("${stripe.webhook.secret}")
    private String webhookSecret;

    @Value("${stripe.currency:usd}")
    private String currency;

    @Override
    @Transactional
    public PaymentResponse createPaymentIntent(Long userId, PaymentRequest request) {
        Order order = orderRepository.findByIdAndUserId(request.getOrderId(), userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", request.getOrderId()));

        if (order.getPaymentStatus() == Order.PaymentStatus.PAID) {
            throw new BadRequestException("Order is already paid");
        }
        if (order.getOrderStatus() == Order.OrderStatus.CANCELLED) {
            throw new BadRequestException("Cannot process payment for a cancelled order");
        }

        try {
            // Convert to cents (Stripe uses smallest currency unit)
            long amountInCents = order.getTotalPrice()
                    .multiply(BigDecimal.valueOf(100))
                    .longValue();

            PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                    .setAmount(amountInCents)
                    .setCurrency(currency)
                    .setPaymentMethod(request.getPaymentMethodId())
                    .setConfirm(true)
                    .setReturnUrl("https://your-app.com/payment/return")
                    .putMetadata("orderId", order.getId().toString())
                    .putMetadata("orderNumber", order.getOrderNumber())
                    .putMetadata("userId", userId.toString())
                    .build();

            PaymentIntent paymentIntent = PaymentIntent.create(params);

            // Save payment intent ID to order
            order.setPaymentIntentId(paymentIntent.getId());

            if ("succeeded".equals(paymentIntent.getStatus())) {
                order.setPaymentStatus(Order.PaymentStatus.PAID);
                order.setOrderStatus(Order.OrderStatus.CONFIRMED);
                log.info("Payment succeeded for order: {}", order.getOrderNumber());
            } else if ("requires_action".equals(paymentIntent.getStatus())) {
                log.info("Payment requires additional action for order: {}", order.getOrderNumber());
            } else {
                order.setPaymentStatus(Order.PaymentStatus.FAILED);
                log.warn("Payment failed for order: {}", order.getOrderNumber());
            }

            orderRepository.save(order);

            return PaymentResponse.builder()
                    .paymentIntentId(paymentIntent.getId())
                    .clientSecret(paymentIntent.getClientSecret())
                    .status(paymentIntent.getStatus())
                    .orderId(order.getId())
                    .orderNumber(order.getOrderNumber())
                    .message(buildPaymentMessage(paymentIntent.getStatus()))
                    .build();

        } catch (StripeException e) {
            log.error("Stripe payment error for order {}: {}", order.getOrderNumber(), e.getMessage());
            order.setPaymentStatus(Order.PaymentStatus.FAILED);
            orderRepository.save(order);
            throw new PaymentException("Payment processing failed: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public PaymentResponse confirmPayment(Long userId, Long orderId, String paymentIntentId) {
        Order order = orderRepository.findByIdAndUserId(orderId, userId)
                .orElseThrow(() -> new ResourceNotFoundException("Order", "id", orderId));

        // CRITICAL: Comprehensive payment validation to prevent fraud
        // 1. Verify paymentIntentId matches order
        if (order.getPaymentIntentId() != null && !order.getPaymentIntentId().equals(paymentIntentId)) {
            throw new UnauthorizedException("Payment intent does not match this order. Potential payment hijacking attempt.");
        }
        
        // 2. Ensure order is in valid state for payment
        if (order.getOrderStatus() == Order.OrderStatus.CANCELLED) {
            throw new BadRequestException("Cannot process payment for a cancelled order");
        }
        
        // 3. Prevent duplicate payment processing
        if (order.getPaymentStatus() == Order.PaymentStatus.PAID) {
            throw new BadRequestException("This order has already been paid");
        }

        try {
            PaymentIntent paymentIntent = PaymentIntent.retrieve(paymentIntentId);

            // 4. Additional validation: ensure payment metadata matches order
            String metadataOrderId = paymentIntent.getMetadata().get("orderId");
            if (metadataOrderId == null || !metadataOrderId.equals(orderId.toString())) {
                throw new UnauthorizedException("Payment intent metadata does not match order. Potential fraud detected.");
            }

            if ("succeeded".equals(paymentIntent.getStatus())) {
                order.setPaymentStatus(Order.PaymentStatus.PAID);
                order.setOrderStatus(Order.OrderStatus.CONFIRMED);
                order.setPaymentIntentId(paymentIntentId);
                orderRepository.save(order);
                log.info("Payment confirmed for order: {} (user: {})", order.getOrderNumber(), userId);
            }

            return PaymentResponse.builder()
                    .paymentIntentId(paymentIntent.getId())
                    .status(paymentIntent.getStatus())
                    .orderId(order.getId())
                    .orderNumber(order.getOrderNumber())
                    .message(buildPaymentMessage(paymentIntent.getStatus()))
                    .build();

        } catch (StripeException e) {
            log.error("Stripe exception during payment confirmation: {}", e.getMessage());
            throw new PaymentException("Failed to confirm payment: " + e.getMessage());
        }
    }

    @Override
    @Transactional
    public void handleWebhookEvent(String payload, String sigHeader) {
        Event event;
        try {
            event = Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException e) {
            log.error("Webhook signature verification failed: {}", e.getMessage());
            throw new BadRequestException("Invalid webhook signature");
        }

        log.info("Received Stripe webhook event: {}", event.getType());

        EventDataObjectDeserializer dataObjectDeserializer = event.getDataObjectDeserializer();
        Optional<StripeObject> stripeObject = dataObjectDeserializer.getObject();

        switch (event.getType()) {
            case "payment_intent.succeeded" -> {
                stripeObject.ifPresent(obj -> {
                    PaymentIntent paymentIntent = (PaymentIntent) obj;
                    handlePaymentSuccess(paymentIntent);
                });
            }
            case "payment_intent.payment_failed" -> {
                stripeObject.ifPresent(obj -> {
                    PaymentIntent paymentIntent = (PaymentIntent) obj;
                    handlePaymentFailure(paymentIntent);
                });
            }
            default -> log.info("Unhandled Stripe event type: {}", event.getType());
        }
    }

    private void handlePaymentSuccess(PaymentIntent paymentIntent) {
        orderRepository.findByPaymentIntentId(paymentIntent.getId()).ifPresent(order -> {
            order.setPaymentStatus(Order.PaymentStatus.PAID);
            order.setOrderStatus(Order.OrderStatus.CONFIRMED);
            orderRepository.save(order);
            log.info("Webhook: Payment succeeded for order {}", order.getOrderNumber());
        });
    }

    private void handlePaymentFailure(PaymentIntent paymentIntent) {
        orderRepository.findByPaymentIntentId(paymentIntent.getId()).ifPresent(order -> {
            order.setPaymentStatus(Order.PaymentStatus.FAILED);
            orderRepository.save(order);
            log.warn("Webhook: Payment failed for order {}", order.getOrderNumber());
        });
    }

    private String buildPaymentMessage(String status) {
        return switch (status) {
            case "succeeded" -> "Payment processed successfully";
            case "requires_action" -> "Additional authentication required";
            case "requires_payment_method" -> "Payment method failed, please try another";
            default -> "Payment status: " + status;
        };
    }
}