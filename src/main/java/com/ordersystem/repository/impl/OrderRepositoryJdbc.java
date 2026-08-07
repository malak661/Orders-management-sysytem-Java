package com.ordersystem.repository.impl;

import com.ordersystem.model.Order;
import com.ordersystem.repository.OrderRepository;
import com.ordersystem.util.DbConnection;

import java.util.List;
import java.util.Optional;

/**
 * SQLite/JDBC implementation of OrderRepository.
 * TODO: implement all methods using java.sql
 * TODO: create the "orders" table via schema.sql (see resources/)
 * TODO: decide how items are loaded (join with OrderItemRepository)
 */
public class OrderRepositoryJdbc implements OrderRepository {

    @Override
    public Order save(Order order) {
        // TODO
        return null;
    }

    @Override
    public Optional<Order> findById(String id) {
        // TODO
        return Optional.empty();
    }

    @Override
    public List<Order> findAll() {
        // TODO
        return null;
    }

    @Override
    public List<Order> findByCustomerId(String customerId) {
        // TODO
        return null;
    }

    @Override
    public List<Order> findUnpaid() {
        // TODO
        return null;
    }

    @Override
    public void update(Order order) {
        // TODO
    }

    @Override
    public void delete(String id) {
        // TODO
    }
}
