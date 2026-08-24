package com.bicap.dto.request.seasonActivity;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateSeasonActivityRequest {

    @NotNull(message = "Activity type is required.")
    private Long activityTypeId;

    @NotNull(message = "Activity time is required.")
    private LocalDateTime activityTime;

    @NotBlank(message = "Description is required.")
    private String description;

}