package com.bicap.dto.response.blockchain;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BlockchainVerificationResponse {

    private Long batchId;

    private boolean verified;

    private String txHash;

    private String message;
}