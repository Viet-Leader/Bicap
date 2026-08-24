package com.bicap.service;

import com.bicap.blockchain.BlockchainClient;
import com.bicap.common.enums.BlockchainTransactionStatus;
import com.bicap.common.enums.ProductBatchStatus;
import com.bicap.dto.blockchain.ProductBatchBlockchainPayload;
import com.bicap.dto.response.blockchain.BlockchainTransactionResponse;
import com.bicap.dto.response.productBatch.ProductBatchDetailResponse;
import com.bicap.entity.Farm;
import com.bicap.entity.FarmingSeason;
import com.bicap.entity.Product;
import com.bicap.entity.ProductBatch;
import com.bicap.exception.BadRequestException;
import com.bicap.exception.ResourceNotFoundException;
import com.bicap.generator.ProductBatchCodeGenerator;
import com.bicap.generator.QrCodeGenerator;
import com.bicap.mapper.BlockchainTransactionMapper;
import com.bicap.mapper.ProductBatchMapper;
import com.bicap.repository.FarmRepository;
import com.bicap.repository.FarmingSeasonRepository;
import com.bicap.repository.ProductBatchRepository;
import com.bicap.repository.ProductRepository;
import com.bicap.security.SecurityUtils;
import com.bicap.service.impl.ProductBatchServiceImpl;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProductBatchServiceTest {

    @Mock
    private ProductBatchRepository productBatchRepository;

    @Mock
    private ProductRepository productRepository;

    @Mock
    private FarmingSeasonRepository farmingSeasonRepository;

    @Mock
    private FarmRepository farmRepository;

    @Mock
    private ProductBatchMapper productBatchMapper;

    @Mock
    private ProductBatchCodeGenerator batchCodeGenerator;

    @Mock
    private QrCodeGenerator qrCodeGenerator;

    @Mock
    private BlockchainService blockchainService;

    @Mock
    private BlockchainTransactionMapper blockchainTransactionMapper;

    @Mock
    private BlockchainClient blockchainClient;

    @InjectMocks
    private ProductBatchServiceImpl productBatchService;


    // ==========================================================
    // TEST 01
    // actualHarvestDate != null
    // -> Cho phép ghi ProductBatch lên Blockchain
    // ==========================================================

 @Test
void recordToBlockchain_shouldRecordSuccessfully() {

    Long batchId = 15L;
    Long accountId = 100L;

    Farm farm = Farm.builder()
            .farmId(2L)
            .build();

    Product product = Product.builder()
            .productId(3L)
            .farm(farm)
            .build();

    FarmingSeason season =
            FarmingSeason.builder()
                    .seasonId(7L)
                    .actualHarvestDate(
                            LocalDate.of(2026, 8, 15)
                    )
                    .activities(List.of())
                    .build();

    ProductBatch batch = ProductBatch.builder()
            .batchId(batchId)
            .batchCode("BATCH-000015")
            .product(product)
            .farmingSeason(season)
            .quantity(
                    new BigDecimal("500")
            )
            .remainingQuantity(
                    new BigDecimal("500")
            )
            .status(
                    ProductBatchStatus.AVAILABLE
            )
            .build();

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
                        .build();

        BlockchainTransactionResponse blockchainResponse =
                BlockchainTransactionResponse.builder()
                        .transactionId(1L)
                        .entityType("PRODUCT_BATCH")
                        .entityId(batchId)
                        .txHash("0xabc123")
                        .status(
                                BlockchainTransactionStatus.SUCCESS
                        )
                        .build();

        // ======================================================
        // Mock SecurityUtils
        // ======================================================

        try (MockedStatic<SecurityUtils> securityUtils =
                     mockStatic(SecurityUtils.class)) {

            securityUtils
                    .when(SecurityUtils::getCurrentAccountId)
                    .thenReturn(accountId);

            // ==================================================
            // Mock Farm
            // ==================================================

            when(
                    farmRepository
                            .findByAccount_AccountId(accountId)
            ).thenReturn(
                    Optional.of(farm)
            );

            // ==================================================
            // Mock ProductBatch
            // ==================================================

            when(
                    productBatchRepository
                            .findByBatchId(batchId)
            ).thenReturn(
                    Optional.of(batch)
            );

            // ==================================================
            // Mock Blockchain Payload
            // ==================================================

            when(
                    blockchainService.recordProductBatch(
                            any(ProductBatchBlockchainPayload.class)
                    )
            ).thenReturn(
                    blockchainResponse
            );

            // ==================================================
            // Execute
            // ==================================================

            BlockchainTransactionResponse result =
                    productBatchService
                            .recordToBlockchain(batchId);

            // ==================================================
            // Assert
            // ==================================================

            assertNotNull(result);

            assertEquals(
                    BlockchainTransactionStatus.SUCCESS,
                    result.getStatus()
            );

            assertEquals(
                    batchId,
                    result.getEntityId()
            );

            assertEquals(
                    "0xabc123",
                    result.getTxHash()
            );

            // ==================================================
            // Capture Payload
            // ==================================================

            ArgumentCaptor<ProductBatchBlockchainPayload>
                    payloadCaptor =
                    ArgumentCaptor.forClass(
                            ProductBatchBlockchainPayload.class
                    );

            verify(blockchainService)
                    .recordProductBatch(
                            payloadCaptor.capture()
                    );

            ProductBatchBlockchainPayload capturedPayload =
                    payloadCaptor.getValue();

            // ==================================================
            // Verify Blockchain Payload
            // ==================================================

            assertEquals(
                    batchId,
                    capturedPayload.getBatchId()
            );

            assertEquals(
                    "BATCH-000015",
                    capturedPayload.getBatchCode()
            );

            assertEquals(
                    3L,
                    capturedPayload.getProductId()
            );

            assertEquals(
                    2L,
                    capturedPayload.getFarmId()
            );

            assertEquals(
                    7L,
                    capturedPayload.getSeasonId()
            );

            assertEquals(
                    LocalDate.of(2026, 8, 15),
                    capturedPayload.getHarvestDate()
            );

            // Quan trọng:
            // Blockchain ghi quantity, không ghi remainingQuantity
            assertEquals(
                    new BigDecimal("500"),
                    capturedPayload.getQuantity()
            );
        }
    }


    // ==========================================================
    // TEST 02
    // actualHarvestDate == null
    // -> Không được ghi Blockchain
    // ==========================================================

    @Test
    void recordToBlockchain_shouldRejectWhenHarvestDateIsNull() {

        Long batchId = 15L;
        Long accountId = 100L;

        Farm farm = Farm.builder()
                .farmId(2L)
                .build();
        FarmingSeason season =
        FarmingSeason.builder()
                .seasonId(7L)
                .actualHarvestDate(null)
                .activities(List.of())
                .build();
        Product product = Product.builder()
                .productId(3L)
                .farm(farm)
                .build();

        ProductBatch batch =
        ProductBatch.builder()
                .batchId(15L)
                .batchCode("BATCH-000015")
                .product(product)
                .farmingSeason(season)
                .quantity(new BigDecimal("500"))
                .remainingQuantity(new BigDecimal("500"))
                .status(ProductBatchStatus.AVAILABLE)
                .build();

        try (MockedStatic<SecurityUtils> securityUtils =
                     mockStatic(SecurityUtils.class)) {

            securityUtils
                    .when(SecurityUtils::getCurrentAccountId)
                    .thenReturn(accountId);

            when(
                    farmRepository
                            .findByAccount_AccountId(accountId)
            ).thenReturn(
                    Optional.of(farm)
            );

            when(
                    productBatchRepository
                            .findByBatchId(batchId)
            ).thenReturn(
                    Optional.of(batch)
            );

            // ==================================================
            // Execute + Assert
            // ==================================================

            assertThrows(
                    BadRequestException.class,
                    () ->
                            productBatchService
                                    .recordToBlockchain(batchId)
            );

            // Không được gọi Blockchain
            verify(
                    blockchainService,
                    never()
            ).recordProductBatch(
                    any(ProductBatchBlockchainPayload.class)
            );
        }
    }


    // ==========================================================
    // TEST 03
    // Batch không thuộc Farm hiện tại
    // -> Không được ghi Blockchain
    // ==========================================================

   @Test
void recordToBlockchain_shouldRejectWhenBatchDoesNotBelongToCurrentFarm() {

    Long batchId = 15L;
    Long accountId = 100L;

    Farm currentFarm = Farm.builder()
            .farmId(2L)
            .build();

    Farm anotherFarm = Farm.builder()
            .farmId(99L)
            .build();

    Product product = Product.builder()
            .productId(3L)
            .farm(anotherFarm)
            .build();

    FarmingSeason season = FarmingSeason.builder()
            .seasonId(7L)
            .actualHarvestDate(
                    LocalDate.of(2026, 8, 15)
            )
            .activities(List.of())
            .build();

    ProductBatch batch = ProductBatch.builder()
            .batchId(batchId)
            .batchCode("BATCH-000015")
            .product(product)
            .farmingSeason(season)
            .quantity(
                    new BigDecimal("500")
            )
            .remainingQuantity(
                    new BigDecimal("500")
            )
            .status(
                    ProductBatchStatus.AVAILABLE
            )
            .build();

        try (MockedStatic<SecurityUtils> securityUtils =
                     mockStatic(SecurityUtils.class)) {

            securityUtils
                    .when(SecurityUtils::getCurrentAccountId)
                    .thenReturn(accountId);

            when(
                    farmRepository
                            .findByAccount_AccountId(accountId)
            ).thenReturn(
                    Optional.of(currentFarm)
            );

            when(
                    productBatchRepository
                            .findByBatchId(batchId)
            ).thenReturn(
                    Optional.of(batch)
            );

            // ==================================================
            // Execute + Assert
            // ==================================================

            assertThrows(
                    ResourceNotFoundException.class,
                    () ->
                            productBatchService
                                    .recordToBlockchain(batchId)
            );

            verify(
                    blockchainService,
                    never()
            ).recordProductBatch(
                    any(ProductBatchBlockchainPayload.class)
            );
        }
    }


    // ==========================================================
    // TEST 04
    // Batch không tồn tại
    // ==========================================================

    @Test
void recordToBlockchain_shouldRejectWhenBatchNotFound() {

    Long batchId = 999L;

    when(productBatchRepository.findByBatchId(batchId))
            .thenReturn(Optional.empty());

    assertThrows(
            ResourceNotFoundException.class,
            () -> productBatchService.recordToBlockchain(batchId)
    );

    verify(productBatchRepository)
            .findByBatchId(batchId);

    verifyNoInteractions(blockchainService);
}
}