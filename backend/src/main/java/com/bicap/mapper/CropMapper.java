package com.bicap.mapper;

import com.bicap.dto.request.crop.CreateCropRequest;
import com.bicap.dto.request.crop.UpdateCropRequest;
import com.bicap.dto.response.crop.CropResponse;
import com.bicap.entity.Crop;
import org.mapstruct.BeanMapping;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CropMapper {

    /**
     * Create DTO -> Entity
     */
    @Mapping(target = "cropId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "products", ignore = true)
    Crop toEntity(CreateCropRequest request);

    /**
     * Update DTO -> Entity
     */
    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "cropId", ignore = true)
    @Mapping(target = "status", ignore = true)
    @Mapping(target = "products", ignore = true)
    void updateEntity(UpdateCropRequest request,
                      @MappingTarget Crop crop);

    /**
     * Entity -> Response
     */
    CropResponse toResponse(Crop crop);

    /**
     * Entity List -> Response List
     */
    List<CropResponse> toResponseList(List<Crop> crops);

}