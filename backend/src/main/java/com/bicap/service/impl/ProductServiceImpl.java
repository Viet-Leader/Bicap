package com.bicap.service.impl;

import com.bicap.common.enums.AccountStatus;
import com.bicap.dto.request.product.CreateProductRequest;
import com.bicap.dto.request.product.UpdateProductRequest;
import com.bicap.dto.response.product.ProductDetailResponse;
import com.bicap.dto.response.product.ProductSummaryResponse;
import com.bicap.entity.Crop;
import com.bicap.entity.Farm;
import com.bicap.entity.Product;
import com.bicap.exception.BadRequestException;
import com.bicap.exception.ResourceNotFoundException;
import com.bicap.mapper.ProductMapper;
import com.bicap.repository.CropRepository;
import com.bicap.repository.FarmRepository;
import com.bicap.repository.ProductRepository;
import com.bicap.security.SecurityUtils;
import com.bicap.service.ProductImageService;
import com.bicap.service.ProductService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ProductServiceImpl implements ProductService {

    private final ProductRepository productRepository;

    private final FarmRepository farmRepository;

    private final CropRepository cropRepository;

    private final ProductMapper productMapper;

    private final ProductImageService productImageService;

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
    private Product getMyProductEntity(Long productId) {

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
     * Lấy Crop ACTIVE.
     */
    private Crop getCrop(Long cropId) {

        return cropRepository
                .findByCropIdAndStatus(
                        cropId,
                        AccountStatus.ACTIVE
                )
                .orElseThrow(() ->
                        new ResourceNotFoundException("Crop not found."));
    }

    /**
     * Mapping ProductSummary và bổ sung thumbnail.
     */
    private ProductSummaryResponse toSummary(Product product) {

        ProductSummaryResponse response =
                productMapper.toSummary(product);

        response.setThumbnail(
                productImageService.getThumbnail(
                        product.getProductId()
                )
        );

        return response;
    }

@Override
public Page<ProductSummaryResponse> getMyProducts(
        String keyword,
        Pageable pageable
) {

    Farm farm = getCurrentFarm();

    Page<Product> products;

    if (keyword == null || keyword.isBlank()) {

        products = productRepository.findByFarmFarmId(
                farm.getFarmId(),
                pageable
        );

    } else {

        products = farmRepository.searchMyProducts(
                farm.getFarmId(),
                keyword.trim(),
                pageable
        );

    }

    return products.map(this::toSummary);
}
@Override
public ProductDetailResponse getMyProduct(Long productId) {

    Product product = getMyProductEntity(productId);

    return productMapper.toDetail(product);
}
@Override
public ProductDetailResponse create(
        CreateProductRequest request
) {

    Farm farm = getCurrentFarm();

    Crop crop = getCrop(request.getCropId());

    boolean existed =
            productRepository
                    .existsByFarmFarmIdAndCropCropIdAndProductNameIgnoreCase(
                            farm.getFarmId(),
                            crop.getCropId(),
                            request.getProductName()
                    );

    if (existed) {
        throw new BadRequestException(
                "Product already exists."
        );
    }

    Product product =
            productMapper.toEntity(request);

    product.setFarm(farm);

    product.setCrop(crop);

    product.setStatus(AccountStatus.ACTIVE);

    product =
            productRepository.save(product);

    return productMapper.toDetail(product);
}
@Override
public ProductDetailResponse update(
        Long productId,
        UpdateProductRequest request
) {

    Product product = getMyProductEntity(productId);

    Farm farm = getCurrentFarm();

    Crop crop = getCrop(request.getCropId());

    boolean existed =
            productRepository
                    .existsByFarmFarmIdAndCropCropIdAndProductNameIgnoreCaseAndProductIdNot(
                            farm.getFarmId(),
                            crop.getCropId(),
                            request.getProductName(),
                            productId
                    );

    if (existed) {
        throw new BadRequestException(
                "Product already exists."
        );
    }

    productMapper.updateEntity(
            request,
            product
    );

    product.setCrop(crop);

    product = productRepository.save(product);

    return productMapper.toDetail(product);
}
@Override
public ProductDetailResponse changeStatus(
        Long productId
) {

    Product product = getMyProductEntity(productId);

    if (product.getStatus() == AccountStatus.ACTIVE) {

        product.setStatus(AccountStatus.INACTIVE);

    } else {

        product.setStatus(AccountStatus.ACTIVE);

    }

    product = productRepository.save(product);

    return productMapper.toDetail(product);
}
@Override
public Page<ProductSummaryResponse> getPublicProducts(
        String keyword,
        Long cropId,
        Pageable pageable
) {

    Page<Product> products;

    boolean hasKeyword =
            keyword != null &&
            !keyword.isBlank();

    boolean hasCrop =
            cropId != null;

    if (!hasKeyword && !hasCrop) {

        products =
                productRepository.findByStatus(
                        AccountStatus.ACTIVE,
                        pageable
                );

    } else if (hasKeyword && !hasCrop) {

        products =
                productRepository
                        .findByStatusAndProductNameContainingIgnoreCase(
                                AccountStatus.ACTIVE,
                                keyword.trim(),
                                pageable
                        );

    } else if (!hasKeyword) {

        products =
                productRepository
                        .findByStatusAndCropCropId(
                                AccountStatus.ACTIVE,
                                cropId,
                                pageable
                        );

    } else {

        products =
                productRepository
                        .findByStatusAndCropCropIdAndProductNameContainingIgnoreCase(
                                AccountStatus.ACTIVE,
                                cropId,
                                keyword.trim(),
                                pageable
                        );

    }

    return products.map(this::toSummary);
}
@Override
public ProductDetailResponse getPublicProduct(
        Long productId
) {

    Product product =
            productRepository
                    .findByProductIdAndStatus(
                            productId,
                            AccountStatus.ACTIVE
                    )
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Product not found."
                            ));

    return productMapper.toDetail(product);
    }

}
