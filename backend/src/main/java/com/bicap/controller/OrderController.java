package com.bicap.controller;
import lombok.RequiredArgsConstructor;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.bicap.dto.response.order.OrderResponse;
import com.bicap.service.OrderService;

@RestController
@RequestMapping("/api/orders")
@RequiredArgsConstructor
public class OrderController {

    private final OrderService orderService;

    @PostMapping("/checkout")
    public ResponseEntity<OrderResponse> checkout() {

        OrderResponse response = orderService.checkout();

        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(response);
    }

    @GetMapping("/retailer")
    public ResponseEntity<List<OrderResponse>> getRetailerOrders() {

        return ResponseEntity.ok(
                orderService.getRetailerOrders()
        );
    }

    @GetMapping("/retailer/{orderId}")
    public ResponseEntity<OrderResponse> getRetailerOrder(
            @PathVariable Long orderId
    ) {

        return ResponseEntity.ok(
                orderService.getRetailerOrder(orderId)
        );
    }

    @GetMapping("/farm")
    public ResponseEntity<List<OrderResponse>> getFarmOrders() {

        return ResponseEntity.ok(
                orderService.getFarmOrders()
        );
    }

    @GetMapping("/farm/{orderId}")
    public ResponseEntity<OrderResponse> getFarmOrder(
            @PathVariable Long orderId
    ) {

        return ResponseEntity.ok(
                orderService.getFarmOrder(orderId)
        );
    }

    @PatchMapping("/{orderId}/confirm")
    public ResponseEntity<OrderResponse> confirmOrder(
            @PathVariable Long orderId
    ) {

        return ResponseEntity.ok(
                orderService.confirmOrder(orderId)
        );
    }

    @PatchMapping("/{orderId}/cancel")
    public ResponseEntity<OrderResponse> cancelOrder(
            @PathVariable Long orderId
    ) {

        return ResponseEntity.ok(
                orderService.cancelOrder(orderId)
        );
    }

    @PatchMapping("/{orderId}/complete")
    public ResponseEntity<OrderResponse> completeOrder(
            @PathVariable Long orderId
    ) {

        return ResponseEntity.ok(
                orderService.completeOrder(orderId)
        );
    }

}