package com.bicap.repository;

import com.bicap.entity.Cart;
import com.bicap.entity.CartItem;
import com.bicap.entity.ProductBatch;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface CartItemRepository extends JpaRepository<CartItem, Long> {

    List<CartItem> findByCart(Cart cart);

    Optional<CartItem> findByCartAndProductBatch(
            Cart cart,
            ProductBatch productBatch
    );

    boolean existsByCartAndProductBatch(
            Cart cart,
            ProductBatch productBatch
    );

    void deleteByCart(Cart cart);

    Optional<CartItem> findByCartItemId(Long cartItemId);

}