package com.bicap.dto.response.seasonActivity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeasonActivitySummaryResponse {

    private Long activityId;

    private String activityTypeName;

    private LocalDateTime activityTime;

}