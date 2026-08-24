package com.bicap.dto.response.seasonActivity;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SeasonActivityDetailResponse {

  private Long activityId;

    private Long seasonId;

    private String seasonName;

    private Long activityTypeId;

    private String activityTypeName;

    private LocalDateTime activityTime;

    private String description;

    private LocalDateTime createdAt;

    private LocalDateTime updatedAt;

}