package com.bicap.service;

import com.bicap.dto.request.seasonActivity.CreateSeasonActivityRequest;
import com.bicap.dto.request.seasonActivity.UpdateSeasonActivityRequest;
import com.bicap.dto.response.seasonActivity.SeasonActivityDetailResponse;
import com.bicap.dto.response.seasonActivity.SeasonActivitySummaryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface SeasonActivityService {

    /* =========================
            Farmer
       ========================= */

    /**
     * Danh sách hoạt động của một mùa vụ.
     */
    Page<SeasonActivitySummaryResponse> getBySeason(
            Long seasonId,
            Pageable pageable
    );

    /**
     * Danh sách đầy đủ hoạt động của mùa vụ.
     */
    List<SeasonActivitySummaryResponse> getAllBySeason(
            Long seasonId
    );

    /**
     * Chi tiết hoạt động.
     */
    SeasonActivityDetailResponse getById(
            Long activityId
    );

    /**
     * Tạo hoạt động.
     */
    SeasonActivityDetailResponse create(
            Long seasonId,
            CreateSeasonActivityRequest request
    );

    /**
     * Cập nhật hoạt động.
     */
    SeasonActivityDetailResponse update(
            Long activityId,
            UpdateSeasonActivityRequest request
    );

    /**
     * Xóa hoạt động.
     */
    void delete(
            Long activityId
    );
}