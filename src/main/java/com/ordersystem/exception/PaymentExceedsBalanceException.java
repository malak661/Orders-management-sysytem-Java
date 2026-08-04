package com.ordersystem.exception;

import java.math.BigDecimal;

/**
 * Thrown when a payment amount exceeds the remaining unpaid balance of an order.
 */
public class PaymentExceedsBalanceException extends RuntimeException {

    public PaymentExceedsBalanceException(BigDecimal amount, BigDecimal remainingBalance) {
        super("Payment amount " + amount + " exceeds remaining balance of " + remainingBalance + ".");
    }
}