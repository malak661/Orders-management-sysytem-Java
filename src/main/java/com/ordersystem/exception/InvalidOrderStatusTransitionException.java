package com.ordersystem.exception;

import com.ordersystem.model.OrderStatus;

/**
 * Thrown when attempting to transition an order to a status that is not
 * valid from its current status (e.g. shipping an unpaid order,
 * updating a cancelled order).
 */
public class InvalidOrderStatusTransitionException extends RuntimeException {

    public InvalidOrderStatusTransitionException(OrderStatus from, OrderStatus to) {
        super("Invalid order status transition: cannot move from " + from + " to " + to + ".");
    }
}