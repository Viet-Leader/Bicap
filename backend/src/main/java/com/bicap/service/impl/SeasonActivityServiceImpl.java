package com.bicap.service.impl;

import com.bicap.common.enums.BlockchainTransactionStatus;
import com.bicap.common.enums.FarmingSeasonStatus;
import com.bicap.dto.request.seasonActivity.CreateSeasonActivityRequest;
import com.bicap.dto.request.seasonActivity.UpdateSeasonActivityRequest;
import com.bicap.dto.response.seasonActivity.SeasonActivityDetailResponse;
import com.bicap.dto.response.seasonActivity.SeasonActivitySummaryResponse;
import com.bicap.dto.response.blockchain.BlockchainVerificationResponse;
import com.bicap.dto.response.blockchain.BlockchainTransactionResponse;
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
import com.bicap.service.BlockchainDataHasher;
import com.bicap.service.BlockchainService;
import com.bicap.service.SeasonActivityService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class SeasonActivityServiceImpl
        implements SeasonActivityService {

    private final SeasonActivityRepository seasonActivityRepository;

    private final FarmingSeasonRepository farmingSeasonRepository;

    private final ActivityTypeRepository activityTypeRepository;

    private final FarmRepository farmRepository;

    private final SeasonActivityMapper seasonActivityMapper;

    private final BlockchainDataHasher blockchainDataHasher;

    private final BlockchainService blockchainService;

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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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
    @Transactional(readOnly = true)
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

        FarmingSeason season = getMySeason(seasonId);

        // ============================================================
        // Validate: không cho tạo sự kiện khi mùa vụ đã kết thúc/hủy
        // ============================================================
        if (season.getStatus() == FarmingSeasonStatus.FINISHED
                || season.getStatus() == FarmingSeasonStatus.CANCELLED) {
            throw new BadRequestException(
                    "Cannot add activities to a season with status: " + season.getStatus()
            );
        }

        ActivityType activityType =
                getActivityType(
                        request.getActivityTypeId()
                );

        SeasonActivity activity =
                seasonActivityMapper.toEntity(request);

        activity.setFarmingSeason(season);
        activity.setActivityType(activityType);

        // ============================================================
        // Bước 1: Lưu vào DB trước (blockchainTxHash còn null)
        // ============================================================
        activity = seasonActivityRepository.save(activity);

        // ============================================================
        // Bước 2: Ghi lên Blockchain
        // Serialize payload -> SHA-256 -> gửi lên contract
        // ============================================================
        try {
            String dataHash = blockchainDataHasher.hashSeasonActivity(activity);

            BlockchainTransactionResponse blockchainTransaction =
                    blockchainService.recordSeasonActivity(activity.getActivityId(), dataHash);

            // ============================================================
            // Bước 3: Cập nhật txHash vào DB record
            // ============================================================
            if (blockchainTransaction.getStatus() == BlockchainTransactionStatus.SUCCESS) {
                activity.setBlockchainTxHash(blockchainTransaction.getTxHash());
            }
            activity = seasonActivityRepository.save(activity);

            log.info(
                    "[SeasonActivity] Activity #{} recorded on blockchain. TxHash: {}",
                    activity.getActivityId(),
                    blockchainTransaction.getTxHash()
            );

        } catch (Exception e) {
            // Blockchain fail — throw để @Transactional rollback DB record
            log.error(
                    "[SeasonActivity] Blockchain call failed for activity #{}: {}",
                    activity.getActivityId(),
                    e.getMessage()
            );
            // Keep the business record. The ledger entry is persisted as FAILED independently.
        }

        return seasonActivityMapper.toDetail(activity);
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
        SeasonActivity activity = getMyActivity(activityId);

        seasonActivityRepository.delete(activity);
    }

    @Override
    @Transactional(readOnly = true)
    public BlockchainVerificationResponse verifyBlockchainRecord(Long activityId) {
        getMyActivity(activityId);
        return blockchainService.verifySeasonActivity(activityId);
    }
}
