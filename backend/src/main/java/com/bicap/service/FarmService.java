package com.bicap.service;

import com.bicap.dto.request.farm.UpdateFarmRequest;
import com.bicap.dto.response.farm.FarmResponse;

public interface FarmService {

    FarmResponse getMyFarm();

    FarmResponse updateMyFarm(UpdateFarmRequest request);

    FarmResponse getFarmById(Long farmId);

}
