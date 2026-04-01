package com.ecommerce.service;

import com.ecommerce.dto.request.OrderRequest;
import com.ecommerce.dto.response.OrderResponse;
import com.ecommerce.entity.*;
import com.ecommerce.exception.BadRequestException;
import com.ecommerce.exception.InsufficientStockException;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.CartRepository;
import com.ecommerce.repository.OrderRepository;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.repository.UserRepository;
import com.ecommerce.service.impl.OrderServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Order Service Tests")
class OrderServiceTest {

    @Mock private OrderRepository orderRepository;
    @Mock private UserRepository userRepository;
    @Mock private CartRepository cartRepository;
    @Mock private ProductRepository productRepository;
    @Mock private CartService cartService;

    @InjectMocks
    private OrderServiceImpl orderService;

    private User user;
    private Product product;
    private Cart cart;
    private CartItem cartItem;
    private OrderRequest orderRequest;

    @BeforeEach
    void setUp() {
        user = User.builder().id(1L).username("johndoe").email("john@example.com").build();

        product = Product.builder()
                .id(10L).name("Widget").price(new BigDecimal("19.99"))
                .stockQuantity(20).active(true).build();

        cartItem = CartItem.builder()
                .id(1L).product(product).quantity(3)
                .unitPrice(product.getPrice()).build();

        cart = Cart.builder()
                .id(1L).user(user)
                .cartItems(new ArrayList<>(List.of(cartItem)))
                .build();
        cartItem.setCart(cart);

        orderRequest = new OrderRequest();
        orderRequest.setShippingAddress("123 Main St");
        orderRequest.setShippingCity("Springfield");
        orderRequest.setShippingState("IL");
        orderRequest.setShippingZipCode("62701");
        orderRequest.setShippingCountry("US");
    }

    @Test
    @DisplayName("Should create order from cart successfully")
    void createOrder_Success() {
        Order savedOrder = Order.builder()
                .id(1L).orderNumber("ORD-ABC12345").user(user)
                .orderItems(new ArrayList<>())
                .totalPrice(new BigDecimal("59.97"))
                .orderStatus(Order.OrderStatus.PENDING)
                .paymentStatus(Order.PaymentStatus.PENDING)
                .shippingAddress("123 Main St").shippingCity("Springfield")
                .shippingState("IL").shippingZipCode("62701").shippingCountry("US")
                .build();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserIdWithItems(1L)).thenReturn(Optional.of(cart));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenReturn(savedOrder);
        doNothing().when(cartService).clearCart(1L);

        OrderResponse response = orderService.createOrder(1L, orderRequest);

        assertThat(response).isNotNull();
        assertThat(response.getOrderNumber()).isEqualTo("ORD-ABC12345");
        assertThat(response.getOrderStatus()).isEqualTo("PENDING");
        assertThat(response.getPaymentStatus()).isEqualTo("PENDING");

        // Verify stock was decremented
        assertThat(product.getStockQuantity()).isEqualTo(17); // 20 - 3
        verify(cartService).clearCart(1L);
    }

    @Test
    @DisplayName("Should throw BadRequestException when cart is empty")
    void createOrder_EmptyCart_Throws() {
        cart.getCartItems().clear();

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserIdWithItems(1L)).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> orderService.createOrder(1L, orderRequest))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("empty cart");
    }

    @Test
    @DisplayName("Should throw InsufficientStockException when stock is low")
    void createOrder_InsufficientStock_Throws() {
        product.setStockQuantity(1); // Only 1 in stock, but cart has 3

        when(userRepository.findById(1L)).thenReturn(Optional.of(user));
        when(cartRepository.findByUserIdWithItems(1L)).thenReturn(Optional.of(cart));

        assertThatThrownBy(() -> orderService.createOrder(1L, orderRequest))
                .isInstanceOf(InsufficientStockException.class);
    }

    @Test
    @DisplayName("Should cancel a pending order and restore stock")
    void cancelOrder_PendingOrder_Success() {
        OrderItem orderItem = com.ecommerce.entity.OrderItem.builder()
                .id(1L).product(product).productName("Widget")
                .quantity(3).unitPrice(product.getPrice()).build();

        Order order = Order.builder()
                .id(1L).orderNumber("ORD-TEST").user(user)
                .orderItems(new ArrayList<>(List.of(orderItem)))
                .orderStatus(Order.OrderStatus.PENDING)
                .paymentStatus(Order.PaymentStatus.PENDING)
                .totalPrice(new BigDecimal("59.97"))
                .build();

        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order));
        when(productRepository.save(any(Product.class))).thenReturn(product);
        when(orderRepository.save(any(Order.class))).thenReturn(order);

        int stockBefore = product.getStockQuantity();
        OrderResponse response = orderService.cancelOrder(1L, 1L);

        assertThat(response.getOrderStatus()).isEqualTo("CANCELLED");
        assertThat(product.getStockQuantity()).isEqualTo(stockBefore + 3); // stock restored
    }

    @Test
    @DisplayName("Should throw BadRequestException when cancelling a shipped order")
    void cancelOrder_ShippedOrder_Throws() {
        Order order = Order.builder()
                .id(1L).user(user).orderItems(new ArrayList<>())
                .orderStatus(Order.OrderStatus.SHIPPED)
                .totalPrice(BigDecimal.TEN).build();

        when(orderRepository.findByIdAndUserId(1L, 1L)).thenReturn(Optional.of(order));

        assertThatThrownBy(() -> orderService.cancelOrder(1L, 1L))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("shipped or delivered");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for unknown order")
    void getOrderById_NotFound_Throws() {
        when(orderRepository.findByIdAndUserId(999L, 1L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> orderService.getOrderById(1L, 999L))
                .isInstanceOf(ResourceNotFoundException.class);
    }
}