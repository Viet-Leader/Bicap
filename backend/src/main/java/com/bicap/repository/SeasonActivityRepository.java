package com.bicap.repository;

import com.bicap.entity.SeasonActivity;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface SeasonActivityRepository
        extends JpaRepository<SeasonActivity, Long> {

    /* =========================
            Farmer
       ========================= */

    Page<SeasonActivity> findByFarmingSeasonSeasonId(
            Long seasonId,
            Pageable pageable
    );

    List<SeasonActivity> findByFarmingSeasonSeasonIdOrderByActivityTimeDesc(
            Long seasonId
    );

    Optional<SeasonActivity> findByActivityId(
            Long activityId
    );

    Optional<SeasonActivity> findByActivityIdAndFarmingSeasonSeasonId(
            Long activityId,
            Long seasonId
    );

    void deleteByActivityId(
            Long activityId
    );

    long countByFarmingSeasonSeasonId(
            Long seasonId
    );
}