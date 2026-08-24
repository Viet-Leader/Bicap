package com.bicap.service;

import com.bicap.dto.blockchain.ProductBatchBlockchainPayload;
import com.bicap.dto.response.blockchain.BlockchainTransactionResponse;

public interface BlockchainService {

    BlockchainTransactionResponse recordProductBatch(
            ProductBatchBlockchainPayload payload
    );

    BlockchainTransactionResponse getProductBatchTransaction(
            Long batchId
    );
}