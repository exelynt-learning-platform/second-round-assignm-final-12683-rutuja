package com.ecommerce.repository;

import com.ecommerce.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ProductRepository extends JpaRepository<Product, Long> {

    @Query("SELECT p FROM Product p WHERE p.active = true ORDER BY p.createdAt DESC")
    Page<Product> findByActiveTrue(Pageable pageable);

    Page<Product> findByCategoryAndActiveTrue(String category, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Product p WHERE p.active = true AND " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')) OR " +
            "LOWER(p.description) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> searchProducts(@Param("keyword") String keyword, Pageable pageable);

    @Query("SELECT DISTINCT p FROM Product p WHERE p.active = true AND " +
            "(:category IS NULL OR p.category = :category) AND " +
            "(LOWER(p.name) LIKE LOWER(CONCAT('%', :keyword, '%')))")
    Page<Product> findByFilters(@Param("keyword") String keyword,
                                @Param("category") String category,
                                Pageable pageable);

    /**
     * Find products by IDs for batch operations (prevents N+1 query)
     */
    List<Product> findAllById(Iterable<Long> ids);

    /**
     * Batch decrement stock for multiple products (optimized single query)
     * CRITICAL: This prevents N+1 query problem by using native bulk update
     */
    @Modifying
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity - :quantity WHERE p.id = :productId")
    void decrementStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);

    /**
     * Batch increment stock for multiple products (e.g., on order cancellation)
     */
    @Modifying
    @Query("UPDATE Product p SET p.stockQuantity = p.stockQuantity + :quantity WHERE p.id = :productId")
    void incrementStock(@Param("productId") Long productId, @Param("quantity") Integer quantity);
}
