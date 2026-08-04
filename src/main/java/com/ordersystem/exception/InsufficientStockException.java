package com.ordersystem.exception;

/**
 * Thrown when an order requests a quantity greater than the available stock
 * for a given product.
 */
public class InsufficientStockException extends RuntimeException {

    public InsufficientStockException(String productId, int requested, int available) {
        super("Insufficient stock for product '" + productId + "': requested "
                + requested + ", available " + available + ".");
    }
}