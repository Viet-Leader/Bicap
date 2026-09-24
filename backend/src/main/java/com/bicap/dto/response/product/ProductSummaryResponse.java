package com.bicap.dto.response.product;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductSummaryResponse {

    private Long productId;

    private String productName;

    private String cropName;

    private String farmName;

    private Long farmId;

    private String unit;

    private BigDecimal remainingQuantity;

    private String thumbnail;

    private Long batchId;

    private String batchCode;

    private String grade;

    private BigDecimal unitPrice;

}
