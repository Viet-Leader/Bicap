package com.bicap.service.impl;

import com.bicap.common.enums.OrderStatus;
import com.bicap.common.enums.ProductBatchStatus;
import com.bicap.dto.response.order.OrderResponse;
import com.bicap.entity.*;
import com.bicap.exception.BadRequestException;
import com.bicap.exception.ResourceNotFoundException;
import com.bicap.exception.UnauthorizedException;
import com.bicap.mapper.OrderMapper;
import com.bicap.repository.*;
import com.bicap.security.SecurityUtils;
import com.bicap.service.NotificationService;
import com.bicap.service.OrderService;
import com.bicap.service.ProductBatchService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional
public class OrderServiceImpl implements OrderService {

    private final OrdersRepository ordersRepository;
    private final OrderDetailRepository orderDetailRepository;

    private final CartRepository cartRepository;

    private final RetailerRepository retailerRepository;
    private final FarmRepository farmRepository;

    private final NotificationService notificationService;

    private final OrderMapper orderMapper;

    private final ProductBatchService productBatchService;

    private Retailer getCurrentRetailer() {

    Long accountId = SecurityUtils.getCurrentAccountId();

    if (accountId == null) {
        throw new UnauthorizedException("Please login first.");
    }

    return retailerRepository
            .findByAccount_AccountId(accountId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Retailer not found."
                    ));
}
private Farm getCurrentFarm() {

    Long accountId = SecurityUtils.getCurrentAccountId();

    if (accountId == null) {
        throw new UnauthorizedException("Please login first.");
    }

    return farmRepository
            .findByAccount_AccountId(accountId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Farm not found."
                    ));
}
private Orders findOrder(Long orderId) {

    return ordersRepository
            .findByOrderId(orderId)
            .orElseThrow(() ->
                    new ResourceNotFoundException(
                            "Order not found."
                    ));
}
private Orders findRetailerOrder(Long orderId) {

    Retailer retailer = getCurrentRetailer();

    return ordersRepository
            .findByOrderIdAndRetailer(orderId, retailer)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Order not found."));
}
private Orders findFarmOrder(Long orderId) {

    Farm farm = getCurrentFarm();

    return ordersRepository
            .findByOrderIdAndFarm(orderId, farm)
            .orElseThrow(() ->
                    new ResourceNotFoundException("Order not found."));
}
private void validateStatus(
        Orders order,
        OrderStatus status
) {

    if (order.getStatus() != status) {

        throw new BadRequestException(
                "Invalid order status."
        );

    }

}
private BigDecimal calculateTotal(
        List<CartItem> items
) {

    return items.stream()
            .map(item ->
                    item.getQuantity().multiply(
                            item.getProductBatch().getUnitPrice()
                    )
            )
            .reduce(
                    BigDecimal.ZERO,
                    BigDecimal::add
            );

}
//
@Override
@Transactional
public List<OrderResponse> getRetailerOrders() {

    Retailer retailer = getCurrentRetailer();

    return ordersRepository
            .findByRetailerOrderByCreatedAtDesc(retailer)
            .stream()
            .map(orderMapper::toResponse)
            .toList();
}
@Override
@Transactional
public OrderResponse getRetailerOrder(Long orderId) {

    Orders order = findRetailerOrder(orderId);

    return orderMapper.toResponse(order);
}
@Override
@Transactional
public List<OrderResponse> getFarmOrders() {

    Farm farm = getCurrentFarm();

    return ordersRepository
            .findByFarmOrderByCreatedAtDesc(farm)
            .stream()
            .map(orderMapper::toResponse)
            .toList();
}
@Override
@Transactional
public OrderResponse getFarmOrder(Long orderId) {

    Orders order = findFarmOrder(orderId);

    return orderMapper.toResponse(order);
}
@Override
@Transactional
public OrderResponse checkout() {

    // Lấy Retailer hiện tại
    Retailer retailer = getCurrentRetailer();

    // Lấy giỏ hàng
    Cart cart = cartRepository.findByRetailer(retailer)
            .orElseThrow(() ->
                    new BadRequestException("Cart is empty.")
            );

    List<CartItem> items = cart.getCartItems();

    if (items.isEmpty()) {
        throw new BadRequestException("Cart is empty.");
    }

    Farm farm = cart.getFarm();

    // ==========================================================
    // BR01 + BR06
    // Validate toàn bộ Cart trước khi tạo Order
    // ==========================================================

    for (CartItem item : items) {

        ProductBatch batch = item.getProductBatch();

        if (batch.getStatus() != ProductBatchStatus.AVAILABLE) {
            throw new BadRequestException(
                    "Product batch " + batch.getBatchCode() + " is unavailable."
            );
        }

        if (item.getQuantity().compareTo(BigDecimal.ZERO) <= 0) {
            throw new BadRequestException(
                    "Quantity must be greater than zero."
            );
        }

        if (item.getQuantity().compareTo(batch.getRemainingQuantity()) > 0) {
            throw new BadRequestException(
                    "Quantity exceeds remaining quantity of batch "
                            + batch.getBatchCode()
            );
        }

    }

    // ==========================================================
    // BR07
    // ==========================================================

    BigDecimal totalAmount = calculateTotal(items);

    // ==========================================================
    // BR08
    // ==========================================================

    Orders order = Orders.builder()
            .retailer(retailer)
            .farm(farm)
            .depositAmount(BigDecimal.ZERO)
            .totalAmount(totalAmount)
            .status(OrderStatus.PENDING)
            .build();

    order = ordersRepository.save(order);

    // ==========================================================
    // Tạo Order Detail
    // BR10: Reserve hàng
    // ==========================================================

    for (CartItem item : items) {

        ProductBatch batch = item.getProductBatch();

        OrderDetail detail = OrderDetail.builder()
                .order(order)
                .productBatch(batch)
                .quantity(item.getQuantity())
                .unitPrice(batch.getUnitPrice())
                .build();

        orderDetailRepository.save(detail);

        // Giữ hàng
        productBatchService.decreaseRemainingQuantity(
                batch.getBatchId(),
                item.getQuantity()
        );
        
    }

    // ==========================================================
    // BR09
    // Xóa giỏ hàng
    // ==========================================================

    cartRepository.delete(cart);

    return orderMapper.toResponse(order);

}
@Override
public OrderResponse confirmOrder(Long orderId) {

    Orders order = findFarmOrder(orderId);

    validateStatus(order, OrderStatus.PENDING);

    order.setStatus(OrderStatus.CONFIRMED);

    order = ordersRepository.save(order);

    notificationService.createNotification(
            order.getRetailer().getAccount(),
            "Order Confirmed",
            "Farm has confirmed Order #" + order.getOrderId()
    );

    return orderMapper.toResponse(order);
}
@Override
public OrderResponse cancelOrder(Long orderId) {

    Orders order = findOrder(orderId);

    // chỉ Farm hoặc Retailer của đơn mới được hủy

    Long accountId = SecurityUtils.getCurrentAccountId();

    boolean isRetailer =
            order.getRetailer()
                    .getAccount()
                    .getAccountId()
                    .equals(accountId);

    boolean isFarm =
            order.getFarm()
                    .getAccount()
                    .getAccountId()
                    .equals(accountId);

    if (!isRetailer && !isFarm) {

        throw new UnauthorizedException(
                "You cannot cancel this order."
        );

    }

    if (order.getStatus() != OrderStatus.PENDING &&
            order.getStatus() != OrderStatus.CONFIRMED) {

        throw new BadRequestException(
                "Order cannot be cancelled."
        );

    }

    List<OrderDetail> details =
            orderDetailRepository.findByOrder(order);

    for (OrderDetail detail : details) {

        productBatchService.increaseRemainingQuantity(
                detail.getProductBatch().getBatchId(),
                detail.getQuantity()
        );

    }

    order.setStatus(OrderStatus.CANCELLED);

    order = ordersRepository.save(order);

    if (isFarm) {

    notificationService.createNotification(
            order.getRetailer().getAccount(),
            "Order Cancelled",
            "Farm has cancelled Order #" + order.getOrderId()
    );

} else {

    notificationService.createNotification(
            order.getFarm().getAccount(),
            "Order Cancelled",
            "Retailer has cancelled Order #" + order.getOrderId()
    );

}

    return orderMapper.toResponse(order);
}
@Override
public OrderResponse completeOrder(Long orderId) {

    Orders order = findOrder(orderId);

    validateStatus(
            order,
            OrderStatus.CONFIRMED
    );

    order.setStatus(OrderStatus.COMPLETED);

    order = ordersRepository.save(order);

    notificationService.createNotification(
            order.getFarm().getAccount(),
            "Order Completed",
            "Order #" + order.getOrderId()
                    + " has been completed."
    );

    notificationService.createNotification(
            order.getRetailer().getAccount(),
            "Order Completed",
            "Order #" + order.getOrderId()
                    + " has been completed."
    );

    return orderMapper.toResponse(order);
}
}