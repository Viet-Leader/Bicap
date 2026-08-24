package com.bicap.controller;

import com.bicap.dto.response.productBatch.ProductBatchDetailResponse;
import com.bicap.dto.response.productBatch.ProductBatchSummaryResponse;
import com.bicap.service.ProductBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/public/product-batches")
public class PublicProductBatchController {

    private final ProductBatchService productBatchService;

    /**
     * Danh sách Batch công khai của Product.
     */
    @GetMapping("/product/{productId}")
    public ResponseEntity<Page<ProductBatchSummaryResponse>> getByProduct(
            @PathVariable Long productId,
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                productBatchService.getPublicByProduct(
                        productId,
                        pageable
                )
        );
    }

    /**
     * Tra cứu Batch bằng QR Code.
     */
    @GetMapping("/qr/{qrCode}")
    public ResponseEntity<ProductBatchDetailResponse> getByQrCode(
            @PathVariable String qrCode
    ) {

        return ResponseEntity.ok(
                productBatchService.getByQrCode(qrCode)
        );
    }
}