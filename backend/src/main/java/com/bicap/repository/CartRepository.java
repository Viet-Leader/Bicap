package com.bicap.repository;

import com.bicap.entity.Cart;
import com.bicap.entity.Retailer;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface CartRepository extends JpaRepository<Cart, Long> {

    Optional<Cart> findByRetailer_RetailerId(Long retailerId);

    boolean existsByRetailer_RetailerId(Long retailerId);

    Optional<Cart> findByRetailer(Retailer retailer);

    Optional<Cart> findByCartId(Long cartId);

}