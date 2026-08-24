package com.bicap.dto.response.blockchain;

import com.bicap.common.enums.BlockchainTransactionStatus;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockchainTransactionResponse {

    private Long transactionId;

    private String entityType;

    private Long entityId;

    private String txHash;

    private BlockchainTransactionStatus status;

    private String errorMessage;

    private LocalDateTime createdAt;
}