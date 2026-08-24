package com.bicap.controller;

import com.bicap.dto.request.productBatch.CreateProductBatchRequest;
import com.bicap.dto.request.productBatch.UpdateProductBatchRequest;
import com.bicap.dto.response.blockchain.BlockchainTransactionResponse;
import com.bicap.dto.response.productBatch.ProductBatchDetailResponse;
import com.bicap.dto.response.productBatch.ProductBatchSummaryResponse;
import com.bicap.service.ProductBatchService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


@RestController
@RequiredArgsConstructor
@RequestMapping
public class ProductBatchController {

    private final ProductBatchService productBatchService;

    /**
     * Danh sách Batch của Product hiện tại.
     */
    @GetMapping("/products/{productId}/batches")
    public ResponseEntity<Page<ProductBatchSummaryResponse>> getByProduct(
            @PathVariable Long productId,
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                productBatchService.getByProduct(
                        productId,
                        pageable
                )
        );
    }

    /**
     * Chi tiết Batch.
     */
    @GetMapping("/product-batches/{batchId}")
    public ResponseEntity<ProductBatchDetailResponse> getById(
            @PathVariable Long batchId
    ) {

        return ResponseEntity.ok(
                productBatchService.getById(batchId)
        );
    }

    /**
     * Tạo Batch mới cho Product.
     */
    @PostMapping("/products/{productId}/batches")
    public ResponseEntity<ProductBatchDetailResponse> create(
            @PathVariable Long productId,
            @Valid
            @RequestBody CreateProductBatchRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        productBatchService.create(
                                productId,
                                request
                        )
                );
    }

    /**
     * Cập nhật Batch.
     */
    @PutMapping("/product-batches/{batchId}")
    public ResponseEntity<ProductBatchDetailResponse> update(
            @PathVariable Long batchId,
            @Valid
            @RequestBody UpdateProductBatchRequest request
    ) {

        return ResponseEntity.ok(
                productBatchService.update(
                        batchId,
                        request
                )
        );
    }

    /**
 * Ghi thông tin Product Batch lên Blockchain.
 */
@PostMapping("/product-batches/{batchId}/blockchain")
public ResponseEntity<BlockchainTransactionResponse> recordToBlockchain(
        @PathVariable Long batchId
) {

    return ResponseEntity.ok(
            productBatchService.recordToBlockchain(batchId)
    );
}
}