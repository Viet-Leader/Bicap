package com.bicap.controller;

import com.bicap.dto.request.crop.CreateCropRequest;
import com.bicap.dto.request.crop.UpdateCropRequest;
import com.bicap.dto.response.crop.CropResponse;
import com.bicap.service.CropService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/crops")
public class CropController {

    private final CropService cropService;

    /**
     * Danh sách cây trồng.
     */
    @GetMapping
    public ResponseEntity<List<CropResponse>> getAll() {

        return ResponseEntity.ok(
                cropService.getAll()
        );
    }

    /**
     * Chi tiết cây trồng.
     */
    @GetMapping("/{cropId}")
    public ResponseEntity<CropResponse> getById(
            @PathVariable Long cropId
    ) {

        return ResponseEntity.ok(
                cropService.getById(cropId)
        );
    }

    /**
     * Tạo cây trồng.
     */
    @PostMapping
    public ResponseEntity<CropResponse> create(
            @Valid
            @RequestBody
            CreateCropRequest request
    ) {

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(
                        cropService.create(request)
                );
    }

    /**
     * Cập nhật cây trồng.
     */
    @PutMapping("/{cropId}")
    public ResponseEntity<CropResponse> update(
            @PathVariable Long cropId,
            @Valid
            @RequestBody
            UpdateCropRequest request
    ) {

        return ResponseEntity.ok(
                cropService.update(
                        cropId,
                        request
                )
        );
    }

    /**
     * Đổi trạng thái ACTIVE / INACTIVE.
     */
    @PatchMapping("/{cropId}/status")
    public ResponseEntity<CropResponse> changeStatus(
            @PathVariable Long cropId
    ) {

        return ResponseEntity.ok(
                cropService.changeStatus(cropId)
        );
    }
}