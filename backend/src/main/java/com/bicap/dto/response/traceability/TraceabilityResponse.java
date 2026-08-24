package com.bicap.dto.response.traceability;

import com.bicap.common.enums.ProductGrade;
import com.bicap.common.enums.BlockchainTransactionStatus;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraceabilityResponse {

    // =========================
    // PRODUCT BATCH
    // =========================

    private Long batchId;

    private String batchCode;

    private String productName;

    private String cropName;

    private String farmName;

    private ProductGrade grade;

    private BigDecimal quantity;

    private String unit;

    // =========================
    // FARMING SEASON
    // =========================

    private Long seasonId;

    private String seasonName;

    private LocalDate plantingDate;

    private LocalDate expectedHarvestDate;

    private LocalDate actualHarvestDate;

    // =========================
    // FARMING ACTIVITIES
    // =========================

    private List<TraceabilityActivityResponse> activities;

    // =========================
    // BLOCKCHAIN
    // =========================

    private Long transactionId;

    private String txHash;

    private BlockchainTransactionStatus blockchainStatus;

    private LocalDateTime blockchainCreatedAt;
}