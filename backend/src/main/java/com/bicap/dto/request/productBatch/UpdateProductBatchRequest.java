package com.bicap.dto.request.productBatch;

import com.bicap.common.enums.ProductGrade;
import jakarta.validation.constraints.*;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateProductBatchRequest {

    @NotNull(message = "Grade is required")
    private ProductGrade grade;

    @NotNull(message = "Quantity is required")
    @Positive(message = "Quantity must be greater than 0")
    private BigDecimal quantity;

    @NotNull(message = "Unit price is required")
    @Positive(message = "Unit price must be greater than 0")
    private BigDecimal unitPrice;
}