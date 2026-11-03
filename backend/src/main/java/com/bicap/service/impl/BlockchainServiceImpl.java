package com.bicap.service.impl;

import com.bicap.blockchain.BlockchainClient;
import com.bicap.blockchain.BlockchainRecord;
import com.bicap.blockchain.BlockchainSubmission;
import com.bicap.common.enums.BlockchainVerificationStatus;
import com.bicap.common.enums.BlockchainTransactionStatus;
import com.bicap.dto.blockchain.ProductBatchBlockchainPayload;
import com.bicap.dto.response.blockchain.BlockchainTransactionResponse;
import com.bicap.dto.response.blockchain.BlockchainVerificationResponse;
import com.bicap.entity.BlockchainTransaction;
import com.bicap.entity.ProductBatch;
import com.bicap.entity.SeasonActivity;
import com.bicap.exception.BlockchainUnavailableException;
import com.bicap.exception.ResourceNotFoundException;
import com.bicap.mapper.BlockchainTransactionMapper;
import com.bicap.repository.BlockchainTransactionRepository;
import com.bicap.repository.ProductBatchRepository;
import com.bicap.repository.SeasonActivityRepository;
import com.bicap.service.BlockchainDataHasher;
import com.bicap.service.BlockchainTransactionPersistenceService;
import com.bicap.service.BlockchainService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;


@Service
@RequiredArgsConstructor
public class BlockchainServiceImpl implements BlockchainService {

    private static final String ENTITY_TYPE_PRODUCT_BATCH =
            "PRODUCT_BATCH";

    private final BlockchainTransactionRepository
            blockchainTransactionRepository;

    private final BlockchainClient blockchainClient;

    private final ProductBatchRepository productBatchRepository;

    private final SeasonActivityRepository seasonActivityRepository;

    private final BlockchainDataHasher blockchainDataHasher;

    private final BlockchainTransactionPersistenceService transactionPersistenceService;

    private final BlockchainTransactionMapper
            blockchainTransactionMapper;

    @Override
    public BlockchainTransactionResponse recordProductBatch(
            ProductBatchBlockchainPayload payload
    ) {

        // ==========================================================
        // P2.3
        // Serialize payload -> JSON
        // ==========================================================

        String hash = blockchainDataHasher.hashProductBatch(payload);

        var latest = blockchainTransactionRepository
                .findFirstByEntityTypeAndEntityIdOrderByCreatedAtDesc(
                        ENTITY_TYPE_PRODUCT_BATCH, payload.getBatchId());
        if (latest.isPresent() && latest.get().getStatus() != BlockchainTransactionStatus.FAILED) {
            return blockchainTransactionMapper.toResponse(latest.get());
        }

        // ==========================================================
        // P2.6 + P2.7
        // Tạo transaction với trạng thái PENDING
        // ==========================================================

        BlockchainTransaction transaction = transactionPersistenceService
                .createPending(ENTITY_TYPE_PRODUCT_BATCH, payload.getBatchId(), hash);

        // ==========================================================
        // P2.5
        // Gửi hash lên Blockchain
        // ==========================================================

        final BlockchainSubmission submission;
        try {
            submission = blockchainClient.submitTransaction(payload.getBatchId(), hash);
        } catch (Exception e) {

            // ======================================================
            // P2.7
            // Blockchain thất bại
            // ======================================================

            transaction = transactionPersistenceService.markFailed(
                    transaction.getTransactionId(), e.getMessage());
            return blockchainTransactionMapper.toResponse(transaction);
        }

        // If this update fails, leave the independently committed PENDING record
        // for reconciliation; never mislabel a confirmed chain write as FAILED.
        transaction = transactionPersistenceService
                .markSuccess(transaction.getTransactionId(), submission);

        // ==========================================================
        // P2.6
        // Lưu kết quả transaction
        // ==========================================================

        return blockchainTransactionMapper.toResponse(transaction);
    }

    @Override
    public BlockchainTransactionResponse getProductBatchTransaction(
            Long batchId
    ) {

        BlockchainTransaction transaction =
                blockchainTransactionRepository
                        .findByEntityTypeAndEntityId(
                                ENTITY_TYPE_PRODUCT_BATCH,
                                batchId
                        )
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Blockchain transaction not found."
                                )
                        );

        return blockchainTransactionMapper.toResponse(transaction);
    }

    @Override
    public BlockchainTransactionResponse recordSeasonActivity(Long activityId, String dataHash) {
        var latest = blockchainTransactionRepository
                .findFirstByEntityTypeAndEntityIdOrderByCreatedAtDesc("SEASON_ACTIVITY", activityId);
        if (latest.isPresent() && latest.get().getStatus() != BlockchainTransactionStatus.FAILED) {
            return blockchainTransactionMapper.toResponse(latest.get());
        }

        BlockchainTransaction pending = transactionPersistenceService
                .createPending("SEASON_ACTIVITY", activityId, dataHash);
        try {
            BlockchainSubmission submission = blockchainClient.submitSeasonActivity(activityId, dataHash);
            return blockchainTransactionMapper.toResponse(
                    transactionPersistenceService.markSuccess(pending.getTransactionId(), submission)
            );
        } catch (Exception e) {
            return blockchainTransactionMapper.toResponse(
                    transactionPersistenceService.markFailed(pending.getTransactionId(), e.getMessage())
            );
        }
    }

    @Override
    public BlockchainVerificationResponse verifyProductBatch(Long batchId) {
        ProductBatch batch = productBatchRepository.findByBatchId(batchId)
                .orElseThrow(() -> new ResourceNotFoundException("Product batch not found."));

        String databaseHash = blockchainDataHasher.hashProductBatch(
                blockchainDataHasher.productBatchPayload(batch)
        );
        return verify(
                ENTITY_TYPE_PRODUCT_BATCH,
                batchId,
                databaseHash,
                () -> blockchainClient.getProductBatch(batchId)
        );
    }

    @Override
    public Page<BlockchainTransactionResponse> getTransactionHistory(Pageable pageable) {
        return blockchainTransactionRepository.findAllByOrderByCreatedAtDesc(pageable)
                .map(blockchainTransactionMapper::toResponse);
    }

    @Override
    public BlockchainVerificationResponse verifySeasonActivity(Long activityId) {
        SeasonActivity activity = seasonActivityRepository.findByActivityId(activityId)
                .orElseThrow(() -> new ResourceNotFoundException("Season activity not found."));

        String databaseHash = blockchainDataHasher.hashSeasonActivity(activity);
        return verify(
                "SEASON_ACTIVITY",
                activityId,
                databaseHash,
                () -> blockchainClient.getSeasonActivity(activityId)
        );
    }

    private BlockchainVerificationResponse verify(
            String entityType,
            Long entityId,
            String databaseHash,
            BlockchainRecordReader recordReader
    ) {
        final BlockchainRecord blockchainRecord;
        try {
            blockchainRecord = recordReader.read();
        } catch (Exception e) {
            throw new BlockchainUnavailableException(
                    "Unable to read the blockchain record. Please try again later.", e
            );
        }

        if (!blockchainRecord.isRecorded()) {
            return BlockchainVerificationResponse.builder()
                    .entityType(entityType)
                    .entityId(entityId)
                    .databaseDataHash(databaseHash)
                    .status(BlockchainVerificationStatus.NOT_RECORDED)
                    .message("This record has not been recorded on blockchain.")
                    .build();
        }

        String blockchainHash = blockchainRecord.dataHash();
        boolean matches = databaseHash.equalsIgnoreCase(stripHexPrefix(blockchainHash));
        return BlockchainVerificationResponse.builder()
                .entityType(entityType)
                .entityId(entityId)
                .databaseDataHash(databaseHash)
                .blockchainDataHash(blockchainHash)
                .status(matches
                        ? BlockchainVerificationStatus.VERIFIED
                        : BlockchainVerificationStatus.MISMATCH)
                .message(matches
                        ? "Database data matches the hash recorded on blockchain."
                        : "Database data does not match the hash recorded on blockchain.")
                .build();
    }

    private String stripHexPrefix(String value) {
        return value != null && value.startsWith("0x") ? value.substring(2) : value;
    }

    @FunctionalInterface
    private interface BlockchainRecordReader {
        BlockchainRecord read();
    }
}
