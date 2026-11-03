package com.bicap.service;

import com.bicap.blockchain.BlockchainSubmission;
import com.bicap.common.enums.BlockchainTransactionStatus;
import com.bicap.entity.BlockchainTransaction;
import com.bicap.repository.BlockchainTransactionRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class BlockchainTransactionPersistenceService {

    private final BlockchainTransactionRepository repository;

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BlockchainTransaction createPending(String entityType, Long entityId, String dataHash) {
        return repository.save(BlockchainTransaction.builder()
                .entityType(entityType).entityId(entityId).dataHash(dataHash)
                .status(BlockchainTransactionStatus.PENDING).build());
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BlockchainTransaction markSuccess(Long id, BlockchainSubmission submission) {
        BlockchainTransaction tx = repository.findById(id).orElseThrow();
        tx.setTxHash(submission.txHash());
        tx.setBlockNumber(submission.blockNumber() == null ? null : submission.blockNumber().longValueExact());
        tx.setGasUsed(submission.gasUsed() == null ? null : submission.gasUsed().longValueExact());
        tx.setConfirmedAt(submission.confirmedAt());
        tx.setErrorMessage(null);
        tx.setStatus(BlockchainTransactionStatus.SUCCESS);
        return repository.save(tx);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public BlockchainTransaction markFailed(Long id, String error) {
        BlockchainTransaction tx = repository.findById(id).orElseThrow();
        tx.setStatus(BlockchainTransactionStatus.FAILED);
        tx.setErrorMessage(error);
        return repository.save(tx);
    }
}
