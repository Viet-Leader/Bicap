package com.bicap.mapper;

import com.bicap.dto.response.cart.CartResponse;
import com.bicap.entity.Cart;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring", uses = CartItemMapper.class)
public interface CartMapper {

    // ==========================================================
    // CART RESPONSE
    // ==========================================================

    @Mapping(source = "cartId", target = "cartId")
    @Mapping(source = "retailer.retailerId", target = "retailerId")
    @Mapping(source = "farm.farmId", target = "farmId")
    @Mapping(source = "cartItems", target = "items")
    @Mapping(target = "totalAmount", ignore = true)
    CartResponse toResponse(Cart cart);

}