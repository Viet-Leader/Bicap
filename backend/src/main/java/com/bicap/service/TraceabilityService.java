package com.bicap.service;

import com.bicap.dto.response.traceability.TraceabilityResponse;

public interface TraceabilityService {

    TraceabilityResponse getByBatchId(Long batchId);

}