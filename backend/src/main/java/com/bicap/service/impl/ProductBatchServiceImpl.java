package com.bicap.service.impl;

import com.bicap.common.enums.ProductBatchStatus;
import com.bicap.dto.blockchain.FarmingActivityBlockchainPayload;
import com.bicap.dto.blockchain.ProductBatchBlockchainPayload;
import com.bicap.dto.request.productBatch.CreateProductBatchRequest;
import com.bicap.dto.request.productBatch.UpdateProductBatchRequest;
import com.bicap.dto.response.blockchain.BlockchainTransactionResponse;
import com.bicap.dto.response.productBatch.ProductBatchDetailResponse;
import com.bicap.dto.response.productBatch.ProductBatchSummaryResponse;
import com.bicap.entity.Farm;
import com.bicap.entity.FarmingSeason;
import com.bicap.entity.Product;
import com.bicap.entity.ProductBatch;
import com.bicap.entity.SeasonActivity;
import com.bicap.exception.BadRequestException;
import com.bicap.exception.ResourceNotFoundException;
import com.bicap.generator.ProductBatchCodeGenerator;
import com.bicap.generator.QrCodeGenerator;
import com.bicap.mapper.BlockchainTransactionMapper;
import com.bicap.mapper.ProductBatchMapper;
import com.bicap.repository.FarmRepository;
import com.bicap.repository.FarmingSeasonRepository;
import com.bicap.repository.ProductBatchRepository;
import com.bicap.repository.ProductRepository;
import com.bicap.security.SecurityUtils;
import com.bicap.service.BlockchainService;
import com.bicap.service.ProductBatchService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

@Service
@RequiredArgsConstructor
public class ProductBatchServiceImpl implements ProductBatchService {

    private final ProductBatchRepository productBatchRepository;

    private final ProductRepository productRepository;

    private final FarmingSeasonRepository farmingSeasonRepository;

    private final FarmRepository farmRepository;

    private final ProductBatchMapper productBatchMapper;

    private final ProductBatchCodeGenerator batchCodeGenerator;

    private final QrCodeGenerator qrCodeGenerator;

    private final BlockchainService blockchainService;

    /**
     * Lấy Farm của tài khoản hiện tại.
     */
    private Farm getCurrentFarm() {

        Long accountId = SecurityUtils.getCurrentAccountId();

        if (accountId == null) {
            throw new BadRequestException("Unauthenticated.");
        }

        return farmRepository
                .findByAccount_AccountId(accountId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Farm not found."));
    }

    /**
     * Lấy Product thuộc Farm hiện tại.
     */
    private Product getMyProduct(Long productId) {

        Farm farm = getCurrentFarm();

        return productRepository
                .findByProductIdAndFarmFarmId(
                        productId,
                        farm.getFarmId()
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException("Product not found."));
    }

    /**
     * Lấy Batch thuộc Farm hiện tại.
     */
    private ProductBatch getMyBatch(Long batchId) {

        ProductBatch batch = productBatchRepository
                .findByBatchId(batchId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Batch not found."));

        Farm farm = getCurrentFarm();

        if (!batch.getProduct()
                .getFarm()
                .getFarmId()
                .equals(farm.getFarmId())) {

            throw new ResourceNotFoundException("Batch not found.");
        }

        return batch;
    }

    /**
     * Lấy Season thuộc Farm hiện tại.
     */
    private FarmingSeason getSeason(Long seasonId) {

        Farm farm = getCurrentFarm();

        FarmingSeason season = farmingSeasonRepository
                .findBySeasonId(seasonId)
                .orElseThrow(() ->
                        new ResourceNotFoundException("Season not found."));

        if (!season.getFarm()
                .getFarmId()
                .equals(farm.getFarmId())) {

            throw new ResourceNotFoundException("Season not found.");
        }

        return season;
    }
@Override
public Page<ProductBatchSummaryResponse> getByProduct(
        Long productId,
        Pageable pageable
) {

    Product product = getMyProduct(productId);

    Page<ProductBatch> batches =
            productBatchRepository.findByProductProductId(
                    product.getProductId(),
                    pageable
            );

    return batches.map(productBatchMapper::toSummary);
}

@Override
public ProductBatchDetailResponse getById(Long batchId) {

    ProductBatch batch = getMyBatch(batchId);

    return productBatchMapper.toDetail(batch);
}
@Override
public ProductBatchDetailResponse create(
        Long productId,
        CreateProductBatchRequest request
) {

    Product product = getMyProduct(productId);

    FarmingSeason season = getSeason(request.getSeasonId());

    ProductBatch batch =
            productBatchMapper.toEntity(request);

    batch.setProduct(product);

    batch.setFarmingSeason(season);

    batch.setStatus(ProductBatchStatus.AVAILABLE);

    batch.setRemainingQuantity(
            request.getQuantity()
    );

    // save lần 1 để có batchId
    batch = productBatchRepository.save(batch);

    batch.setBatchCode(
            batchCodeGenerator.generate(
                    batch.getBatchId()
            )
    );

    batch.setQrCode(
            qrCodeGenerator.generate()
    );

    // save lần 2
    batch = productBatchRepository.save(batch);

    return productBatchMapper.toDetail(batch);
}
@Override
public ProductBatchDetailResponse update(
        Long batchId,
        UpdateProductBatchRequest request
) {

    ProductBatch batch = getMyBatch(batchId);

    if (batch.getRemainingQuantity()
            .compareTo(request.getQuantity()) > 0) {

        throw new BadRequestException(
                "Remaining quantity cannot exceed quantity."
        );
    }

    productBatchMapper.updateEntity(
            request,
            batch
    );

    batch = productBatchRepository.save(batch);

    return productBatchMapper.toDetail(batch);
}

@Override
public ProductBatchDetailResponse getByQrCode(
        String qrCode
) {

    ProductBatch batch =
            productBatchRepository
                    .findByQrCode(qrCode)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Batch not found."
                            ));

    return productBatchMapper.toDetail(batch);
}
   @Override
public Page<ProductBatchSummaryResponse> getPublicByProduct(
        Long productId,
        Pageable pageable
) {

    // Kiểm tra Product có tồn tại hay không
    Product product = productRepository
            .findByProductId(productId)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Product not found."));

    Page<ProductBatch> batches =
            productBatchRepository.findByProductProductIdAndStatus(
                    product.getProductId(),
                    ProductBatchStatus.AVAILABLE,
                    pageable
            );

    return batches.map(productBatchMapper::toSummary);
}
@Override
public void decreaseRemainingQuantity(
        Long batchId,
        BigDecimal quantity
) {

    ProductBatch batch = productBatchRepository
            .findByBatchId(batchId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Product batch not found."
                    ));

    if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
        throw new BadRequestException(
                "Quantity must be greater than zero."
        );
    }

    if (batch.getRemainingQuantity().compareTo(quantity) < 0) {
        throw new BadRequestException(
                "Insufficient remaining quantity."
        );
    }

    batch.setRemainingQuantity(
            batch.getRemainingQuantity()
                    .subtract(quantity)
    );

    updateBatchStatus(batch);

    productBatchRepository.save(batch);

}
@Override
public void increaseRemainingQuantity(
        Long batchId,
        BigDecimal quantity
) {

    ProductBatch batch = productBatchRepository
            .findByBatchId(batchId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Product batch not found."
                    ));

    if (quantity.compareTo(BigDecimal.ZERO) <= 0) {
        throw new BadRequestException(
                "Quantity must be greater than zero."
        );
    }

    batch.setRemainingQuantity(
            batch.getRemainingQuantity()
                    .add(quantity)
    );

    updateBatchStatus(batch);

    productBatchRepository.save(batch);

}
private void updateBatchStatus(ProductBatch batch) {

    if (batch.getRemainingQuantity().compareTo(BigDecimal.ZERO) == 0) {

        batch.setStatus(ProductBatchStatus.SOLD_OUT);

    } else {

        batch.setStatus(ProductBatchStatus.AVAILABLE);

    }

}
@Override
public BlockchainTransactionResponse recordToBlockchain(
        Long batchId
) {

    // 1. Lấy Batch thuộc Farm hiện tại
    ProductBatch batch = getMyBatch(batchId);

    // 2. Lấy FarmingSeason
    FarmingSeason season = batch.getFarmingSeason();

    if (season == null) {
        throw new ResourceNotFoundException(
                "Farming season not found."
        );
    }

    // 3. Chỉ được ghi Blockchain sau khi đã thu hoạch
    if (season.getActualHarvestDate() == null) {
        throw new BadRequestException(
                "Actual harvest date is required before recording to blockchain."
        );
    }

    // 4. Tạo danh sách farming activities
    List<FarmingActivityBlockchainPayload> activities =
            season.getActivities()
                    .stream()
                    .map(this::toActivityPayload)
                    .toList();

    // 5. Tạo Blockchain Payload
    ProductBatchBlockchainPayload payload =
            ProductBatchBlockchainPayload.builder()
                    .batchId(batch.getBatchId())
                    .batchCode(batch.getBatchCode())
                    .productId(
                            batch.getProduct().getProductId()
                    )
                    .farmId(
                            batch.getProduct()
                                    .getFarm()
                                    .getFarmId()
                    )
                    .seasonId(season.getSeasonId())
                    .harvestDate(
                            season.getActualHarvestDate()
                    )
                    .quantity(
                            batch.getQuantity()
                    )
                    .activities(activities)
                    .build();

    // 6. Ghi lên Blockchain
    return blockchainService.recordProductBatch(payload);
}
private FarmingActivityBlockchainPayload toActivityPayload(
        SeasonActivity activity
) {

    return FarmingActivityBlockchainPayload.builder()
            .activityId(activity.getActivityId())
            .activityTime(activity.getActivityTime())
            .activityType(
                    activity.getActivityType()
                            .getActivityName()
            )
            .description(activity.getDescription())
            .build();
}
}