package com.ordersystem.exception;

/**
 * Thrown when attempting to create or update a product with a negative stock quantity.
 */
public class InvalidStockQuantityException extends RuntimeException {

    public InvalidStockQuantityException(int quantity) {
        super("Invalid stock quantity: " + quantity + ". Quantity cannot be negative.");
    }
}