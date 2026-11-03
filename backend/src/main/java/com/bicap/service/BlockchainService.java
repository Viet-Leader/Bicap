package com.bicap.service;

import com.bicap.dto.blockchain.ProductBatchBlockchainPayload;
import com.bicap.dto.response.blockchain.BlockchainTransactionResponse;
import com.bicap.dto.response.blockchain.BlockchainVerificationResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface BlockchainService {

    BlockchainTransactionResponse recordProductBatch(
            ProductBatchBlockchainPayload payload
    );

    BlockchainTransactionResponse getProductBatchTransaction(
            Long batchId
    );

    BlockchainTransactionResponse recordSeasonActivity(Long activityId, String dataHash);

    BlockchainVerificationResponse verifyProductBatch(Long batchId);

    BlockchainVerificationResponse verifySeasonActivity(Long activityId);

    Page<BlockchainTransactionResponse> getTransactionHistory(Pageable pageable);
}
