package com.bicap.repository;

import com.bicap.entity.BlockchainTransaction;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

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

    Optional<BlockchainTransaction> findFirstByEntityTypeAndEntityIdOrderByCreatedAtDesc(
            String entityType, Long entityId);

    Page<BlockchainTransaction> findAllByOrderByCreatedAtDesc(Pageable pageable);
}
