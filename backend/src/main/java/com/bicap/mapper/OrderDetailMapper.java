package com.bicap.mapper;

import com.bicap.dto.response.order.OrderDetailResponse;
import com.bicap.entity.OrderDetail;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface OrderDetailMapper {

    @Mapping(source = "detailId", target = "detailId")
    @Mapping(source = "productBatch.batchId", target = "batchId")
    @Mapping(source = "productBatch.batchCode", target = "batchCode")
    @Mapping(source = "productBatch.product.productName", target = "productName")
    @Mapping(source = "productBatch.grade", target = "grade")
    @Mapping(source = "quantity", target = "quantity")
    @Mapping(source = "unitPrice", target = "unitPrice")
    @Mapping(target = "subTotal", ignore = true)
    OrderDetailResponse toResponse(OrderDetail detail);

}