package com.bicap.service.impl;

import com.bicap.blockchain.BlockchainClient;
import com.bicap.common.enums.BlockchainTransactionStatus;
import com.bicap.dto.blockchain.ProductBatchBlockchainPayload;
import com.bicap.dto.response.blockchain.BlockchainTransactionResponse;
import com.bicap.entity.BlockchainTransaction;
import com.bicap.exception.ResourceNotFoundException;
import com.bicap.mapper.BlockchainTransactionMapper;
import com.bicap.repository.BlockchainTransactionRepository;
import com.bicap.service.BlockchainService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
@RequiredArgsConstructor
@Transactional
public class BlockchainServiceImpl implements BlockchainService {

    private static final String ENTITY_TYPE_PRODUCT_BATCH =
            "PRODUCT_BATCH";

    private final BlockchainTransactionRepository
            blockchainTransactionRepository;

    private final BlockchainClient blockchainClient;

    private final ObjectMapper objectMapper;

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

        String data = convertToJson(payload);

        // ==========================================================
        // P2.4
        // Generate SHA-256 hash
        // ==========================================================

        String hash = generateSha256(data);

        // ==========================================================
        // P2.6 + P2.7
        // Tạo transaction với trạng thái PENDING
        // ==========================================================

        BlockchainTransaction transaction =
                BlockchainTransaction.builder()
                        .entityType(ENTITY_TYPE_PRODUCT_BATCH)
                        .entityId(payload.getBatchId())
                        .status(BlockchainTransactionStatus.PENDING)
                        .build();

        transaction =
                blockchainTransactionRepository.save(transaction);

        // ==========================================================
        // P2.5
        // Gửi hash lên Blockchain
        // ==========================================================

        try {

            String txHash =
                    blockchainClient.submitTransaction(
                                                        payload.getBatchId(),
                                                        hash
                                                );

            transaction.setTxHash(txHash);

            transaction.setStatus(
                    BlockchainTransactionStatus.SUCCESS
            );

        } catch (Exception e) {

            // ======================================================
            // P2.7
            // Blockchain thất bại
            // ======================================================

            transaction.setStatus(
                    BlockchainTransactionStatus.FAILED
            );

            transaction.setErrorMessage(
                    e.getMessage()
            );
        }

        // ==========================================================
        // P2.6
        // Lưu kết quả transaction
        // ==========================================================

        transaction =
                blockchainTransactionRepository.save(transaction);

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

    // ==========================================================
    // P2.3
    // Serialize ProductBatch payload -> JSON
    // ==========================================================

    private String convertToJson(
            ProductBatchBlockchainPayload payload
    ) {

        try {

            return objectMapper.writeValueAsString(payload);

        } catch (JsonProcessingException e) {

            throw new IllegalStateException(
                    "Cannot serialize blockchain payload.",
                    e
            );
        }
    }

    // ==========================================================
    // P2.4
    // Generate SHA-256
    // ==========================================================

    private String generateSha256(String data) {

        try {

            MessageDigest digest =
                    MessageDigest.getInstance("SHA-256");

            byte[] hash =
                    digest.digest(
                            data.getBytes(StandardCharsets.UTF_8)
                    );

            StringBuilder hexString =
                    new StringBuilder();

            for (byte b : hash) {

                String hex =
                        Integer.toHexString(0xff & b);

                if (hex.length() == 1) {
                    hexString.append('0');
                }

                hexString.append(hex);
            }

            return hexString.toString();

        } catch (NoSuchAlgorithmException e) {

            throw new IllegalStateException(
                    "SHA-256 algorithm is not available.",
                    e
            );
        }
    }
}