package com.bicap.repository;

import com.bicap.common.enums.ProductBatchStatus;
import com.bicap.entity.ProductBatch;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductBatchRepository extends JpaRepository<ProductBatch, Long> {

    /* =========================
            Farmer
       ========================= */

    // Danh sách Batch của Product
    List<ProductBatch> findByProductProductId(Long productId);

    // Có phân trang
    Page<ProductBatch> findByProductProductId(
            Long productId,
            Pageable pageable
    );

    // Farmer xem đúng Batch thuộc Product
    Optional<ProductBatch> findByBatchIdAndProductProductId(
            Long batchId,
            Long productId
    );

    // Kiểm tra BatchCode
    boolean existsByBatchCode(String batchCode);

    /* =========================
            Public
       ========================= */

    // QR Code
    Optional<ProductBatch> findByQrCode(String qrCode);

    // Batch AVAILABLE của Product
    List<ProductBatch> findByProductProductIdAndStatus(
            Long productId,
            ProductBatchStatus status
    );

    // Danh sách Batch AVAILABLE
    List<ProductBatch> findByStatus(ProductBatchStatus status);

    /* =========================
            Dashboard
       ========================= */

    long countByStatus(ProductBatchStatus status);

    long countByProductProductId(Long productId);

    Optional<ProductBatch> findByBatchId(Long batchId);

    Page<ProductBatch> findByProductProductIdAndStatus(
        Long productId,
        ProductBatchStatus status,
        Pageable pageable
);
}