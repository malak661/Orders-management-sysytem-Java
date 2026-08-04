package com.ordersystem.exception;

/**
 * Thrown when attempting to create an order with no items.
 */
public class EmptyOrderException extends RuntimeException {

    public EmptyOrderException() {
        super("An order must contain at least one item.");
    }
}