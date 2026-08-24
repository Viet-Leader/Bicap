package com.bicap.repository;

import com.bicap.entity.ProductImage;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface ProductImageRepository extends JpaRepository<ProductImage, Long> {

   List<ProductImage> findByProductBatchBatchIdOrderByDisplayOrderAsc(Long batchId);

Optional<ProductImage> findByImageId(Long imageId);

Optional<ProductImage> findByImageIdAndProductBatchBatchId(
        Long imageId,
        Long batchId
);

Optional<ProductImage> findTopByProductBatchBatchIdOrderByDisplayOrderDesc(
        Long batchId
);

void deleteByProductBatchBatchId(Long batchId);

boolean existsByProductBatchBatchId(Long batchId);

Optional<ProductImage> findFirstByProductBatchProductProductIdOrderByDisplayOrderAsc(
        Long productId
);

}