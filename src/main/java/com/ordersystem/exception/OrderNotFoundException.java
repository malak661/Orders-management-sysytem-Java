package com.ordersystem.exception;

/**
 * TODO: Thrown when ... (describe the business rule this exception protects)
 */
public class OrderNotFoundException extends RuntimeException {

    public OrderNotFoundException(String message) {
        super(message);
    }

    // TODO: add additional constructors if useful (e.g. with cause, or with structured fields)
}
