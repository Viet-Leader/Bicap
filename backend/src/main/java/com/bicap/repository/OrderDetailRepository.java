package com.bicap.repository;

import com.bicap.entity.OrderDetail;
import com.bicap.entity.Orders;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface OrderDetailRepository
        extends JpaRepository<OrderDetail, Long> {

    List<OrderDetail> findByOrder(Orders order);

}