package com.ordersystem.repository;

import com.ordersystem.model.Payment;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Layer contract for Payment persistence.
 * TODO: implement with PaymentRepositoryJdbc (see repository/impl)
 */
public interface PaymentRepository {

    Payment save(Payment payment);

    Optional<Payment> findById(String id);

    List<Payment> findByOrderId(String orderId);

    List<Payment> findAll();
}
