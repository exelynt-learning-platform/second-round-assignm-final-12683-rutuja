package com.ecommerce.servicee;

import com.ecommerce.dto.request.CartItemRequest;
import com.ecommerce.dto.response.CartResponse;

public interface CartService {
    CartResponse getCart(Long userId);
    CartResponse addItemToCart(Long userId, CartItemRequest request);
    CartResponse updateCartItem(Long userId, Long cartItemId, Integer quantity);
    CartResponse removeItemFromCart(Long userId, Long cartItemId);
    void clearCart(Long userId);
}