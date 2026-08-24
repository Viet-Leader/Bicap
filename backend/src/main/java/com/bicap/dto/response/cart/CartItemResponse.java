package com.bicap.dto.response.cart;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CartItemResponse {

    private Long cartItemId;

    private Long batchId;

    private String batchCode;

    private String productName;

    private String grade;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private BigDecimal subTotal;

    private BigDecimal remainingQuantity;

}