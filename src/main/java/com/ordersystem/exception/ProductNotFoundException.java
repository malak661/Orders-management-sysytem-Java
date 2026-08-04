package com.ordersystem.exception;

/**
 * TODO: Thrown when ... (describe the business rule this exception protects)
 */
public class ProductNotFoundException extends RuntimeException {

    public ProductNotFoundException(String message) {
        super(message);
    }

    // TODO: add additional constructors if useful (e.g. with cause, or with structured fields)
}
