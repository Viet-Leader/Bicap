package com.bicap.blockchain;

public interface BlockchainClient {

    /**
     * Gửi giao dịch lên blockchain cho ProductBatch.
     */
    String submitTransaction(
            Long batchId,
            String dataHash
    );

    /**
     * Gửi giao dịch lên blockchain cho SeasonActivity.
     */
    String submitSeasonActivity(
            Long activityId,
            String dataHash
    );
}