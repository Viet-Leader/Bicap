package com.bicap.service.impl;

import com.bicap.dto.request.farm.UpdateFarmRequest;
import com.bicap.dto.response.farm.FarmResponse;
import com.bicap.entity.Farm;
import com.bicap.exception.ResourceNotFoundException;
import com.bicap.mapper.FarmMapper;
import com.bicap.repository.FarmRepository;
import com.bicap.security.CustomUserDetails;
import com.bicap.service.FarmService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class FarmServiceImpl implements FarmService {

    private final FarmRepository farmRepository;

    private final FarmMapper farmMapper;

    // ==========================================================
    // GET MY FARM
    // ==========================================================

    @Override
    public FarmResponse getMyFarm() {

        CustomUserDetails currentUser = getCurrentUser();

        Farm farm = getFarmByAccountId(
                currentUser.getAccount().getAccountId()
        );

        return farmMapper.toResponse(farm);
    }

    // ==========================================================
    // UPDATE FARM
    // ==========================================================

    @Override
    @Transactional
    public FarmResponse updateMyFarm(UpdateFarmRequest request) {

        CustomUserDetails currentUser = getCurrentUser();

        Farm farm = getFarmByAccountId(
                currentUser.getAccount().getAccountId()
        );

        farmMapper.updateFarm(
                request,
                farm
        );

        farmRepository.save(farm);

        return farmMapper.toResponse(farm);
    }

    // ==========================================================
    // GET FARM BY ID
    // ==========================================================

    @Override
    public FarmResponse getFarmById(Long farmId) {

        Farm farm = getFarm(farmId);

        return farmMapper.toResponse(farm);
    }

    // ==========================================================
    // PRIVATE METHODS
    // ==========================================================

    /**
     * Lấy Farm theo ID.
     */
    private Farm getFarm(Long farmId) {

        return farmRepository.findById(farmId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Farm not found."
                        ));
    }

    /**
     * Lấy Farm theo Account ID.
     */
    private Farm getFarmByAccountId(Long accountId) {

        return farmRepository.findByAccount_AccountId(accountId)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "Farm not found."
                        ));
    }

    /**
     * Lấy người dùng đang đăng nhập.
     */
    private CustomUserDetails getCurrentUser() {

        Authentication authentication =
                SecurityContextHolder
                        .getContext()
                        .getAuthentication();

        return (CustomUserDetails) authentication.getPrincipal();
    }

}