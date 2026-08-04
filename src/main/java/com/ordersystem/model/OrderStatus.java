package com.ordersystem.model;

/**
 * Represents the lifecycle states of an Order.
 * TODO: Define valid transition rules (see OrderService / OrderStatusValidator).
 */
public enum OrderStatus {
    CREATED,
    CONFIRMED,
    PAID,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED
}
