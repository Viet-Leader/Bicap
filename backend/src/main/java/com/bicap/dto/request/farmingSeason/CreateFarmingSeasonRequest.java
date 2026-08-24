package com.bicap.dto.request.farmingSeason;

import jakarta.validation.constraints.Future;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateFarmingSeasonRequest {

    @NotBlank(message = "Season name is required.")
    private String seasonName;

    @NotNull(message = "Planting date is required.")
    private LocalDate plantingDate;

    @NotNull(message = "Expected harvest date is required.")
    @Future(message = "Expected harvest date must be in the future.")
    private LocalDate expectedHarvestDate;

}