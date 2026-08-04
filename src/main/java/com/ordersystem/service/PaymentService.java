package com.ordersystem.service;

import com.ordersystem.model.Payment;
import com.ordersystem.repository.PaymentRepository;

import java.math.BigDecimal;
import java.util.List;

/**
 * Business Logic Layer for Payment operations.
 * Responsibilities (AC8):
 * TODO: - recordPayment(orderId, amount, method) -> reject if amount > remaining balance
 * TODO: - getRemainingBalance(orderId) -> order total - sum(payments)
 * TODO: - getPaymentsForOrder(orderId)
 * TODO: - update order status to PAID when balance reaches zero (coordinate with OrderService)
 */
public class PaymentService {

    private final PaymentRepository paymentRepository;
    // TODO: inject OrderService to fetch order total / update status

    public PaymentService(PaymentRepository paymentRepository) {
        this.paymentRepository = paymentRepository;
    }

    public Payment recordPayment(String orderId, BigDecimal amount, String method) {
        // TODO: validate amount <= remaining balance
        return null;
    }

    public BigDecimal getRemainingBalance(String orderId) {
        // TODO
        return null;
    }

    public List<Payment> getPaymentsForOrder(String orderId) {
        // TODO
        return null;
    }
}
