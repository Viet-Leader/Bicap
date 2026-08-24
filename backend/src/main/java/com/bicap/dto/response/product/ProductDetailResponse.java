package com.bicap.dto.response.product;

import java.util.List;

import com.bicap.dto.response.productBatch.ProductBatchSummaryResponse;

import lombok.*;


@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder
public class ProductDetailResponse {

    private Long productId;

    private String productName;

    private String cropName;

    private String farmName;

    private String unit;

    private String description;

    private List<ProductBatchSummaryResponse> batches;

}