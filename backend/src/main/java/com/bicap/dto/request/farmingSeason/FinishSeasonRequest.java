package com.bicap.dto.request.farmingSeason;

import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FinishSeasonRequest {

    @NotNull(message = "Harvest date is required.")
    private LocalDate actualHarvestDate;

}