package com.bicap.dto.response.order;

import com.bicap.common.enums.OrderStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OrderResponse {

    private Long orderId;

    private Long retailerId;

    private String retailerName;

    private Long farmId;

    private String farmName;

    private BigDecimal depositAmount;

    private BigDecimal totalAmount;

    private OrderStatus status;

    private LocalDateTime createdAt;

    private List<OrderDetailResponse> orderDetails;

}