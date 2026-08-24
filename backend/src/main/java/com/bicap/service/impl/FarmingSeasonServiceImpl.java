package com.bicap.service.impl;

import com.bicap.common.enums.FarmingSeasonStatus;
import com.bicap.dto.request.farmingSeason.CreateFarmingSeasonRequest;
import com.bicap.dto.request.farmingSeason.FinishSeasonRequest;
import com.bicap.dto.request.farmingSeason.UpdateFarmingSeasonRequest;
import com.bicap.dto.response.farmingSeason.FarmingSeasonDetailResponse;
import com.bicap.dto.response.farmingSeason.FarmingSeasonSummaryResponse;
import com.bicap.entity.Farm;
import com.bicap.entity.FarmingSeason;
import com.bicap.exception.BadRequestException;
import com.bicap.exception.ResourceNotFoundException;
import com.bicap.mapper.FarmingSeasonMapper;
import com.bicap.repository.FarmRepository;
import com.bicap.repository.FarmingSeasonRepository;
import com.bicap.security.SecurityUtils;
import com.bicap.service.FarmingSeasonService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FarmingSeasonServiceImpl implements FarmingSeasonService {

    private final FarmingSeasonRepository farmingSeasonRepository;

    private final FarmRepository farmRepository;

    private final FarmingSeasonMapper farmingSeasonMapper;

    /**
     * Lấy Farm của tài khoản hiện tại.
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
    private FarmingSeason getMySeasonEntity(Long seasonId) {

        Farm farm = getCurrentFarm();

        return farmingSeasonRepository
                .findBySeasonIdAndFarmFarmId(
                        seasonId,
                        farm.getFarmId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException("Season not found."));
    }
    @Override
public Page<FarmingSeasonSummaryResponse> getMySeasons(
        String keyword,
        Pageable pageable
) {

    Farm farm = getCurrentFarm();

    Page<FarmingSeason> seasons;

    if (keyword == null || keyword.isBlank()) {

        seasons = farmingSeasonRepository.findByFarmFarmId(
                farm.getFarmId(),
                pageable
        );

    } else {

        seasons = farmingSeasonRepository
                .findByFarmFarmIdAndSeasonNameContainingIgnoreCase(
                        farm.getFarmId(),
                        keyword.trim(),
                        pageable
                );
    }

    return seasons.map(farmingSeasonMapper::toSummary);
}  
    @Override
public FarmingSeasonDetailResponse getMySeason(Long seasonId) {

    FarmingSeason season = getMySeasonEntity(seasonId);

    return farmingSeasonMapper.toDetail(season);
}
@Override
public FarmingSeasonDetailResponse create(
        CreateFarmingSeasonRequest request
) {

    Farm farm = getCurrentFarm();

    boolean existed =
            farmingSeasonRepository
                    .existsByFarmFarmIdAndSeasonNameIgnoreCase(
                            farm.getFarmId(),
                            request.getSeasonName()
                    );

    if (existed) {
        throw new BadRequestException(
                "Season name already exists."
        );
    }

    if (request.getExpectedHarvestDate()
            .isBefore(request.getPlantingDate())) {

        throw new BadRequestException(
                "Expected harvest date must be after planting date."
        );
    }

    FarmingSeason season =
            farmingSeasonMapper.toEntity(request);

    season.setFarm(farm);

    season.setStatus(FarmingSeasonStatus.PLANNING);

    season = farmingSeasonRepository.save(season);

    return farmingSeasonMapper.toDetail(season);
}
    @Override
public FarmingSeasonDetailResponse update(
        Long seasonId,
        UpdateFarmingSeasonRequest request
) {

    FarmingSeason season = getMySeasonEntity(seasonId);

    Farm farm = getCurrentFarm();

    boolean existed =
            farmingSeasonRepository
                    .existsByFarmFarmIdAndSeasonNameIgnoreCaseAndSeasonIdNot(
                            farm.getFarmId(),
                            request.getSeasonName(),
                            seasonId
                    );

    if (existed) {
        throw new BadRequestException(
                "Season name already exists."
        );
    }

    if (request.getExpectedHarvestDate()
            .isBefore(request.getPlantingDate())) {

        throw new BadRequestException(
                "Expected harvest date must be after planting date."
        );
    }

    farmingSeasonMapper.updateEntity(
            request,
            season
    );

    season = farmingSeasonRepository.save(season);

    return farmingSeasonMapper.toDetail(season);
}
   @Override
public FarmingSeasonDetailResponse start(Long seasonId) {

    FarmingSeason season = getMySeasonEntity(seasonId);

    if (season.getStatus() != FarmingSeasonStatus.PLANNING) {
        throw new BadRequestException(
                "Only planning season can be started."
        );
    }

    season.setStatus(FarmingSeasonStatus.GROWING);

    season = farmingSeasonRepository.save(season);

    return farmingSeasonMapper.toDetail(season);
}
@Override
public FarmingSeasonDetailResponse harvest(Long seasonId) {

    FarmingSeason season = getMySeasonEntity(seasonId);

    if (season.getStatus() != FarmingSeasonStatus.GROWING) {
        throw new BadRequestException(
                "Only growing season can be harvested."
        );
    }

    season.setStatus(FarmingSeasonStatus.HARVESTING);

    season = farmingSeasonRepository.save(season);

    return farmingSeasonMapper.toDetail(season);
}
@Override
public FarmingSeasonDetailResponse cancel(Long seasonId) {

    FarmingSeason season = getMySeasonEntity(seasonId);

    if (season.getStatus() == FarmingSeasonStatus.FINISHED) {
        throw new BadRequestException(
                "Finished season cannot be cancelled."
        );
    }

    season.setStatus(FarmingSeasonStatus.CANCELLED);

    season = farmingSeasonRepository.save(season);

    return farmingSeasonMapper.toDetail(season);
}
@Override
public FarmingSeasonDetailResponse finish(
        Long seasonId,
        FinishSeasonRequest request
) {

    FarmingSeason season = getMySeasonEntity(seasonId);

    if (season.getStatus() != FarmingSeasonStatus.HARVESTING) {
        throw new BadRequestException(
                "Only harvesting season can be finished."
        );
    }

    if (request.getActualHarvestDate()
            .isBefore(season.getPlantingDate())) {

        throw new BadRequestException(
                "Actual harvest date is invalid."
        );
    }

    season.setActualHarvestDate(
            request.getActualHarvestDate()
    );

    season.setStatus(
            FarmingSeasonStatus.FINISHED
    );

    season = farmingSeasonRepository.save(season);

    return farmingSeasonMapper.toDetail(season);
}
}