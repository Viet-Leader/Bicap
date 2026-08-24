package com.bicap.service;

import com.bicap.dto.response.order.OrderResponse;

import java.util.List;

public interface OrderService {

    // UC14
    OrderResponse checkout();

    // Retailer
    List<OrderResponse> getRetailerOrders();

    OrderResponse getRetailerOrder(Long orderId);

    // Farm
    List<OrderResponse> getFarmOrders();

    OrderResponse getFarmOrder(Long orderId);

    // Farm
    OrderResponse confirmOrder(Long orderId);

    // Farm & Retailer
    OrderResponse cancelOrder(Long orderId);

    // Admin
    OrderResponse completeOrder(Long orderId);

}