package com.bicap.mapper;

import com.bicap.dto.request.account.CreateAccountRequest;
import com.bicap.dto.request.auth.RegisterRequest;
import com.bicap.dto.request.retailer.UpdateRetailerRequest;
import com.bicap.dto.response.retailer.RetailerResponse;
import com.bicap.entity.Retailer;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface RetailerMapper {

    // ==========================================================
    // CREATE RETAILER
    // ==========================================================

    @Mapping(target = "retailerId", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "cart", ignore = true)
    Retailer toRetailer(RegisterRequest request);

    // ==========================================================
    // CREATE RETAILER BY ADMIN
    // ==========================================================

    @Mapping(target = "retailerId", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "cart", ignore = true)
    Retailer toRetailer(CreateAccountRequest request);

    // ==========================================================
    // ENTITY -> RESPONSE
    // ==========================================================

    RetailerResponse toResponse(Retailer retailer);

    // ==========================================================
    // UPDATE RETAILER
    // ==========================================================

    @Mapping(target = "retailerId", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "cart", ignore = true) 
    void updateRetailer(
            UpdateRetailerRequest request,
            @MappingTarget Retailer retailer
    );

}