package com.bicap.blockchain;

public interface BlockchainClient {

    /**
     * Sends a ProductBatch hash to the blockchain.
     */
    BlockchainSubmission submitTransaction(Long batchId, String dataHash);

    /**
     * Sends a SeasonActivity hash to the blockchain.
     */
    BlockchainSubmission submitSeasonActivity(Long activityId, String dataHash);

    /**
     * Reads the latest ProductBatch record stored by the smart contract.
     */
    BlockchainRecord getProductBatch(Long batchId);

    /**
     * Reads the latest SeasonActivity record stored by the smart contract.
     */
    BlockchainRecord getSeasonActivity(Long activityId);
}
