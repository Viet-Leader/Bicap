package com.bicap.repository;

import com.bicap.common.enums.AccountStatus;
import com.bicap.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /* =========================
            Farmer
       ========================= */

    /**
     * Get all products of a farm.
     */
    Page<Product> findByFarmFarmId(
            Long farmId,
            Pageable pageable
    );

    /**
     * Search products of a farm.
     */
    Page<Product> findByFarmFarmIdAndProductNameContainingIgnoreCase(
            Long farmId,
            String keyword,
            Pageable pageable
    );

    /**
     * Farmer only accesses products of their own farm.
     */
    Optional<Product> findByProductIdAndFarmFarmId(
            Long productId,
            Long farmId
    );

    /**
     * Check duplicate product name in the same farm and crop.
     */
    boolean existsByFarmFarmIdAndCropCropIdAndProductNameIgnoreCase(
            Long farmId,
            Long cropId,
            String productName
    );

    /* =========================
            Public
       ========================= */

    /**
     * Get all active products.
     */
    Page<Product> findByStatus(
            AccountStatus status,
            Pageable pageable
    );

    /**
     * Search active products.
     */
    Page<Product> findByStatusAndProductNameContainingIgnoreCase(
            AccountStatus status,
            String keyword,
            Pageable pageable
    );

    /**
     * Filter active products by crop.
     */
    Page<Product> findByStatusAndCropCropId(
            AccountStatus status,
            Long cropId,
            Pageable pageable
    );

    /**
     * Search active products by crop.
     */
    Page<Product> findByStatusAndCropCropIdAndProductNameContainingIgnoreCase(
            AccountStatus status,
            Long cropId,
            String keyword,
            Pageable pageable
    );

    /**
     * Public product detail.
     */
    Optional<Product> findByProductIdAndStatus(
            Long productId,
            AccountStatus status
    );

    /**
     * Internal lookup.
     */
    Optional<Product> findByProductId(Long productId);
    
    boolean existsByFarmFarmIdAndCropCropIdAndProductNameIgnoreCaseAndProductIdNot(
        Long farmId,
        Long cropId,
        String productName,
        Long productId
);

}