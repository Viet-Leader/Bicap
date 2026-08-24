package com.bicap.dto.response.traceability;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TraceabilityActivityResponse {

    private Long activityId;

    private LocalDateTime activityTime;

    private String activityType;

    private String description;
}