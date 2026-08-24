package com.bicap.dto.request.cart;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UpdateCartItemRequest {

    @NotNull(message = "Quantity is required")
    @DecimalMin(value = "0.01", inclusive = true,
            message = "Quantity must be greater than 0")
    private BigDecimal quantity;

}