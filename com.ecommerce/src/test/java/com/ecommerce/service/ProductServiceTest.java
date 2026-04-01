package com.ecommerce.service;

import com.ecommerce.dto.request.ProductRequest;
import com.ecommerce.dto.response.PagedResponse;
import com.ecommerce.dto.response.ProductResponse;
import com.ecommerce.entity.Product;
import com.ecommerce.exception.ResourceNotFoundException;
import com.ecommerce.repository.ProductRepository;
import com.ecommerce.service.impl.ProductServiceImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("Product Service Tests")
class ProductServiceTest {

    @Mock private ProductRepository productRepository;

    @InjectMocks
    private ProductServiceImpl productService;

    private Product product;
    private ProductRequest productRequest;

    @BeforeEach
    void setUp() {
        product = Product.builder()
                .id(1L)
                .name("Laptop Pro")
                .description("High-performance laptop")
                .price(new BigDecimal("999.99"))
                .stockQuantity(15)
                .imageUrl("http://example.com/laptop.jpg")
                .category("Electronics")
                .active(true)
                .build();

        productRequest = new ProductRequest();
        productRequest.setName("Laptop Pro");
        productRequest.setDescription("High-performance laptop");
        productRequest.setPrice(new BigDecimal("999.99"));
        productRequest.setStockQuantity(15);
        productRequest.setImageUrl("http://example.com/laptop.jpg");
        productRequest.setCategory("Electronics");
    }

    @Test
    @DisplayName("Should create product successfully")
    void createProduct_Success() {
        when(productRepository.save(any(Product.class))).thenReturn(product);

        ProductResponse response = productService.createProduct(productRequest);

        assertThat(response).isNotNull();
        assertThat(response.getName()).isEqualTo("Laptop Pro");
        assertThat(response.getPrice()).isEqualByComparingTo("999.99");
        assertThat(response.getStockQuantity()).isEqualTo(15);
        verify(productRepository).save(any(Product.class));
    }

    @Test
    @DisplayName("Should retrieve product by id when active")
    void getProductById_ActiveProduct_ReturnsResponse() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        ProductResponse response = productService.getProductById(1L);

        assertThat(response).isNotNull();
        assertThat(response.getId()).isEqualTo(1L);
        assertThat(response.getName()).isEqualTo("Laptop Pro");
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException for inactive product")
    void getProductById_InactiveProduct_Throws() {
        product.setActive(false);
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));

        assertThatThrownBy(() -> productService.getProductById(1L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should throw ResourceNotFoundException when product not found")
    void getProductById_NotFound_Throws() {
        when(productRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> productService.getProductById(99L))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    @DisplayName("Should return paged list of active products")
    void getAllProducts_ReturnsPaged() {
        Pageable pageable = PageRequest.of(0, 10, Sort.by("createdAt").descending());
        Page<Product> page = new PageImpl<>(List.of(product), pageable, 1);

        when(productRepository.findByActiveTrue(any(Pageable.class))).thenReturn(page);

        PagedResponse<ProductResponse> response = productService.getAllProducts(0, 10, "createdAt", "desc");

        assertThat(response.getContent()).hasSize(1);
        assertThat(response.getTotalElements()).isEqualTo(1);
        assertThat(response.isFirst()).isTrue();
        assertThat(response.isLast()).isTrue();
    }

    @Test
    @DisplayName("Should update product fields")
    void updateProduct_Success() {
        productRequest.setName("Updated Laptop");
        productRequest.setPrice(new BigDecimal("1099.99"));

        Product updatedProduct = Product.builder()
                .id(1L).name("Updated Laptop").price(new BigDecimal("1099.99"))
                .stockQuantity(15).active(true).build();

        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(updatedProduct);

        ProductResponse response = productService.updateProduct(1L, productRequest);

        assertThat(response.getName()).isEqualTo("Updated Laptop");
        assertThat(response.getPrice()).isEqualByComparingTo("1099.99");
    }

    @Test
    @DisplayName("Should soft-delete product by setting active=false")
    void deleteProduct_SetsActiveFalse() {
        when(productRepository.findById(1L)).thenReturn(Optional.of(product));
        when(productRepository.save(any(Product.class))).thenReturn(product);

        productService.deleteProduct(1L);

        assertThat(product.isActive()).isFalse();
        verify(productRepository).save(product);
    }
}