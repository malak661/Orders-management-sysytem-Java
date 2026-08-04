package com.ordersystem.exception;

/**
 * Thrown when attempting to create or update a customer with an email
 * that already exists in the system.
 */
public class DuplicateEmailException extends RuntimeException {

    public DuplicateEmailException(String email) {
        super("A customer with email '" + email + "' already exists.");
    }
}