package com.bicap.dto.response.order;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderDetailResponse {

    private Long detailId;

    private Long batchId;

    private String batchCode;

    private String productName;

    private String grade;

    private BigDecimal quantity;

    private BigDecimal unitPrice;

    private BigDecimal subTotal;

}