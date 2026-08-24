package com.bicap.mapper;

import com.bicap.dto.request.account.CreateAccountRequest;
import com.bicap.dto.request.farm.UpdateFarmRequest;
import com.bicap.dto.response.farm.FarmResponse;
import com.bicap.entity.Farm;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;

@Mapper(componentModel = "spring")
public interface FarmMapper {

    @Mapping(target = "farmId", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)

    @Mapping(target = "products", ignore = true)
    @Mapping(target = "farmingSeasons", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "carts", ignore = true)     // <-- thêm

    Farm toFarm(CreateAccountRequest request);

    FarmResponse toResponse(Farm farm);

    @Mapping(target = "farmId", ignore = true)
    @Mapping(target = "account", ignore = true)
    @Mapping(target = "businessLicense", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)

    @Mapping(target = "products", ignore = true)
    @Mapping(target = "farmingSeasons", ignore = true)
    @Mapping(target = "orders", ignore = true)
    @Mapping(target = "carts", ignore = true)     // <-- thêm

    void updateFarm(
            UpdateFarmRequest request,
            @MappingTarget Farm farm
    );
}