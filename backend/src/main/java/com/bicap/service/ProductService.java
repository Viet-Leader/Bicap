package com.bicap.service;

import com.bicap.dto.request.product.CreateProductRequest;
import com.bicap.dto.request.product.UpdateProductRequest;
import com.bicap.dto.response.product.ProductDetailResponse;
import com.bicap.dto.response.product.ProductSummaryResponse;


import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ProductService {

    /* =========================
            Farmer
       ========================= */

    /**
     * Get products of current farmer.
     * Support search by keyword.
     */
    Page<ProductSummaryResponse> getMyProducts(
            String keyword,
            Pageable pageable
    );

    /**
     * Get product detail of current farmer.
     */
    ProductDetailResponse getMyProduct(Long productId);

    /**
     * Create product.
     */
    ProductDetailResponse create(CreateProductRequest request);

    /**
     * Update product.
     */
    ProductDetailResponse update(
            Long productId,
            UpdateProductRequest request
    );

    /**
     * Active / Inactive product.
     */
    ProductDetailResponse changeStatus(Long productId);

    /* =========================
             Public
       ========================= */

    /**
     * Get public products.
     * Support search and crop filter.
     */
    Page<ProductSummaryResponse> getPublicProducts(
            String keyword,
            Long cropId,
            Pageable pageable
    );

    /**
     * Get public product detail.
     */
    ProductDetailResponse getPublicProduct(Long productId);

}