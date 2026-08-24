package com.bicap.service.impl;

import com.bicap.dto.request.productImage.ReorderProductImageRequest;
import com.bicap.dto.response.productImage.ProductImageResponse;
import com.bicap.entity.ProductBatch;
import com.bicap.entity.ProductImage;
import com.bicap.exception.ResourceNotFoundException;
import com.bicap.mapper.ProductImageMapper;
import com.bicap.repository.ProductBatchRepository;
import com.bicap.repository.ProductImageRepository;
import com.bicap.service.FileStorageService;
import com.bicap.service.ProductImageService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class ProductImageServiceImpl implements ProductImageService {

    private final ProductImageRepository productImageRepository;

    private final ProductBatchRepository productBatchRepository;

    private final ProductImageMapper productImageMapper;

    private final FileStorageService fileStorageService;

    /**
     * Upload images.
     */
    @Override
    public List<ProductImageResponse> upload(
            Long batchId,
            List<MultipartFile> files
    ) {

        ProductBatch batch = getBatch(batchId);

        int nextDisplayOrder = productImageRepository
                .findTopByProductBatchBatchIdOrderByDisplayOrderDesc(batchId)
                .map(ProductImage::getDisplayOrder)
                .orElse(0);

        List<ProductImageResponse> responses = new ArrayList<>();

        for (MultipartFile file : files) {

            String imageUrl =
                    fileStorageService.storeProductImage(file);

            ProductImage image = ProductImage.builder()
                    .productBatch(batch)
                    .imageUrl(imageUrl)
                    .displayOrder(++nextDisplayOrder)
                    .build();

            image = productImageRepository.save(image);

            responses.add(
                    productImageMapper.toResponse(image)
            );
        }

        return responses;
    }
        /**
     * Reorder images.
     */
    @Override
public List<ProductImageResponse> reorder(
        Long batchId,
        ReorderProductImageRequest request
) {

    ProductBatch batch = getBatch(batchId);

    List<ProductImage> images =
            productImageRepository
                    .findByProductBatchBatchIdOrderByDisplayOrderAsc(
                            batch.getBatchId()
                    );

    if (images.size() != request.getImageIds().size()) {
        throw new ResourceNotFoundException(
                "Invalid image list."
        );
    }

    Map<Long, ProductImage> imageMap = images.stream()
            .collect(Collectors.toMap(
                    ProductImage::getImageId,
                    Function.identity()
            ));

    int displayOrder = 1;

    for (Long imageId : request.getImageIds()) {

        ProductImage image = imageMap.get(imageId);

        if (image == null) {
            throw new ResourceNotFoundException(
                    "Image not found."
            );
        }

        image.setDisplayOrder(displayOrder++);
    }

    productImageRepository.saveAll(images);

    return productImageMapper.toResponseList(
            productImageRepository
                    .findByProductBatchBatchIdOrderByDisplayOrderAsc(batchId)
    );
}

    /**
     * Delete image.
     */
    @Override
    public void delete(Long imageId) {

        ProductImage image = getImage(imageId);

        fileStorageService.delete(
                image.getImageUrl()
        );

        productImageRepository.delete(image);

        // Đánh lại displayOrder
        List<ProductImage> remain =
                productImageRepository
                        .findByProductBatchBatchIdOrderByDisplayOrderAsc(
                                image.getProductBatch().getBatchId()
                        );

        int order = 1;

        for (ProductImage item : remain) {
            item.setDisplayOrder(order++);
        }

        productImageRepository.saveAll(remain);
    }
    @Override
@Transactional(readOnly = true)
public List<ProductImageResponse> getByBatch(Long batchId) {

    getBatch(batchId);

    return productImageMapper.toResponseList(
            productImageRepository
                    .findByProductBatchBatchIdOrderByDisplayOrderAsc(batchId)
    );
}
@Override
@Transactional(readOnly = true)
public String getThumbnail(Long productId) {

    return productImageRepository
            .findFirstByProductBatchProductProductIdOrderByDisplayOrderAsc(productId)
            .map(ProductImage::getImageUrl)
            .orElse(null);
}
private ProductBatch getBatch(Long batchId) {

    return productBatchRepository
            .findByBatchId(batchId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Product batch not found."
                    ));
}
private ProductImage getImage(Long imageId) {

    return productImageRepository
            .findByImageId(imageId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Product image not found."
                    ));
}
}