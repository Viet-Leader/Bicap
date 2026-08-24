package com.bicap.mapper;

import com.bicap.dto.request.product.CreateProductRequest;
import com.bicap.dto.request.product.UpdateProductRequest;
import com.bicap.dto.response.product.ProductDetailResponse;
import com.bicap.dto.response.product.ProductSummaryResponse;
import com.bicap.entity.Product;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(
        componentModel = "spring",
        uses = {
                ProductBatchMapper.class
        }
)
public interface ProductMapper {

    /**
     * Create DTO -> Entity
     */
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "farm", ignore = true)
    @Mapping(target = "crop", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "productBatches", ignore = true)
    Product toEntity(CreateProductRequest request);

    /**
     * Update DTO -> Entity
     */
    @BeanMapping(
            nullValuePropertyMappingStrategy =
                    NullValuePropertyMappingStrategy.IGNORE
    )
    @Mapping(target = "productId", ignore = true)
    @Mapping(target = "farm", ignore = true)
    @Mapping(target = "crop", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "createdAt", ignore = true)
    @Mapping(target = "updatedAt", ignore = true)
    @Mapping(target = "productBatches", ignore = true)
    void updateEntity(
            UpdateProductRequest request,
            @MappingTarget Product product
    );

    /**
     * Entity -> Summary Response
     */
    @Mapping(target = "cropName", source = "crop.cropName")
    @Mapping(target = "farmName", source = "farm.farmName")
    @Mapping(target = "thumbnail", ignore = true)
    ProductSummaryResponse toSummary(Product product);

    /**
     * Entity -> Detail Response
     */
    @Mapping(target = "cropName", source = "crop.cropName")
    @Mapping(target = "farmName", source = "farm.farmName")
    @Mapping(target = "batches", source = "productBatches")
    ProductDetailResponse toDetail(Product product);

    /**
     * Entity List -> Summary Response List
     */
    List<ProductSummaryResponse> toSummaryList(List<Product> products);

}