package com.ordersystem.exception;

/**
 * Thrown when an order with the given id cannot be found.
 */
public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String orderId) {
        super("Order not found with id: " + orderId);
    }
}