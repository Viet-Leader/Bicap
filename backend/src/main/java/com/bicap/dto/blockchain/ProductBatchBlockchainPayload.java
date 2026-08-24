package com.bicap.dto.blockchain;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductBatchBlockchainPayload {

    private Long batchId;

    private String batchCode;

    private Long productId;

    private Long farmId;

    private Long seasonId;

    private LocalDate harvestDate;

    private BigDecimal quantity;

    private List<FarmingActivityBlockchainPayload> activities;
}