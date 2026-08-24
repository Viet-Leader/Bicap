package com.bicap.mapper;

import com.bicap.dto.request.productBatch.CreateProductBatchRequest;
import com.bicap.dto.request.productBatch.UpdateProductBatchRequest;
import com.bicap.dto.response.productBatch.ProductBatchDetailResponse;
import com.bicap.dto.response.productBatch.ProductBatchSummaryResponse;
import com.bicap.entity.ProductBatch;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {
                ProductImageMapper.class
        }
)
public interface ProductBatchMapper {

    @Mapping(target = "batchId", ignore = true)
    @Mapping(target = "farmingSeason", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "batchCode", ignore = true)
    @Mapping(target = "qrCode", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "remainingQuantity", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "productImages", ignore = true)
    @Mapping(target = "cartItems", ignore = true)   // <-- thêm
    ProductBatch toEntity(CreateProductBatchRequest request);

    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "batchId", ignore = true)
    @Mapping(target = "farmingSeason", ignore = true)
    @Mapping(target = "product", ignore = true)
    @Mapping(target = "batchCode", ignore = true)
    @Mapping(target = "qrCode", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "remainingQuantity", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "productImages", ignore = true)
    @Mapping(target = "cartItems", ignore = true)   // <-- thêm
    void updateEntity(
            UpdateProductBatchRequest request,
            @MappingTarget ProductBatch batch
    );

    ProductBatchSummaryResponse toSummary(ProductBatch batch);

    @Mapping(target = "productName", source = "product.productName")
    @Mapping(target = "cropName", source = "product.crop.cropName")
    @Mapping(target = "farmName", source = "product.farm.farmName")
    @Mapping(target = "seasonName", source = "farmingSeason.seasonName")
    @Mapping(target = "images", source = "productImages")
    ProductBatchDetailResponse toDetail(ProductBatch batch);

    List<ProductBatchSummaryResponse> toSummaryList(
            List<ProductBatch> batches
    );
}