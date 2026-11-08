package com.bicap.service;

import com.bicap.dto.blockchain.FarmingActivityBlockchainPayload;
import com.bicap.dto.blockchain.ProductBatchBlockchainPayload;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import org.junit.jupiter.api.Test;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BlockchainDataHasherTest {

    private final BlockchainDataHasher hasher = new BlockchainDataHasher(
            new ObjectMapper().registerModule(new JavaTimeModule())
    );

    @Test
    void sha256_usesUtf8AndReturnsExpectedLowercaseHex() {
        assertEquals(
                "ba7816bf8f01cfea414140de5dae2223b00361a396177a9cb410ff61f20015ad",
                hasher.sha256("abc")
        );
    }

    @Test
    void productBatchHash_isIndependentOfActivityCollectionOrder() {
        FarmingActivityBlockchainPayload first = FarmingActivityBlockchainPayload.builder()
                .activityId(1L)
                .activityTime(LocalDateTime.of(2026, 1, 1, 8, 0))
                .activityType("PLANTING")
                .description("Seeded")
                .build();
        FarmingActivityBlockchainPayload second = FarmingActivityBlockchainPayload.builder()
                .activityId(2L)
                .activityTime(LocalDateTime.of(2026, 1, 2, 8, 0))
                .activityType("WATERING")
                .description("Watered")
                .build();

        ProductBatchBlockchainPayload ordered = ProductBatchBlockchainPayload.builder()
                .batchId(1L).activities(List.of(first, second)).build();
        ProductBatchBlockchainPayload reversed = ProductBatchBlockchainPayload.builder()
                .batchId(1L).activities(List.of(second, first)).build();

        assertEquals(hasher.hashProductBatch(ordered), hasher.hashProductBatch(reversed));
    }
}
