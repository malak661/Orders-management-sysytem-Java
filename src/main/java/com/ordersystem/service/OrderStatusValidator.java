package com.ordersystem.service;

import com.ordersystem.model.OrderStatus;

/**
 * Encapsulates the valid Order status transition rules (AC7).
 * TODO: define the allowed transition map, e.g.:
 *   CREATED -> CONFIRMED, CANCELLED
 *   CONFIRMED -> PAID, CANCELLED
 *   PAID -> PROCESSING, REFUNDED
 *   PROCESSING -> SHIPPED
 *   SHIPPED -> DELIVERED
 *   DELIVERED -> (terminal)
 *   CANCELLED -> (terminal)
 *   REFUNDED -> (terminal)
 * TODO: implement isValidTransition(OrderStatus from, OrderStatus to)
 */
public class OrderStatusValidator {

    public boolean isValidTransition(OrderStatus from, OrderStatus to) {
        // TODO
        return false;
    }
}
