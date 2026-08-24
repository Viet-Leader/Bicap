package com.bicap.mapper;

import com.bicap.dto.response.productImage.ProductImageResponse;
import com.bicap.entity.ProductImage;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.List;

@Mapper(componentModel = "spring")
public interface ProductImageMapper {

    /**
     * Entity -> Response
     */
    @Mapping(target = "imageId", source = "imageId")
    @Mapping(target = "imageUrl", source = "imageUrl")
    @Mapping(target = "displayOrder", source = "displayOrder")
    ProductImageResponse toResponse(ProductImage image);

    /**
     * Entity List -> Response List
     */
    List<ProductImageResponse> toResponseList(List<ProductImage> images);

}