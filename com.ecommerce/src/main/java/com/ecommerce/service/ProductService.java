package com.ecommerce.service;

import com.ecommerce.dto.request.ProductRequest;
import com.ecommerce.dto.response.PagedResponse;
import com.ecommerce.dto.response.ProductResponse;

public interface ProductService {
    ProductResponse createProduct(ProductRequest request);
    ProductResponse getProductById(Long id);
    PagedResponse<ProductResponse> getAllProducts(int page, int size, String sortBy, String sortDir);
    PagedResponse<ProductResponse> searchProducts(String keyword, String category, int page, int size);
    ProductResponse updateProduct(Long id, ProductRequest request);
    void deleteProduct(Long id);
}

