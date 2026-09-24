package com.bicap.mapper;

import com.bicap.dto.response.cart.CartItemResponse;
import com.bicap.entity.CartItem;
import com.bicap.entity.ProductBatch;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface CartItemMapper {

    // ==========================================================
    // CART ITEM RESPONSE
    // ==========================================================

    @Mapping(source = "cartItemId", target = "cartItemId")
    @Mapping(source = "productBatch.batchId", target = "batchId")
    @Mapping(source = "productBatch.batchCode", target = "batchCode")
    @Mapping(source = "productBatch.product.productName", target = "productName")
    @Mapping(source = "productBatch.grade", target = "grade")
    @Mapping(source = "productBatch.unitPrice", target = "unitPrice")
    @Mapping(source = "productBatch.remainingQuantity", target = "remainingQuantity")
    @Mapping(target = "thumbnail", expression = "java(getThumbnail(cartItem.getProductBatch()))")
    @Mapping(target = "subTotal", ignore = true)
    CartItemResponse toResponse(CartItem cartItem);

    default String getThumbnail(ProductBatch batch) {
        if (batch == null || batch.getProductImages() == null || batch.getProductImages().isEmpty()) {
            return null;
        }
        return batch.getProductImages().stream()
                .sorted(java.util.Comparator.comparing(image -> image.getDisplayOrder() == null ? Integer.MAX_VALUE : image.getDisplayOrder()))
                .findFirst()
                .map(image -> image.getImageUrl())
                .orElse(null);
    }

}