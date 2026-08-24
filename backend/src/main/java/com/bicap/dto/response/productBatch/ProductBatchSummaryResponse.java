package com.bicap.dto.response.productBatch;

import com.bicap.common.enums.ProductBatchStatus;
import com.bicap.common.enums.ProductGrade;
import lombok.*;
import java.math.BigDecimal;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductBatchSummaryResponse {

    private Long batchId;

    private String batchCode;

    private ProductGrade grade;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private ProductBatchStatus status;

    private BigDecimal remainingQuantity;

}