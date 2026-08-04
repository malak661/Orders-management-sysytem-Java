package com.ordersystem.service;

import com.ordersystem.model.Order;
import com.ordersystem.model.OrderItem;
import com.ordersystem.model.OrderStatus;
import com.ordersystem.repository.OrderRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Business Logic Layer for Order creation and lifecycle management.
 * Responsibilities (AC3, AC4, AC5, AC6, AC7):
 * TODO: - createOrder(customerId, items) -> validate customer exists, items not empty,
 *         validate stock availability for each item (do NOT partially create order)
 * TODO: - calculateTotals(order) -> subtotal, discount, tax, total using BigDecimal
 * TODO: - confirmOrder(orderId) -> reduce stock exactly once, transition CREATED -> CONFIRMED
 * TODO: - cancelOrder(orderId) -> if was CONFIRMED, restore stock; transition to CANCELLED
 * TODO: - markPaid / process / ship / deliver / refund -> validate transitions
 *         (e.g. cannot ship unpaid order, cannot update cancelled order)
 * TODO: - getOrderById(orderId)
 * TODO: - getOrdersByCustomer(customerId)
 */
public class OrderService {

    private final OrderRepository orderRepository;
    // TODO: inject ProductService (for stock validation/reduction)
    // TODO: inject OrderItemRepository if items stored separately

    public OrderService(OrderRepository orderRepository) {
        this.orderRepository = orderRepository;
    }

    public Order createOrder(String customerId, List<OrderItem> items) {
        // TODO: validate customer, validate items not empty, validate stock for each item
        return null;
    }

    public Order confirmOrder(String orderId) {
        // TODO: validate current status == CREATED, reduce stock, set status = CONFIRMED
        return null;
    }

    public Order cancelOrder(String orderId) {
        // TODO: if status == CONFIRMED, restore stock; set status = CANCELLED
        return null;
    }

    public Order transitionStatus(String orderId, OrderStatus newStatus) {
        // TODO: validate transition is allowed (see OrderStatusValidator)
        return null;
    }

    public BigDecimal calculateSubtotal(Order order) {
        // TODO
        return null;
    }

    public BigDecimal calculateTotal(Order order) {
        // TODO: subtotal - discount + tax
        return null;
    }

    public Order getOrderById(String orderId) {
        // TODO
        return null;
    }

    public List<Order> getOrdersByCustomer(String customerId) {
        // TODO
        return null;
    }
}
