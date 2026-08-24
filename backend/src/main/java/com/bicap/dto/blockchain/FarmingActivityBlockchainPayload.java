package com.bicap.dto.blockchain;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FarmingActivityBlockchainPayload {

    private Long activityId;

    private LocalDateTime activityTime;

    private String activityType;

    private String description;
}