package com.bicap.service.impl;

import com.bicap.dto.request.seasonActivity.CreateSeasonActivityRequest;
import com.bicap.dto.request.seasonActivity.UpdateSeasonActivityRequest;
import com.bicap.dto.response.seasonActivity.SeasonActivityDetailResponse;
import com.bicap.dto.response.seasonActivity.SeasonActivitySummaryResponse;
import com.bicap.entity.ActivityType;
import com.bicap.entity.Farm;
import com.bicap.entity.FarmingSeason;
import com.bicap.entity.SeasonActivity;
import com.bicap.exception.BadRequestException;
import com.bicap.exception.ResourceNotFoundException;
import com.bicap.mapper.SeasonActivityMapper;
import com.bicap.repository.ActivityTypeRepository;
import com.bicap.repository.FarmRepository;
import com.bicap.repository.FarmingSeasonRepository;
import com.bicap.repository.SeasonActivityRepository;
import com.bicap.security.SecurityUtils;
import com.bicap.service.SeasonActivityService;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class SeasonActivityServiceImpl
        implements SeasonActivityService {

    private final SeasonActivityRepository seasonActivityRepository;

    private final FarmingSeasonRepository farmingSeasonRepository;

    private final ActivityTypeRepository activityTypeRepository;

    private final FarmRepository farmRepository;

    private final SeasonActivityMapper seasonActivityMapper;
        /**
     * Lấy Farm hiện tại.
     */
    private Farm getCurrentFarm() {

        Long accountId = SecurityUtils.getCurrentAccountId();

        if (accountId == null) {
            throw new BadRequestException("Unauthenticated.");
        }

        return farmRepository
                .findByAccount_AccountId(accountId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Farm not found."));
    }
        /**
     * Lấy Season thuộc Farm hiện tại.
     */
    private FarmingSeason getMySeason(Long seasonId) {

        Farm farm = getCurrentFarm();

        return farmingSeasonRepository
                .findBySeasonIdAndFarmFarmId(
                        seasonId,
                        farm.getFarmId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Season not found."
                        ));
    }
        /**
     * Lấy Activity thuộc Farm hiện tại.
     */
    private SeasonActivity getMyActivity(Long activityId) {

        SeasonActivity activity =
                seasonActivityRepository
                        .findByActivityId(activityId)
                        .orElseThrow(() ->
                                new ResourceNotFoundException(
                                        "Activity not found."
                                ));

        Farm farm = getCurrentFarm();

        if (!activity.getFarmingSeason()
                .getFarm()
                .getFarmId()
                .equals(farm.getFarmId())) {

            throw new ResourceNotFoundException(
                    "Activity not found."
            );
        }

        return activity;
    }
        /**
     * Lấy ActivityType.
     */
    private ActivityType getActivityType(
            Long activityTypeId
    ) {

        return activityTypeRepository
                .findByActivityTypeId(activityTypeId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Activity type not found."
                        ));
    }
    @Override
public Page<SeasonActivitySummaryResponse> getBySeason(
        Long seasonId,
        Pageable pageable
) {

    FarmingSeason season = getMySeason(seasonId);

    Page<SeasonActivity> activities =
            seasonActivityRepository
                    .findByFarmingSeasonSeasonId(
                            season.getSeasonId(),
                            pageable
                    );

    return activities.map(
            seasonActivityMapper::toSummary
    );
}
@Override
public List<SeasonActivitySummaryResponse> getAllBySeason(
        Long seasonId
) {

    FarmingSeason season = getMySeason(seasonId);

    List<SeasonActivity> activities =
            seasonActivityRepository
                    .findByFarmingSeasonSeasonIdOrderByActivityTimeDesc(
                            season.getSeasonId()
                    );

    return seasonActivityMapper.toSummaryList(
            activities
    );
}
@Override
public SeasonActivityDetailResponse getById(
        Long activityId
) {

    SeasonActivity activity =
            getMyActivity(activityId);

    return seasonActivityMapper.toDetail(
            activity
    );
}
@Override
public SeasonActivityDetailResponse create(
        Long seasonId,
        CreateSeasonActivityRequest request
) {

    FarmingSeason season =
            getMySeason(seasonId);

    ActivityType activityType =
            getActivityType(
                    request.getActivityTypeId()
            );

    SeasonActivity activity =
            seasonActivityMapper.toEntity(request);

    activity.setFarmingSeason(season);

    activity.setActivityType(activityType);

    activity =
            seasonActivityRepository.save(activity);

    return seasonActivityMapper.toDetail(
            activity
    );
}
@Override
public SeasonActivityDetailResponse update(
        Long activityId,
        UpdateSeasonActivityRequest request
) {

    SeasonActivity activity =
            getMyActivity(activityId);

    ActivityType activityType =
            getActivityType(
                    request.getActivityTypeId()
            );

    seasonActivityMapper.updateEntity(
            request,
            activity
    );

    activity.setActivityType(activityType);

    activity =
            seasonActivityRepository.save(activity);

    return seasonActivityMapper.toDetail(
            activity
    );
}
@Override
public void delete(
        Long activityId
) {

    SeasonActivity activity =
            getMyActivity(activityId);

    seasonActivityRepository.delete(activity);
}
}