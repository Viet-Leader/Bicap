package com.bicap.service.impl;

import com.bicap.common.enums.AccountStatus;
import com.bicap.dto.request.crop.CreateCropRequest;
import com.bicap.dto.request.crop.UpdateCropRequest;
import com.bicap.dto.response.crop.CropResponse;
import com.bicap.entity.Crop;
import com.bicap.exception.BadRequestException;
import com.bicap.exception.ResourceNotFoundException;
import com.bicap.mapper.CropMapper;
import com.bicap.repository.CropRepository;
import com.bicap.service.CropService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CropServiceImpl implements CropService {

    private final CropRepository cropRepository;
    private final CropMapper cropMapper;

    /**
     * Get all active crops.
     */
    @Override
    @Transactional(readOnly = true)
    public List<CropResponse> getAll() {

        return cropMapper.toResponseList(
                cropRepository.findByStatus(AccountStatus.ACTIVE)
        );
    }

    /**
     * Get crop by id.
     */
    @Override
    @Transactional(readOnly = true)
    public CropResponse getById(Long cropId) {

        return cropMapper.toResponse(
                getActiveCrop(cropId)
        );
    }

    /**
     * Create new crop.
     */
    @Override
    @Transactional
    public CropResponse create(CreateCropRequest request) {

        validateCreateRequest(request);

        Crop crop = cropMapper.toEntity(request);

        crop.setStatus(AccountStatus.ACTIVE);

        crop = cropRepository.save(crop);

        return cropMapper.toResponse(crop);
    }

    /**
     * Update crop.
     */
    @Override
    @Transactional
    public CropResponse update(
            Long cropId,
            UpdateCropRequest request) {

        Crop crop = getCrop(cropId);

        validateUpdateRequest(cropId, request);

        cropMapper.updateEntity(request, crop);

        crop = cropRepository.save(crop);

        return cropMapper.toResponse(crop);
    }

    /**
     * Change crop status.
     */
    @Override
    @Transactional
    public CropResponse changeStatus(Long cropId) {

        Crop crop = getCrop(cropId);

        if (crop.getStatus() == AccountStatus.ACTIVE) {
            crop.setStatus(AccountStatus.INACTIVE);
        } else {
            crop.setStatus(AccountStatus.ACTIVE);
        }

        crop = cropRepository.save(crop);

        return cropMapper.toResponse(crop);
    }

    // =====================================================
    // PRIVATE METHODS
    // =====================================================

    private Crop getCrop(Long cropId) {

        return cropRepository.findById(cropId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Crop not found."));
    }

    private Crop getActiveCrop(Long cropId) {

        return cropRepository.findByCropIdAndStatus(
                        cropId,
                        AccountStatus.ACTIVE
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException("Crop not found."));
    }

    private void validateCreateRequest(CreateCropRequest request) {

        if (cropRepository.existsByCropNameIgnoreCase(request.getCropName())) {
            throw new BadRequestException("Crop name already exists.");
        }
    }

    private void validateUpdateRequest(
            Long cropId,
            UpdateCropRequest request) {

        cropRepository.findByCropNameIgnoreCase(request.getCropName())
                .ifPresent(existing -> {

                    if (!existing.getCropId().equals(cropId)) {
                        throw new BadRequestException("Crop name already exists.");
                    }

                });
    }

}