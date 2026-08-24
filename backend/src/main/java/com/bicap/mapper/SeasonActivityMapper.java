package com.bicap.mapper;

import com.bicap.dto.request.seasonActivity.CreateSeasonActivityRequest;
import com.bicap.dto.request.seasonActivity.UpdateSeasonActivityRequest;
import com.bicap.dto.response.seasonActivity.SeasonActivityDetailResponse;
import com.bicap.dto.response.seasonActivity.SeasonActivitySummaryResponse;
import com.bicap.entity.SeasonActivity;
import org.mapstruct.*;

import java.util.List;

@Mapper(componentModel = "spring")
public interface SeasonActivityMapper {

    /**
     * Create DTO -> Entity
     */
    @Mapping(target = "activityId", ignore = true)
    @Mapping(target = "farmingSeason", ignore = true)
    @Mapping(target = "activityType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    SeasonActivity toEntity(CreateSeasonActivityRequest request);

    /**
     * Update DTO -> Entity
     */
    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "activityId", ignore = true)
    @Mapping(target = "farmingSeason", ignore = true)
    @Mapping(target = "activityType", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    void updateEntity(
            UpdateSeasonActivityRequest request,
            @MappingTarget SeasonActivity activity
    );

    /**
     * Entity -> Summary
     */
    @Mapping(target = "activityTypeName", source = "activityType.activityName")
    SeasonActivitySummaryResponse toSummary(
            SeasonActivity activity
    );

    /**
     * Entity -> Detail
     */
    @Mapping(target = "seasonId", source = "farmingSeason.seasonId")
    @Mapping(target = "seasonName", source = "farmingSeason.seasonName")
    @Mapping(target = "activityTypeId", source = "activityType.activityTypeId")
    @Mapping(target = "activityTypeName", source = "activityType.activityName")
    SeasonActivityDetailResponse toDetail(
            SeasonActivity activity
    );

    /**
     * Entity List -> Summary List
     */
    List<SeasonActivitySummaryResponse> toSummaryList(
            List<SeasonActivity> activities
    );
}