package com.bicap.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bicap.dto.request.product.CreateProductRequest;
import com.bicap.dto.request.product.UpdateProductRequest;
import com.bicap.dto.response.product.ProductDetailResponse;
import com.bicap.dto.response.product.ProductSummaryResponse;
import com.bicap.service.ProductService;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;

@RestController
@RequiredArgsConstructor
@RequestMapping("/products")
public class ProductController {

    private final ProductService productService;

    /**
     * Danh sách sản phẩm của Farm hiện tại.
     */
    @GetMapping
    public ResponseEntity<Page<ProductSummaryResponse>> getMyProducts(
            @RequestParam(required = false)
            String keyword,
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                productService.getMyProducts(
                        keyword,
                        pageable
                )
        );
    }

    /**
     * Chi tiết sản phẩm.
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponse> getMyProduct(
            @PathVariable Long productId
    ) {

        return ResponseEntity.ok(
                productService.getMyProduct(productId)
        );
    }

    /**
     * Tạo sản phẩm.
     */
    @PostMapping
    public ResponseEntity<ProductDetailResponse> create(
            @Valid
            @RequestBody CreateProductRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        productService.create(request)
                );
    }

    /**
     * Cập nhật sản phẩm.
     */
    @PutMapping("/{productId}")
    public ResponseEntity<ProductDetailResponse> update(
            @PathVariable Long productId,
            @Valid
            @RequestBody UpdateProductRequest request
    ) {

        return ResponseEntity.ok(
                productService.update(
                        productId,
                        request
                )
        );
    }

    /**
     * Active / Inactive.
     */
    @PatchMapping("/{productId}/status")
    public ResponseEntity<ProductDetailResponse> changeStatus(
            @PathVariable Long productId
    ) {

        return ResponseEntity.ok(
                productService.changeStatus(productId)
        );
    }

}
