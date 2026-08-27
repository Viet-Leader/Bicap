package com.bicap.controller;

import com.bicap.dto.request.farmingSeason.CreateFarmingSeasonRequest;
import com.bicap.dto.request.farmingSeason.FinishSeasonRequest;
import com.bicap.dto.request.farmingSeason.UpdateFarmingSeasonRequest;
import com.bicap.dto.response.farmingSeason.FarmingSeasonDetailResponse;
import com.bicap.dto.response.farmingSeason.FarmingSeasonSummaryResponse;
import com.bicap.service.FarmingSeasonService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/farming-seasons")
public class FarmingSeasonController {

    private final FarmingSeasonService farmingSeasonService;

    /**
     * Danh sách mùa vụ.
     */
    @GetMapping
    public ResponseEntity<Page<FarmingSeasonSummaryResponse>> getMySeasons(
            @RequestParam(required = false)
            String keyword,
            Pageable pageable
    ) {

        return ResponseEntity.ok(
                farmingSeasonService.getMySeasons(
                        keyword,
                        pageable
                )
        );
    }

    /**
     * Chi tiết mùa vụ.
     */
    @GetMapping("/{seasonId}")
    public ResponseEntity<FarmingSeasonDetailResponse> getMySeason(
            @PathVariable Long seasonId
    ) {

        return ResponseEntity.ok(
                farmingSeasonService.getMySeason(seasonId)
        );
    }

    /**
     * Tạo mùa vụ.
     */
    @PostMapping
    public ResponseEntity<FarmingSeasonDetailResponse> create(
            @Valid
            @RequestBody
            CreateFarmingSeasonRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        farmingSeasonService.create(request)
                );
    }

    /**
     * Cập nhật mùa vụ.
     */
    @PutMapping("/{seasonId}")
    public ResponseEntity<FarmingSeasonDetailResponse> update(
            @PathVariable Long seasonId,
            @Valid
            @RequestBody
            UpdateFarmingSeasonRequest request
    ) {

        return ResponseEntity.ok(
                farmingSeasonService.update(
                        seasonId,
                        request
                )
        );
    }

    /**
     * Bắt đầu gieo trồng.
     */
    @PatchMapping("/{seasonId}/start")
    public ResponseEntity<FarmingSeasonDetailResponse> start(
            @PathVariable Long seasonId
    ) {

        return ResponseEntity.ok(
                farmingSeasonService.start(seasonId)
        );
    }

    /**
     * Bắt đầu thu hoạch.
     */
    @PatchMapping("/{seasonId}/harvest")
    public ResponseEntity<FarmingSeasonDetailResponse> harvest(
            @PathVariable Long seasonId
    ) {

        return ResponseEntity.ok(
                farmingSeasonService.harvest(seasonId)
        );
    }

    /**
     * Kết thúc mùa vụ.
     */
    @PatchMapping("/{seasonId}/finish")
    public ResponseEntity<FarmingSeasonDetailResponse> finish(
            @PathVariable Long seasonId,
            @Valid
            @RequestBody
            FinishSeasonRequest request
    ) {

        return ResponseEntity.ok(
                farmingSeasonService.finish(
                        seasonId,
                        request
                )
        );
    }

    /**
     * Hủy mùa vụ.
     */
    @PatchMapping("/{seasonId}/cancel")
    public ResponseEntity<FarmingSeasonDetailResponse> cancel(
            @PathVariable Long seasonId
    ) {

        return ResponseEntity.ok(
                farmingSeasonService.cancel(seasonId)
        );
    }
}