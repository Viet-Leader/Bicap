package com.bicap.service;

import com.bicap.dto.request.farmingSeason.CreateFarmingSeasonRequest;
import com.bicap.dto.request.farmingSeason.FinishSeasonRequest;
import com.bicap.dto.request.farmingSeason.UpdateFarmingSeasonRequest;
import com.bicap.dto.response.farmingSeason.FarmingSeasonDetailResponse;
import com.bicap.dto.response.farmingSeason.FarmingSeasonSummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface FarmingSeasonService {

    /* =========================
            Farmer
       ========================= */

    /**
     * Danh sách mùa vụ của Farm hiện tại.
     * Có hỗ trợ tìm kiếm theo tên.
     */
    Page<FarmingSeasonSummaryResponse> getMySeasons(
            String keyword,
            Pageable pageable
    );

    /**
     * Chi tiết mùa vụ.
     */
    FarmingSeasonDetailResponse getMySeason(
            Long seasonId
    );

    /**
     * Tạo mùa vụ.
     */
    FarmingSeasonDetailResponse create(
            CreateFarmingSeasonRequest request
    );

    /**
     * Cập nhật mùa vụ.
     */
    FarmingSeasonDetailResponse update(
            Long seasonId,
            UpdateFarmingSeasonRequest request
    );

    /**
     * Bắt đầu gieo trồng.
     * PLANNING -> GROWING
     */
    FarmingSeasonDetailResponse start(
            Long seasonId
    );

    /**
     * Bắt đầu thu hoạch.
     * GROWING -> HARVESTING
     */
    FarmingSeasonDetailResponse harvest(
            Long seasonId
    );

    /**
     * Kết thúc mùa vụ.
     * HARVESTING -> FINISHED
     */
    FarmingSeasonDetailResponse finish(
            Long seasonId,
            FinishSeasonRequest request
    );

    /**
     * Hủy mùa vụ.
     */
    FarmingSeasonDetailResponse cancel(
            Long seasonId
    );

}