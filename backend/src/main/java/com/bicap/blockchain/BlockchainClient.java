package com.bicap.blockchain;

public interface BlockchainClient {

    String submitTransaction(
            Long batchId,
            String dataHash
    );
}