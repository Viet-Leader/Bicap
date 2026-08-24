package com.bicap.mapper;

import com.bicap.dto.blockchain.FarmingActivityBlockchainPayload;
import com.bicap.dto.response.blockchain.BlockchainTransactionResponse;
import com.bicap.entity.BlockchainTransaction;
import com.bicap.entity.SeasonActivity;

import org.springframework.stereotype.Component;

@Component
public class BlockchainTransactionMapper {

    public BlockchainTransactionResponse toResponse(
            BlockchainTransaction transaction
    ) {

        return BlockchainTransactionResponse.builder()
                .transactionId(transaction.getTransactionId())
                .entityType(transaction.getEntityType())
                .entityId(transaction.getEntityId())
                .txHash(transaction.getTxHash())
                .status(transaction.getStatus())
                .errorMessage(transaction.getErrorMessage())
                .createdAt(transaction.getCreatedAt())
                .build();
    }

    public FarmingActivityBlockchainPayload toActivityPayload(
            SeasonActivity activity
    ) {

        return FarmingActivityBlockchainPayload.builder()
                .activityId(activity.getActivityId())
                .activityTime(activity.getActivityTime())
                .activityType(
                        activity.getActivityType()
                                .getActivityName()
                )
                .description(activity.getDescription())
                .build();
    }
}