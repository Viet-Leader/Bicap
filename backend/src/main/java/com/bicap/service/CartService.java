package com.bicap.service;

import com.bicap.dto.request.cart.AddCartItemRequest;
import com.bicap.dto.request.cart.UpdateCartItemRequest;
import com.bicap.dto.response.cart.CartResponse;

public interface CartService {

    CartResponse getMyCart();

    CartResponse addItem(AddCartItemRequest request);

    CartResponse updateItem(Long cartItemId,
                            UpdateCartItemRequest request);

    void removeItem(Long cartItemId);

    void clearCart();

}
