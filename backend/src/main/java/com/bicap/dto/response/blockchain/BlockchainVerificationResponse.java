package com.bicap.dto.response.blockchain;

import com.bicap.common.enums.BlockchainVerificationStatus;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockchainVerificationResponse {

    private String entityType;

    private Long entityId;

    private String databaseDataHash;

    private String blockchainDataHash;

    private BlockchainVerificationStatus status;

    private String message;
}
