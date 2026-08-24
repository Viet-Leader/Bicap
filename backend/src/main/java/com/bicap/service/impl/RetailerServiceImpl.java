package com.bicap.service.impl;

import com.bicap.dto.request.retailer.UpdateRetailerRequest;
import com.bicap.dto.response.retailer.RetailerResponse;
import com.bicap.entity.Retailer;
import com.bicap.exception.ResourceNotFoundException;
import com.bicap.mapper.RetailerMapper;
import com.bicap.repository.RetailerRepository;
import com.bicap.security.CustomUserDetails;
import com.bicap.service.RetailerService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RetailerServiceImpl implements RetailerService {

    private final RetailerRepository retailerRepository;

    private final RetailerMapper retailerMapper;

    // ==========================================================
    // GET MY RETAILER
    // ==========================================================

    @Override
    public RetailerResponse getMyRetailer() {

        CustomUserDetails currentUser = getCurrentUser();

        Retailer retailer = getRetailerByAccountId(
                currentUser.getAccount().getAccountId()
        );

        return retailerMapper.toResponse(retailer);
    }

    // ==========================================================
    // UPDATE MY RETAILER
    // ==========================================================

    @Override
    @Transactional
    public RetailerResponse updateMyRetailer(UpdateRetailerRequest request) {

        CustomUserDetails currentUser = getCurrentUser();

        Retailer retailer = getRetailerByAccountId(
                currentUser.getAccount().getAccountId()
        );

        retailerMapper.updateRetailer(request, retailer);

        retailerRepository.save(retailer);

        return retailerMapper.toResponse(retailer);
    }

    // ==========================================================
    // PRIVATE METHODS
    // ==========================================================

    /**
     * Lấy Retailer theo Account ID.
     */
    private Retailer getRetailerByAccountId(Long accountId) {

        return retailerRepository.findByAccount_AccountId(accountId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Retailer not found."));
    }

    /**
     * Lấy người dùng đang đăng nhập.
     */
    private CustomUserDetails getCurrentUser() {

        Authentication authentication = SecurityContextHolder
                .getContext()
                .getAuthentication();

        return (CustomUserDetails) authentication.getPrincipal();
    }

}