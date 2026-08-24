package com.bicap.mapper;

import com.bicap.dto.response.order.OrderResponse;
import com.bicap.entity.Orders;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(
        componentModel = "spring",
        uses = OrderDetailMapper.class
)
public interface OrderMapper {

    @Mapping(source = "retailer.retailerId", target = "retailerId")
    @Mapping(source = "retailer.retailerName", target = "retailerName")

    @Mapping(source = "farm.farmId", target = "farmId")
    @Mapping(source = "farm.farmName", target = "farmName")

    @Mapping(source = "orderDetails", target = "orderDetails")

    OrderResponse toResponse(Orders order);

}