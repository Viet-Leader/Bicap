package com.bicap.repository;

import com.bicap.entity.BlockchainTransaction;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface BlockchainTransactionRepository
        extends JpaRepository<BlockchainTransaction, Long> {

    Optional<BlockchainTransaction> findByEntityTypeAndEntityId(
            String entityType,
            Long entityId
    );

    List<BlockchainTransaction> findByEntityTypeAndEntityIdOrderByCreatedAtDesc(
            String entityType,
            Long entityId
    );

    Optional<BlockchainTransaction> findByTxHash(String txHash);
}