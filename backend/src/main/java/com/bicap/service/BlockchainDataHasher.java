package com.bicap.service;

import com.bicap.dto.blockchain.FarmingActivityBlockchainPayload;
import com.bicap.dto.blockchain.ProductBatchBlockchainPayload;
import com.bicap.entity.ProductBatch;
import com.bicap.entity.SeasonActivity;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * The sole definition of the business data serialized and hashed for blockchain.
 * Keep the payload field order stable; hashes are a protocol, not presentation data.
 */
@Component
@RequiredArgsConstructor
public class BlockchainDataHasher {

    private static final DateTimeFormatter ACTIVITY_TIME_FORMAT =
            DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    private final ObjectMapper objectMapper;

    public ProductBatchBlockchainPayload productBatchPayload(ProductBatch batch) {
        var season = batch.getFarmingSeason();

        List<FarmingActivityBlockchainPayload> activities = season.getActivities()
                .stream()
                .sorted(Comparator.comparing(
                        SeasonActivity::getActivityId,
                        Comparator.nullsLast(Comparator.naturalOrder())
                ))
                .map(this::activityPayload)
                .toList();

        return ProductBatchBlockchainPayload.builder()
                .batchId(batch.getBatchId())
                .batchCode(batch.getBatchCode())
                .productId(batch.getProduct().getProductId())
                .farmId(batch.getProduct().getFarm().getFarmId())
                .seasonId(season.getSeasonId())
                .harvestDate(season.getActualHarvestDate())
                .quantity(batch.getQuantity())
                .activities(activities)
                .build();
    }

    public String hashProductBatch(ProductBatchBlockchainPayload payload) {
        ProductBatchBlockchainPayload normalizedPayload = ProductBatchBlockchainPayload.builder()
                .batchId(payload.getBatchId())
                .batchCode(payload.getBatchCode())
                .productId(payload.getProductId())
                .farmId(payload.getFarmId())
                .seasonId(payload.getSeasonId())
                .harvestDate(payload.getHarvestDate())
                .quantity(payload.getQuantity())
                .activities(payload.getActivities() == null ? List.of() : payload.getActivities().stream()
                        .sorted(Comparator.comparing(
                                FarmingActivityBlockchainPayload::getActivityId,
                                Comparator.nullsLast(Comparator.naturalOrder())
                        ))
                        .toList())
                .build();

        return sha256(toJson(normalizedPayload));
    }

    public String hashSeasonActivity(SeasonActivity activity) {
        Map<String, Object> payload = new LinkedHashMap<>();
        payload.put("activityId", activity.getActivityId());
        payload.put("seasonId", activity.getFarmingSeason().getSeasonId());
        payload.put("seasonName", activity.getFarmingSeason().getSeasonName());
        payload.put("activityType", activity.getActivityType().getActivityName());
        payload.put("activityTime", ACTIVITY_TIME_FORMAT.format(activity.getActivityTime()));
        payload.put("description", activity.getDescription());

        return sha256(toJson(payload));
    }

    private FarmingActivityBlockchainPayload activityPayload(SeasonActivity activity) {
        return FarmingActivityBlockchainPayload.builder()
                .activityId(activity.getActivityId())
                .activityTime(activity.getActivityTime())
                .activityType(activity.getActivityType().getActivityName())
                .description(activity.getDescription())
                .build();
    }

    private String toJson(Object payload) {
        try {
            return objectMapper.writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new IllegalStateException("Cannot serialize blockchain payload.", e);
        }
    }

    public String sha256(String value) {
        try {
            byte[] hash = MessageDigest.getInstance("SHA-256")
                    .digest(value.getBytes(StandardCharsets.UTF_8));
            StringBuilder result = new StringBuilder(64);
            for (byte valueByte : hash) {
                result.append(String.format("%02x", valueByte));
            }
            return result.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException("SHA-256 algorithm is not available.", e);
        }
    }
}
