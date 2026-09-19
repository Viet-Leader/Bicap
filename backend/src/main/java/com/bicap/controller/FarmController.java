package com.bicap.controller;

import com.bicap.dto.request.farm.UpdateFarmRequest;
import com.bicap.dto.request.account.ChangeAccountStatusRequest;
import com.bicap.dto.response.farm.FarmResponse;
import com.bicap.service.FarmService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/farms")
@RequiredArgsConstructor
public class FarmController {

    private final FarmService farmService;

    // ==========================================================
    // GET MY FARM
    // ==========================================================

    @GetMapping("/me")
    @PreAuthorize("hasRole('FARM')")
    public ResponseEntity<FarmResponse> getMyFarm() {

        return ResponseEntity.ok(
                farmService.getMyFarm()
        );
    }

    // ==========================================================
    // UPDATE MY FARM
    // ==========================================================

    @PutMapping("/me")
    @PreAuthorize("hasRole('FARM')")
    public ResponseEntity<FarmResponse> updateMyFarm(
            @Valid @RequestBody UpdateFarmRequest request) {

        return ResponseEntity.ok(
                farmService.updateMyFarm(request)
        );
    }

    // ==========================================================
    // GET FARM BY ID
    // ==========================================================

    @GetMapping("/{farmId}")
    @PreAuthorize("hasAnyRole('ADMIN','RETAILER')")
    public ResponseEntity<FarmResponse> getFarmById(
            @PathVariable Long farmId) {

        return ResponseEntity.ok(
                farmService.getFarmById(farmId)
        );
    }

    @GetMapping
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<List<FarmResponse>> getAllFarms() {

        return ResponseEntity.ok(
                farmService.getAllFarms()
        );
    }

    @PatchMapping("/{farmId}/status")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<FarmResponse> changeFarmStatus(
            @PathVariable Long farmId,
            @Valid @RequestBody ChangeAccountStatusRequest request) {

        return ResponseEntity.ok(
                farmService.changeFarmStatus(farmId, request.getStatus())
        );
    }

}
