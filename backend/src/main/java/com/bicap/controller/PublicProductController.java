package com.bicap.controller;

import com.bicap.dto.response.product.ProductDetailResponse;
import com.bicap.dto.response.product.ProductSummaryResponse;
import com.bicap.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/public/products")
public class PublicProductController {

    private final ProductService productService;

    /**
     * Danh sách sản phẩm.
     */
    @GetMapping
    public ResponseEntity<Page<ProductSummaryResponse>> getProducts(

            @RequestParam(required = false)
            String keyword,

            @RequestParam(required = false)
            Long cropId,

            Pageable pageable
    ) {

        return ResponseEntity.ok(
                productService.getPublicProducts(
                        keyword,
                        cropId,
                        pageable
                )
        );
    }

    /**
     * Chi tiết sản phẩm.
     */
    @GetMapping("/{productId}")
    public ResponseEntity<ProductDetailResponse> getProduct(
            @PathVariable Long productId
    ) {

        return ResponseEntity.ok(
                productService.getPublicProduct(
                        productId
                )
        );
    }

}