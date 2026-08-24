package com.bicap.repository;

import com.bicap.common.enums.OrderStatus;
import com.bicap.entity.Farm;
import com.bicap.entity.Orders;
import com.bicap.entity.Retailer;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface OrdersRepository extends JpaRepository<Orders, Long> {

    Optional<Orders> findByOrderId(Long orderId);

    List<Orders> findByRetailer(Retailer retailer);

    List<Orders> findByFarm(Farm farm);

    List<Orders> findByRetailerOrderByCreatedAtDesc(Retailer retailer);

    List<Orders> findByFarmOrderByCreatedAtDesc(Farm farm);

    List<Orders> findByStatus(OrderStatus status);

    Optional<Orders> findByOrderIdAndRetailer(
            Long orderId,
            Retailer retailer
    );

    Optional<Orders> findByOrderIdAndFarm(
            Long orderId,
            Farm farm
    );

}