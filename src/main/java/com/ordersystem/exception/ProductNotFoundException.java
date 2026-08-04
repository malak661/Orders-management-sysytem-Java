package com.ordersystem.exception;

/**
 * Thrown when a product with the given id cannot be found.
 */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String productId) {
        super("Product not found with id: " + productId);
    }
}