package com.bicap.dto.response.productBatch;

import com.bicap.common.enums.ProductBatchStatus;
import com.bicap.common.enums.ProductGrade;
import com.bicap.dto.response.productImage.ProductImageResponse;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductBatchDetailResponse {

    private Long batchId;

    private String batchCode;

    private String productName;

    private String cropName;

    private String farmName;

    private String seasonName;

    private ProductGrade grade;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private String qrCode;

    private ProductBatchStatus status;

    private LocalDateTime createdAt;

    private BigDecimal remainingQuantity;

    private List<ProductImageResponse> images;
}