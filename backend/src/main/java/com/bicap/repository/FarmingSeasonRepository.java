package com.bicap.repository;

import com.bicap.common.enums.FarmingSeasonStatus;
import com.bicap.entity.FarmingSeason;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface FarmingSeasonRepository
        extends JpaRepository<FarmingSeason, Long> {

    /* =========================
            Farmer
       ========================= */

    Page<FarmingSeason> findByFarmFarmId(
            Long farmId,
            Pageable pageable
    );

    List<FarmingSeason> findByFarmFarmId(Long farmId);

    /**
     * Search theo tên mùa vụ.
     */
   Page<FarmingSeason> findByFarmFarmIdAndSeasonNameContainingIgnoreCase(
        Long farmId,
        String keyword,
        Pageable pageable
);

    Optional<FarmingSeason> findBySeasonId(Long seasonId);

    Optional<FarmingSeason> findBySeasonIdAndFarmFarmId(
            Long seasonId,
            Long farmId
    );

    boolean existsByFarmFarmIdAndSeasonNameIgnoreCase(
            Long farmId,
            String seasonName
    );

    boolean existsByFarmFarmIdAndSeasonNameIgnoreCaseAndSeasonIdNot(
            Long farmId,
            String seasonName,
            Long seasonId
    );

    /* =========================
             Public
       ========================= */

   Page<FarmingSeason> findByStatus(
        FarmingSeasonStatus status,
        Pageable pageable
);

    Optional<FarmingSeason> findBySeasonIdAndStatus(
            Long seasonId,
            FarmingSeasonStatus status
    );

    /* =========================
            Dashboard
       ========================= */

    long countByFarmFarmId(
            Long farmId
    );

    long countByFarmFarmIdAndStatus(
            Long farmId,
            FarmingSeasonStatus status
    );

}