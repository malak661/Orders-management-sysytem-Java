package com.ordersystem.repository.impl;

import com.ordersystem.model.Payment;
import com.ordersystem.repository.PaymentRepository;
import com.ordersystem.util.DbConnection;

import java.util.List;
import java.util.Optional;

/**
 * SQLite/JDBC implementation of PaymentRepository.
 * TODO: implement all methods using java.sql
 * TODO: create the "payments" table via schema.sql (see resources/)
 */
public class PaymentRepositoryJdbc implements PaymentRepository {

    @Override
    public Payment save(Payment payment) {
        // TODO
        return null;
    }

    @Override
    public Optional<Payment> findById(String id) {
        // TODO
        return Optional.empty();
    }

    @Override
    public List<Payment> findByOrderId(String orderId) {
        // TODO
        return null;
    }

    @Override
    public List<Payment> findAll() {
        // TODO
        return null;
    }
}
