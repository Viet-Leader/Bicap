package com.bicap.service;

import com.bicap.dto.request.farm.UpdateFarmRequest;
import com.bicap.dto.response.farm.FarmResponse;

import java.util.List;
import com.bicap.common.enums.AccountStatus;

public interface FarmService {

    FarmResponse getMyFarm();

    FarmResponse updateMyFarm(UpdateFarmRequest request);

    FarmResponse getFarmById(Long farmId);

    List<FarmResponse> getAllFarms();

    FarmResponse changeFarmStatus(Long farmId, AccountStatus status);

}
