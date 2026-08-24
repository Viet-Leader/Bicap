package com.bicap.service;

import java.math.BigDecimal;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import com.bicap.dto.request.productBatch.CreateProductBatchRequest;
import com.bicap.dto.request.productBatch.UpdateProductBatchRequest;
import com.bicap.dto.response.blockchain.BlockchainTransactionResponse;
import com.bicap.dto.response.productBatch.ProductBatchDetailResponse;
import com.bicap.dto.response.productBatch.ProductBatchSummaryResponse;

public interface ProductBatchService {

    Page<ProductBatchSummaryResponse> getByProduct(
            Long productId,
            Pageable pageable
    );

    ProductBatchDetailResponse getById(Long batchId);

    ProductBatchDetailResponse create(
            Long productId,
            CreateProductBatchRequest request
    );

    ProductBatchDetailResponse update(
            Long batchId,
            UpdateProductBatchRequest request
    );

    ProductBatchDetailResponse getByQrCode(
            String qrCode
    );

        Page<ProductBatchSummaryResponse> getPublicByProduct(
        Long productId,
        Pageable pageable
);
void decreaseRemainingQuantity(Long batchId, BigDecimal quantity);

void increaseRemainingQuantity(Long batchId, BigDecimal quantity);

BlockchainTransactionResponse recordToBlockchain(Long batchId);
}
