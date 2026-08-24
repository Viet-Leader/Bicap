package com.bicap.repository;

import com.bicap.entity.Farm;
import com.bicap.entity.Product;

import java.util.Optional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

public interface FarmRepository extends JpaRepository<Farm, Long> {
    boolean existsByBusinessLicense(String businessLicense);

    Optional<Farm> findByAccount_AccountId(Long accountId);

    @Query("""
    SELECT p
    FROM Product p
    WHERE p.farm.farmId = :farmId
    AND (
        LOWER(p.productName) LIKE LOWER(CONCAT('%', :keyword, '%'))
        OR LOWER(p.crop.cropName) LIKE LOWER(CONCAT('%', :keyword, '%'))
    )
    """)
    Page<Product> searchMyProducts(
            Long farmId,
            String keyword,
            Pageable pageable
    );
}