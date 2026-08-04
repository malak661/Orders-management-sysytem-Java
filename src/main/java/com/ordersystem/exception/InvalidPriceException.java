package com.ordersystem.exception;

import java.math.BigDecimal;

/**
 * Thrown when attempting to create or update a product with a negative price.
 */
public class InvalidPriceException extends RuntimeException {

    public InvalidPriceException(BigDecimal price) {
        super("Invalid price: " + price + ". Price cannot be negative.");
    }
}