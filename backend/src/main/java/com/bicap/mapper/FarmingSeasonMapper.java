package com.bicap.mapper;

import com.bicap.dto.request.farmingSeason.CreateFarmingSeasonRequest;
import com.bicap.dto.request.farmingSeason.UpdateFarmingSeasonRequest;
import com.bicap.dto.response.farmingSeason.FarmingSeasonDetailResponse;
import com.bicap.dto.response.farmingSeason.FarmingSeasonSummaryResponse;
import com.bicap.entity.FarmingSeason;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface FarmingSeasonMapper {

    /**
     * Create DTO -> Entity
     */
    @Mapping(target = "seasonId", ignore = true)
    @Mapping(target = "farm", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "actualHarvestDate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "productBatches", ignore = true)
    @Mapping(target = "activities", ignore = true)
    FarmingSeason toEntity(CreateFarmingSeasonRequest request);

    /**
     * Update DTO -> Entity
     */
    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "seasonId", ignore = true)
    @Mapping(target = "farm", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "actualHarvestDate", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "productBatches", ignore = true)
    @Mapping(target = "activities", ignore = true)
    void updateEntity(
            UpdateFarmingSeasonRequest request,
            @MappingTarget FarmingSeason season
    );

    /**
     * Entity -> Summary Response
     */
    FarmingSeasonSummaryResponse toSummary(
            FarmingSeason season
    );

    /**
     * Entity -> Detail Response
     */
    @Mapping(target = "farmName", source = "farm.farmName")
    FarmingSeasonDetailResponse toDetail(
            FarmingSeason season
    );

    /**
     * Entity List -> Response List
     */
    List<FarmingSeasonSummaryResponse> toSummaryList(
            List<FarmingSeason> seasons
    );

}