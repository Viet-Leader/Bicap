package com.bicap.service;

import com.bicap.dto.request.crop.CreateCropRequest;
import com.bicap.dto.request.crop.UpdateCropRequest;
import com.bicap.dto.response.crop.CropResponse;

import java.util.List;

public interface CropService {

    /**
     * Get all active crops.
     */
    List<CropResponse> getAll();

    /**
     * Get crop by id.
     */
    CropResponse getById(Long cropId);

    /**
     * Create new crop.
     */
    CropResponse create(CreateCropRequest request);

    /**
     * Update crop.
     */
    CropResponse update(
            Long cropId,
            UpdateCropRequest request
    );

    /**
     * Change crop status (ACTIVE <-> INACTIVE).
     */
    CropResponse changeStatus(Long cropId);

}