package com.bicap.service;

import com.bicap.blockchain.BlockchainClient;
import com.bicap.common.enums.BlockchainTransactionStatus;
import com.bicap.dto.blockchain.FarmingActivityBlockchainPayload;
import com.bicap.dto.blockchain.ProductBatchBlockchainPayload;
import com.bicap.dto.response.blockchain.BlockchainTransactionResponse;
import com.bicap.entity.BlockchainTransaction;
import com.bicap.mapper.BlockchainTransactionMapper;
import com.bicap.repository.BlockchainTransactionRepository;
import com.bicap.service.impl.BlockchainServiceImpl;
import com.bicap.service.BlockchainDataHasher;
import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class BlockchainServiceTest {

    @Mock
    private BlockchainTransactionRepository blockchainTransactionRepository;

    @Mock
    private BlockchainClient blockchainClient;

    @Mock
    private BlockchainDataHasher blockchainDataHasher;

    @Mock
    private BlockchainTransactionPersistenceService transactionPersistenceService;

    @Mock
    private BlockchainTransactionMapper blockchainTransactionMapper;

    @InjectMocks
    private BlockchainServiceImpl blockchainService;

    // ==========================================================
    // TEST 01
    // ProductBatch -> JSON -> SHA-256 -> Blockchain
    // ==========================================================

    @Test
    void recordProductBatch_shouldGenerateHashAndSubmitTransaction()
            throws Exception {

        ProductBatchBlockchainPayload payload =
                ProductBatchBlockchainPayload.builder()
                        .batchId(15L)
                        .batchCode("BATCH-000015")
                        .productId(3L)
                        .farmId(2L)
                        .seasonId(7L)
                        .harvestDate(
                                LocalDate.of(2026, 8, 15)
                        )
                        .quantity(
                                new BigDecimal("500")
                        )
                        .activities(
        List.of(

                FarmingActivityBlockchainPayload
                        .builder()
                        .activityId(1L)
                        .activityTime(
                                LocalDateTime.of(
                                        2026,
                                        6,
                                        1,
                                        8,
                                        0
                                )
                        )
                        .activityType("PLANTING")
                        .description("Gieo trồng")
                        .build(),

                FarmingActivityBlockchainPayload
                        .builder()
                        .activityId(2L)
                        .activityTime(
                                LocalDateTime.of(
                                        2026,
                                        6,
                                        15,
                                        10,
                                        0
                                )
                        )
                        .activityType("FERTILIZING")
                        .description("Bón phân")
                        .build()
        )
)
                        .build();

        String json =
                """
                {
                    "batchId":15,
                    "batchCode":"BATCH-000015",
                    "productId":3,
                    "farmId":2,
                    "seasonId":7,
                    "harvestDate":"2026-08-15",
                    "quantity":500,
                    "activities":[
                        {
                            "activityId":1,
                            "activityTime":"2026-06-01T08:00:00",
                            "activityType":"PLANTING",
                            "description":"Gieo trồng"
                        },
                        {
                            "activityId":2,
                            "activityTime":"2026-06-15T10:00:00",
                            "activityType":"FERTILIZING",
                            "description":"Bón phân"
                        }
                    ]
                }
                """;

        when(blockchainDataHasher.hashProductBatch(payload))
                .thenReturn("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

        BlockchainTransaction transaction =
                BlockchainTransaction.builder()
                        .transactionId(1L)
                        .entityType("PRODUCT_BATCH")
                        .entityId(15L)
                        .status(
                                BlockchainTransactionStatus.PENDING
                        )
                        .build();

        when(blockchainTransactionRepository
                .findFirstByEntityTypeAndEntityIdOrderByCreatedAtDesc(anyString(), anyLong()))
                .thenReturn(Optional.empty());
        when(transactionPersistenceService.createPending(anyString(), anyLong(), anyString()))
                .thenReturn(transaction);
        when(transactionPersistenceService.markSuccess(anyLong(), any()))
                .thenReturn(transaction);

        when(
        blockchainClient.submitTransaction(
                anyLong(),
                anyString()
        )
).thenReturn(new com.bicap.blockchain.BlockchainSubmission("0xabc123", null, null, null));

        BlockchainTransactionResponse response =
                BlockchainTransactionResponse.builder()
                        .transactionId(1L)
                        .entityType("PRODUCT_BATCH")
                        .entityId(15L)
                        .txHash("0xabc123")
                        .status(
                                BlockchainTransactionStatus.SUCCESS
                        )
                        .build();

        when(
                blockchainTransactionMapper.toResponse(
                        any(BlockchainTransaction.class)
                )
        ).thenReturn(response);

        // ======================================================
        // Act
        // ======================================================

        BlockchainTransactionResponse result =
                blockchainService.recordProductBatch(payload);

        // ======================================================
        // Assert
        // ======================================================

        assertNotNull(result);

        assertEquals(
                BlockchainTransactionStatus.SUCCESS,
                result.getStatus()
        );

        assertEquals(
                "PRODUCT_BATCH",
                result.getEntityType()
        );

        assertEquals(
                15L,
                result.getEntityId()
        );

        assertEquals(
                "0xabc123",
                result.getTxHash()
        );

        // ======================================================
        // Verify canonical hash component
        // ======================================================

        verify(blockchainDataHasher)
                .hashProductBatch(payload);

        // ======================================================
        // Verify BlockchainClient
        // ======================================================

       ArgumentCaptor<Long> batchIdCaptor =
        ArgumentCaptor.forClass(Long.class);

        ArgumentCaptor<String> hashCaptor =
                ArgumentCaptor.forClass(String.class);

        verify(blockchainClient)
                .submitTransaction(
                        batchIdCaptor.capture(),
                        hashCaptor.capture()
                );

        Long submittedBatchId =
                batchIdCaptor.getValue();

        String hash =
                hashCaptor.getValue();

        assertEquals(
                15L,
                submittedBatchId
        );

        assertNotNull(hash);

        assertFalse(hash.isBlank());

        assertEquals(
                64,
                hash.length()
        );

        assertTrue(
                hash.matches("[0-9a-f]{64}")
        );

        // ======================================================
        // Verify Repository
        // ======================================================

        verify(transactionPersistenceService).createPending(
                eq("PRODUCT_BATCH"), eq(15L), anyString());
        verify(transactionPersistenceService).markSuccess(eq(1L), any());

        // ======================================================
        // Verify Mapper
        // ======================================================

        verify(
                blockchainTransactionMapper
        ).toResponse(
                any(BlockchainTransaction.class)
        );
    }

    // ==========================================================
    // TEST 02
    // Payload thay đổi -> Hash phải thay đổi
    // ==========================================================

    @Test
    void recordProductBatch_shouldGenerateDifferentHashWhenPayloadChanges()
            throws Exception {

        ProductBatchBlockchainPayload payload1 =
                ProductBatchBlockchainPayload.builder()
                        .batchId(15L)
                        .batchCode("BATCH-000015")
                        .productId(3L)
                        .farmId(2L)
                        .seasonId(7L)
                        .harvestDate(
                                LocalDate.of(2026, 8, 15)
                        )
                        .quantity(
                                new BigDecimal("500")
                        )
                        .activities(List.of())
                        .build();

        ProductBatchBlockchainPayload payload2 =
                ProductBatchBlockchainPayload.builder()
                        .batchId(15L)
                        .batchCode("BATCH-000015")
                        .productId(3L)
                        .farmId(2L)
                        .seasonId(7L)
                        .harvestDate(
                                LocalDate.of(2026, 8, 15)
                        )
                        .quantity(
                                new BigDecimal("501")
                        )
                        .activities(List.of())
                        .build();

        String json1 =
                """
                {
                    "batchId":15,
                    "batchCode":"BATCH-000015",
                    "productId":3,
                    "farmId":2,
                    "seasonId":7,
                    "harvestDate":"2026-08-15",
                    "quantity":500,
                    "activities":[]
                }
                """;

        String json2 =
                """
                {
                    "batchId":15,
                    "batchCode":"BATCH-000015",
                    "productId":3,
                    "farmId":2,
                    "seasonId":7,
                    "harvestDate":"2026-08-15",
                    "quantity":501,
                    "activities":[]
                }
                """;

        when(
                blockchainDataHasher.hashProductBatch(payload1)
        ).thenReturn("aaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaaa");

        when(
                blockchainDataHasher.hashProductBatch(payload2)
        ).thenReturn("bbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbbb");

        BlockchainTransaction transaction =
                BlockchainTransaction.builder()
                        .transactionId(1L)
                        .entityType("PRODUCT_BATCH")
                        .entityId(15L)
                        .status(
                                BlockchainTransactionStatus.PENDING
                        )
                        .build();

        when(blockchainTransactionRepository
                .findFirstByEntityTypeAndEntityIdOrderByCreatedAtDesc(anyString(), anyLong()))
                .thenReturn(Optional.empty());
        when(transactionPersistenceService.createPending(anyString(), anyLong(), anyString()))
                .thenReturn(transaction);
        when(transactionPersistenceService.markSuccess(anyLong(), any()))
                .thenReturn(transaction);

        when(
        blockchainClient.submitTransaction(
                anyLong(),
                anyString()
        )
).thenReturn(new com.bicap.blockchain.BlockchainSubmission("0xabc", null, null, null));

        BlockchainTransactionResponse response =
                BlockchainTransactionResponse.builder()
                        .transactionId(1L)
                        .entityType("PRODUCT_BATCH")
                        .entityId(15L)
                        .txHash("0xabc")
                        .status(
                                BlockchainTransactionStatus.SUCCESS
                        )
                        .build();

        when(
                blockchainTransactionMapper.toResponse(
                        any(BlockchainTransaction.class)
                )
        ).thenReturn(response);

        // ======================================================
        // Act
        // ======================================================

        blockchainService.recordProductBatch(payload1);

        blockchainService.recordProductBatch(payload2);

        // ======================================================
        // Capture hash
        // ======================================================

        ArgumentCaptor<String> hashCaptor =
        ArgumentCaptor.forClass(String.class);

        verify(
                blockchainClient,
                times(2)
        ).submitTransaction(
                anyLong(),
                hashCaptor.capture()
        );

        List<String> hashes =
                hashCaptor.getAllValues();

        // ======================================================
        // Assert
        // ======================================================

        assertEquals(
                2,
                hashes.size()
        );

        assertNotNull(
                hashes.get(0)
        );

        assertNotNull(
                hashes.get(1)
        );

        assertEquals(
                64,
                hashes.get(0).length()
        );

        assertEquals(
                64,
                hashes.get(1).length()
        );

        // Payload khác nhau => hash khác nhau
        assertNotEquals(
                hashes.get(0),
                hashes.get(1)
        );
    }
}
