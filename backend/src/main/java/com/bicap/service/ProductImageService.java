package com.bicap.service;

import com.bicap.dto.request.productImage.ReorderProductImageRequest;
import com.bicap.dto.response.productImage.ProductImageResponse;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

public interface ProductImageService {

    List<ProductImageResponse> upload(
            Long batchId,
            List<MultipartFile> files
    );

    List<ProductImageResponse> reorder(
            Long batchId,
            ReorderProductImageRequest request
    );

    void delete(Long imageId);

    List<ProductImageResponse> getByBatch(Long batchId);

    String getThumbnail(Long productId);

}