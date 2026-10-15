package com.bicap.repository;

import com.bicap.common.enums.AccountStatus;
import com.bicap.entity.Product;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ProductRepository extends JpaRepository<Product, Long> {

    /* =========================
            Farmer
       ========================= */

    /**
     * Get all products of a farm.
     */
        @EntityGraph(attributePaths = {"farm", "crop", "productBatches"})
        Page<Product> findByFarmFarmId(
            Long farmId,
            Pageable pageable
    );

    /**
     * Search products of a farm.
     */
        @EntityGraph(attributePaths = {"farm", "crop", "productBatches"})
        Page<Product> findByFarmFarmIdAndProductNameContainingIgnoreCase(
            Long farmId,
            String keyword,
            Pageable pageable
    );

    /**
     * Admin search across products from every farm.
     */
    Page<Product> findByProductNameContainingIgnoreCase(
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
        @EntityGraph(attributePaths = {"farm", "crop"})
        Page<Product> findByStatus(
            AccountStatus status,
            Pageable pageable
    );

    /**
     * Search active products.
     */
        @EntityGraph(attributePaths = {"farm", "crop"})
        Page<Product> findByStatusAndProductNameContainingIgnoreCase(
            AccountStatus status,
            String keyword,
            Pageable pageable
    );

    /**
     * Filter active products by crop.
     */
        @EntityGraph(attributePaths = {"farm", "crop"})
        Page<Product> findByStatusAndCropCropId(
            AccountStatus status,
            Long cropId,
            Pageable pageable
    );

    /**
     * Search active products by crop.
     */
        @EntityGraph(attributePaths = {"farm", "crop"})
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

                @EntityGraph(attributePaths = {"farm", "crop"})
                @Query("""
                                                select p
                                                from Product p
                                                where p.status = ?1
                                                        and exists (
                                                                        select b.batchId
                                                                        from ProductBatch b
                                                                        where b.product = p
                                                                                and b.status = com.bicap.common.enums.ProductBatchStatus.AVAILABLE
                                                                                and b.remainingQuantity > 0
                                                        )
                                                """)
    Page<Product> findPublicProductsWithAvailableBatch(
            AccountStatus productStatus,
            Pageable pageable
    );

                @EntityGraph(attributePaths = {"farm", "crop"})
                @Query("""
                                                select p
                                                from Product p
                                                where p.status = ?1
                                                        and lower(p.productName) like lower(concat('%', ?2, '%'))
                                                        and exists (
                                                                        select b.batchId
                                                                        from ProductBatch b
                                                                        where b.product = p
                                                                                and b.status = com.bicap.common.enums.ProductBatchStatus.AVAILABLE
                                                                                and b.remainingQuantity > 0
                                                        )
                                                """)
    Page<Product> searchPublicProductsWithAvailableBatch(
            AccountStatus productStatus,
            String keyword,
            Pageable pageable
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
