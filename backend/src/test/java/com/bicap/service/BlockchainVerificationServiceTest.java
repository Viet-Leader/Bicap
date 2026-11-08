package com.bicap.service;

import com.bicap.blockchain.BlockchainClient;
import com.bicap.blockchain.BlockchainRecord;
import com.bicap.common.enums.BlockchainVerificationStatus;
import com.bicap.dto.blockchain.ProductBatchBlockchainPayload;
import com.bicap.dto.response.blockchain.BlockchainVerificationResponse;
import com.bicap.entity.ProductBatch;
import com.bicap.entity.SeasonActivity;
import com.bicap.exception.BlockchainUnavailableException;
import com.bicap.exception.ResourceNotFoundException;
import com.bicap.mapper.BlockchainTransactionMapper;
import com.bicap.repository.BlockchainTransactionRepository;
import com.bicap.repository.ProductBatchRepository;
import com.bicap.repository.SeasonActivityRepository;
import com.bicap.service.impl.BlockchainServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigInteger;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockchainVerificationServiceTest {

    private static final String HASH =
            "aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa";

    @Mock private BlockchainTransactionRepository transactionRepository;
    @Mock private BlockchainClient blockchainClient;
    @Mock private ProductBatchRepository productBatchRepository;
    @Mock private SeasonActivityRepository seasonActivityRepository;
    @Mock private BlockchainDataHasher blockchainDataHasher;
    @Mock private BlockchainTransactionMapper transactionMapper;

    @InjectMocks private BlockchainServiceImpl blockchainService;

    @Test
    void verifyProductBatch_returnsVerifiedWhenHashesMatch() {
        ProductBatch batch = ProductBatch.builder().batchId(15L).build();
        ProductBatchBlockchainPayload payload = ProductBatchBlockchainPayload.builder().batchId(15L).build();
        when(productBatchRepository.findByBatchId(15L)).thenReturn(Optional.of(batch));
        when(blockchainDataHasher.productBatchPayload(batch)).thenReturn(payload);
        when(blockchainDataHasher.hashProductBatch(payload)).thenReturn(HASH);
        when(blockchainClient.getProductBatch(15L)).thenReturn(
                new BlockchainRecord("0x" + HASH, BigInteger.ONE, "0x0000000000000000000000000000000000000001")
        );

        BlockchainVerificationResponse result = blockchainService.verifyProductBatch(15L);

        assertEquals(BlockchainVerificationStatus.VERIFIED, result.getStatus());
        assertEquals(HASH, result.getDatabaseDataHash());
        assertEquals("0x" + HASH, result.getBlockchainDataHash());
    }

    @Test
    void verifySeasonActivity_returnsMismatchWhenHashesDiffer() {
        SeasonActivity activity = SeasonActivity.builder().activityId(7L).build();
        when(seasonActivityRepository.findByActivityId(7L)).thenReturn(Optional.of(activity));
        when(blockchainDataHasher.hashSeasonActivity(activity)).thenReturn(HASH);
        when(blockchainClient.getSeasonActivity(7L)).thenReturn(
                new BlockchainRecord(
                        "0xbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb",
                        BigInteger.ONE,
                        "0x0000000000000000000000000000000000000001"
                )
        );

        BlockchainVerificationResponse result = blockchainService.verifySeasonActivity(7L);

        assertEquals(BlockchainVerificationStatus.MISMATCH, result.getStatus());
        assertEquals("SEASON_ACTIVITY", result.getEntityType());
    }

    @Test
    void verifyProductBatch_returnsNotRecordedForZeroContractRecord() {
        ProductBatch batch = ProductBatch.builder().batchId(15L).build();
        ProductBatchBlockchainPayload payload = ProductBatchBlockchainPayload.builder().batchId(15L).build();
        when(productBatchRepository.findByBatchId(15L)).thenReturn(Optional.of(batch));
        when(blockchainDataHasher.productBatchPayload(batch)).thenReturn(payload);
        when(blockchainDataHasher.hashProductBatch(payload)).thenReturn(HASH);
        when(blockchainClient.getProductBatch(15L)).thenReturn(
                new BlockchainRecord(
                        "0x0000000000000000000000000000000000000000000000000000000000000000",
                        BigInteger.ZERO,
                        "0x0000000000000000000000000000000000000000"
                )
        );

        BlockchainVerificationResponse result = blockchainService.verifyProductBatch(15L);

        assertEquals(BlockchainVerificationStatus.NOT_RECORDED, result.getStatus());
        assertNull(result.getBlockchainDataHash());
    }

    @Test
    void verifySeasonActivity_wrapsBlockchainReadFailure() {
        SeasonActivity activity = SeasonActivity.builder().activityId(7L).build();
        when(seasonActivityRepository.findByActivityId(7L)).thenReturn(Optional.of(activity));
        when(blockchainDataHasher.hashSeasonActivity(activity)).thenReturn(HASH);
        when(blockchainClient.getSeasonActivity(7L)).thenThrow(new IllegalStateException("RPC unavailable"));

        assertThrows(BlockchainUnavailableException.class,
                () -> blockchainService.verifySeasonActivity(7L));
    }

    @Test
    void verifyProductBatch_throwsNotFoundWhenDatabaseRecordDoesNotExist() {
        when(productBatchRepository.findByBatchId(99L)).thenReturn(Optional.empty());

        assertThrows(ResourceNotFoundException.class,
                () -> blockchainService.verifyProductBatch(99L));
        verifyNoInteractions(blockchainClient);
    }
}
