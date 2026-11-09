package com.bicap.service;

import com.bicap.dto.response.traceability.TraceabilityResponse;
import com.bicap.dto.response.blockchain.BlockchainVerificationResponse;

public interface TraceabilityService {

    TraceabilityResponse getByBatchId(Long batchId);

    BlockchainVerificationResponse verifyByBatchId(Long batchId);

}