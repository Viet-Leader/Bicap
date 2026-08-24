package com.bicap.service.impl;

import com.bicap.dto.response.traceability.TraceabilityActivityResponse;
import com.bicap.dto.response.traceability.TraceabilityResponse;
import com.bicap.entity.BlockchainTransaction;
import com.bicap.entity.ProductBatch;
import com.bicap.entity.SeasonActivity;
import com.bicap.exception.ResourceNotFoundException;
import com.bicap.repository.BlockchainTransactionRepository;
import com.bicap.repository.ProductBatchRepository;
import com.bicap.service.TraceabilityService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class TraceabilityServiceImpl implements TraceabilityService {

    private static final String ENTITY_TYPE_PRODUCT_BATCH =
            "PRODUCT_BATCH";

    private final ProductBatchRepository productBatchRepository;

    private final BlockchainTransactionRepository
            blockchainTransactionRepository;

    @Override
    public TraceabilityResponse getByBatchId(Long batchId) {

        // 1. Lấy ProductBatch
        ProductBatch batch = productBatchRepository
                .findByBatchId(batchId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Product batch not found."
                        )
                );

        // 2. Lấy FarmingSeason
        var season = batch.getFarmingSeason();

        if (season == null) {
            throw new ResourceNotFoundException(
                    "Farming season not found."
            );
        }

        // 3. Lấy danh sách Farming Activities
        List<TraceabilityActivityResponse> activities =
                season.getActivities()
                        .stream()
                        .map(this::toActivityResponse)
                        .toList();

        // 4. Lấy Blockchain Transaction
        BlockchainTransaction transaction =
                blockchainTransactionRepository
                        .findByEntityTypeAndEntityId(
                                ENTITY_TYPE_PRODUCT_BATCH,
                                batchId
                        )
                        .orElse(null);

        // 5. Tạo Traceability Response
        return TraceabilityResponse.builder()

                // =========================
                // PRODUCT BATCH
                // =========================

                .batchId(batch.getBatchId())
                .batchCode(batch.getBatchCode())

                .productName(
                        batch.getProduct()
                                .getProductName()
                )

                .cropName(
                        batch.getProduct()
                                .getCrop()
                                .getCropName()
                )

                .farmName(
                        batch.getProduct()
                                .getFarm()
                                .getFarmName()
                )

                .grade(batch.getGrade())

                .quantity(batch.getQuantity())

                .unit(
                        batch.getProduct()
                                .getUnit()
                )

                // =========================
                // FARMING SEASON
                // =========================

                .seasonId(season.getSeasonId())
                .seasonName(season.getSeasonName())
                .plantingDate(season.getPlantingDate())
                .expectedHarvestDate(
                        season.getExpectedHarvestDate()
                )
                .actualHarvestDate(
                        season.getActualHarvestDate()
                )

                // =========================
                // FARMING ACTIVITIES
                // =========================

                .activities(activities)

                // =========================
                // BLOCKCHAIN
                // =========================

                .transactionId(
                        transaction != null
                                ? transaction.getTransactionId()
                                : null
                )

                .txHash(
                        transaction != null
                                ? transaction.getTxHash()
                                : null
                )

                .blockchainStatus(
                        transaction != null
                                ? transaction.getStatus()
                                : null
                )

                .blockchainCreatedAt(
                        transaction != null
                                ? transaction.getCreatedAt()
                                : null
                )

                .build();
    }

    private TraceabilityActivityResponse toActivityResponse(
            SeasonActivity activity
    ) {

        return TraceabilityActivityResponse.builder()
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