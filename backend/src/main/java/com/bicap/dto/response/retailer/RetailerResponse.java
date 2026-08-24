package com.bicap.dto.response.retailer;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class RetailerResponse {

    private Long retailerId;

    private String retailerName;

    private String address;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}