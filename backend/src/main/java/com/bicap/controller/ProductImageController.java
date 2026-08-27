package com.bicap.controller;

import com.bicap.dto.request.productImage.ReorderProductImageRequest;
import com.bicap.dto.response.productImage.ProductImageResponse;
import com.bicap.service.ProductImageService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/product-images")
public class ProductImageController {

    private final ProductImageService productImageService;

    /**
     * Upload nhiều ảnh cho Batch.
     */
    @PostMapping("/batch/{batchId}")
    public ResponseEntity<List<ProductImageResponse>> upload(
            @PathVariable Long batchId,
            @RequestParam("files")
            List<MultipartFile> files
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        productImageService.upload(
                                batchId,
                                files
                        )
                );
    }

    /**
     * Danh sách ảnh của Batch.
     */
    @GetMapping("/batch/{batchId}")
    public ResponseEntity<List<ProductImageResponse>> getByBatch(
            @PathVariable Long batchId
    ) {

        return ResponseEntity.ok(
                productImageService.getByBatch(batchId)
        );
    }

    /**
     * Sắp xếp lại thứ tự ảnh.
     */
    @PatchMapping("/batch/{batchId}/reorder")
    public ResponseEntity<List<ProductImageResponse>> reorder(
            @PathVariable Long batchId,
            @Valid
            @RequestBody
            ReorderProductImageRequest request
    ) {

        return ResponseEntity.ok(
                productImageService.reorder(
                        batchId,
                        request
                )
        );
    }

    /**
     * Xóa ảnh.
     */
    @DeleteMapping("/{imageId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long imageId
    ) {

        productImageService.delete(imageId);

        return ResponseEntity.noContent().build();
    }

}