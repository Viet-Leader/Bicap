package com.bicap.service.impl;

import com.bicap.common.enums.ProductBatchStatus;
import com.bicap.dto.request.cart.AddCartItemRequest;
import com.bicap.dto.request.cart.UpdateCartItemRequest;
import com.bicap.dto.response.cart.CartResponse;
import com.bicap.entity.Cart;
import com.bicap.entity.CartItem;
import com.bicap.entity.Farm;
import com.bicap.entity.ProductBatch;
import com.bicap.entity.Retailer;
import com.bicap.exception.BadRequestException;
import com.bicap.exception.ResourceNotFoundException;
import com.bicap.exception.UnauthorizedException;
import com.bicap.mapper.CartMapper;
import com.bicap.repository.CartItemRepository;
import com.bicap.repository.CartRepository;
import com.bicap.repository.ProductBatchRepository;
import com.bicap.repository.RetailerRepository;
import com.bicap.security.SecurityUtils;
import com.bicap.service.CartService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.Optional;
@Service
@RequiredArgsConstructor
@Transactional
public class CartServiceImpl implements CartService {

    private final CartRepository cartRepository;
    private final CartItemRepository cartItemRepository;
    private final RetailerRepository retailerRepository;
    private final ProductBatchRepository productBatchRepository;

    private final CartMapper cartMapper;

    private Retailer getCurrentRetailer() {

    Long accountId = SecurityUtils.getCurrentAccountId();

    if (accountId == null) {
        throw new UnauthorizedException(
                "Please login first."
        );
    }

    return retailerRepository
            .findByAccount_AccountId(accountId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Retailer not found."
                    ));
}
private Cart getOrCreateCart(Retailer retailer,
                             Farm farm) {

    return cartRepository
            .findByRetailer(retailer)
            .orElseGet(() -> {

                Cart cart = Cart.builder()
                        .retailer(retailer)
                        .farm(farm)
                        .build();

                return cartRepository.save(cart);

            });

}
private ProductBatch validateBatch(Long batchId) {

    ProductBatch batch =
            productBatchRepository.findByBatchId(batchId)
                    .orElseThrow(() ->
                            new ResourceNotFoundException(
                                    "Product batch not found."
                            ));

    if (batch.getStatus() != ProductBatchStatus.AVAILABLE) {

        throw new BadRequestException(
                "Product batch is not available."
        );

    }

    return batch;

}
private void validateQuantity(
        BigDecimal quantity,
        ProductBatch batch
) {

    if (quantity.compareTo(BigDecimal.ZERO) <= 0) {

        throw new BadRequestException(
                "Quantity must be greater than zero."
        );

    }

    if (quantity.compareTo(batch.getRemainingQuantity()) > 0) {

        throw new BadRequestException(
                "Quantity exceeds remaining quantity."
        );

    }

}
private void validateSameFarm(
        Cart cart,
        ProductBatch batch
) {

    Long cartFarmId =
            cart.getFarm().getFarmId();

    Long batchFarmId =
            batch.getProduct()
                    .getFarm()
                    .getFarmId();

    if (!cartFarmId.equals(batchFarmId)) {

        throw new BadRequestException(
                "All product batches must belong to the same farm."
        );

    }

}
private Optional<CartItem> findExistingCartItem(
        Cart cart,
        ProductBatch batch
) {

    return cartItemRepository
            .findByCartAndProductBatch(
                    cart,
                    batch
            );

}
private BigDecimal calculateTotal(Cart cart) {

    return cart.getCartItems()
            .stream()
            .map(item ->
                    item.getQuantity()
                            .multiply(
                                    item.getProductBatch()
                                            .getUnitPrice()
                            )
            )
            .reduce(
                    BigDecimal.ZERO,
                    BigDecimal::add
            );

}
private CartResponse buildResponse(
        Cart cart
) {

    CartResponse response =
            cartMapper.toResponse(cart);

    response.setTotalAmount(
            calculateTotal(cart)
    );

    response.getItems().forEach(item ->

            item.setSubTotal(
                    item.getQuantity()
                            .multiply(
                                    item.getUnitPrice()
                            )
            )

    );

    return response;

}
@Override
@Transactional
public CartResponse getMyCart() {

    Retailer retailer = getCurrentRetailer();

    Cart cart = cartRepository
            .findByRetailer(retailer)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Cart not found."
                    ));

    return buildResponse(cart);
}
@Override
public CartResponse addItem(AddCartItemRequest request) {

    ProductBatch batch = validateBatch(request.getBatchId());

    validateQuantity(
            request.getQuantity(),
            batch
    );

    Retailer retailer = getCurrentRetailer();

    Farm farm = batch.getProduct().getFarm();

    Cart cart = getOrCreateCart(
            retailer,
            farm
    );

    validateSameFarm(
            cart,
            batch
    );

    Optional<CartItem> existingItem =
            findExistingCartItem(
                    cart,
                    batch
            );

    if (existingItem.isPresent()) {

        CartItem item = existingItem.get();

        BigDecimal newQuantity =
                item.getQuantity()
                        .add(request.getQuantity());

        validateQuantity(
                newQuantity,
                batch
        );

        item.setQuantity(newQuantity);

        cartItemRepository.save(item);

    } else {

        CartItem item = CartItem.builder()
                .cart(cart)
                .productBatch(batch)
                .quantity(request.getQuantity())
                .build();

        cartItemRepository.save(item);

        cart.getCartItems().add(item);
    }

    return buildResponse(cart);

}
@Override
public CartResponse updateItem(
        Long cartItemId,
        UpdateCartItemRequest request
) {

    CartItem item = cartItemRepository
            .findByCartItemId(cartItemId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Cart item not found."
                    ));

    Retailer retailer = getCurrentRetailer();

    if (!item.getCart()
            .getRetailer()
            .getRetailerId()
            .equals(retailer.getRetailerId())) {

        throw new UnauthorizedException(
                "You cannot update this cart."
        );
    }

    validateQuantity(
            request.getQuantity(),
            item.getProductBatch()
    );

    item.setQuantity(request.getQuantity());

    cartItemRepository.save(item);

    return buildResponse(
            item.getCart()
    );

}
@Override
public void removeItem(Long cartItemId) {

    CartItem item = cartItemRepository
            .findByCartItemId(cartItemId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Cart item not found."
                    ));

    Retailer retailer = getCurrentRetailer();

    if (!item.getCart()
            .getRetailer()
            .getRetailerId()
            .equals(retailer.getRetailerId())) {

        throw new UnauthorizedException(
                "You cannot delete this cart item."
        );
    }

    Cart cart = item.getCart();

    cartItemRepository.delete(item);

    cart.getCartItems().remove(item);

    if (cart.getCartItems().isEmpty()) {

        cartRepository.delete(cart);

    }

}
@Override
public void clearCart() {

    Retailer retailer = getCurrentRetailer();

    Cart cart = cartRepository
            .findByRetailer(retailer)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Cart not found."
                    ));

    cartRepository.delete(cart);

}
}
