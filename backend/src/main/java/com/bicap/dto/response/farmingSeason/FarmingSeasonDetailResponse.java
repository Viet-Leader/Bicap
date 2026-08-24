package com.bicap.dto.response.farmingSeason;

import com.bicap.common.enums.FarmingSeasonStatus;
import lombok.*;

import java.time.LocalDate;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class FarmingSeasonDetailResponse {

    private Long seasonId;

    private String farmName;

    private String seasonName;

    private LocalDate plantingDate;

    private LocalDate expectedHarvestDate;

    private LocalDate actualHarvestDate;

    private FarmingSeasonStatus status;

}