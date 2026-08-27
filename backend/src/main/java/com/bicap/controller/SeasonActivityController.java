package com.bicap.controller;

import com.bicap.dto.request.seasonActivity.CreateSeasonActivityRequest;
import com.bicap.dto.request.seasonActivity.UpdateSeasonActivityRequest;
import com.bicap.dto.response.seasonActivity.SeasonActivityDetailResponse;
import com.bicap.dto.response.seasonActivity.SeasonActivitySummaryResponse;
import com.bicap.service.SeasonActivityService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/season-activities")
public class SeasonActivityController {

    private final SeasonActivityService seasonActivityService;

    /**
     * Danh sách hoạt động của một mùa vụ.
     */
    @GetMapping("/season/{seasonId}")
    public ResponseEntity<Page<SeasonActivitySummaryResponse>> getBySeason(
            @PathVariable Long seasonId,
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                seasonActivityService.getBySeason(
                        seasonId,
                        pageable
                )
        );
    }

    /**
     * Chi tiết hoạt động.
     */
    @GetMapping("/{activityId}")
    public ResponseEntity<SeasonActivityDetailResponse> getById(
            @PathVariable Long activityId
    ) {

        return ResponseEntity.ok(
                seasonActivityService.getById(activityId)
        );
    }

    /**
     * Tạo hoạt động mới.
     */
    @PostMapping("/season/{seasonId}")
    public ResponseEntity<SeasonActivityDetailResponse> create(
            @PathVariable Long seasonId,
            @Valid
            @RequestBody CreateSeasonActivityRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        seasonActivityService.create(
                                seasonId,
                                request
                        )
                );
    }

    /**
     * Cập nhật hoạt động.
     */
    @PutMapping("/{activityId}")
    public ResponseEntity<SeasonActivityDetailResponse> update(
            @PathVariable Long activityId,
            @Valid
            @RequestBody UpdateSeasonActivityRequest request
    ) {

        return ResponseEntity.ok(
                seasonActivityService.update(
                        activityId,
                        request
                )
        );
    }

    /**
     * Xóa hoạt động.
     */
    @DeleteMapping("/{activityId}")
    public ResponseEntity<Void> delete(
            @PathVariable Long activityId
    ) {

        seasonActivityService.delete(activityId);

        return ResponseEntity.noContent().build();
    }
}