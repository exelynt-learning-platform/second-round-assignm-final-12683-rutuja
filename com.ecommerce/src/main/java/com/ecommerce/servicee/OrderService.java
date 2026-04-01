package com.ecommerce.servicee;

import com.ecommerce.dto.request.OrderRequest;
import com.ecommerce.dto.response.OrderResponse;
import com.ecommerce.dto.response.PagedResponse;
import jakarta.validation.Valid;

public interface OrderService {
    OrderResponse createOrder(Long userId, @Valid OrderRequest request);

    PagedResponse<OrderResponse> getUserOrders(Long userId, int page, int size);

    OrderResponse getOrderById(Long userId, Long orderId);

    OrderResponse updateOrderStatus(Long orderId, String status);

    OrderResponse cancelOrder(Long userId, Long orderId);
    PagedResponse<OrderResponse> getAllOrders(int page, int size);
}
