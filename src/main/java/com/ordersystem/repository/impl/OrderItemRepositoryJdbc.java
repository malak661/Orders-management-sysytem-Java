package com.ordersystem.repository.impl;

import com.ordersystem.model.OrderItem;
import com.ordersystem.repository.OrderItemRepository;
import com.ordersystem.util.DbConnection;

import java.util.List;

/**
 * SQLite/JDBC implementation of OrderItemRepository.
 * TODO: implement all methods using java.sql
 * TODO: create the "order_items" table via schema.sql (see resources/)
 */
public class OrderItemRepositoryJdbc implements OrderItemRepository {

    @Override
    public void saveAll(String orderId, List<OrderItem> items) {
        // TODO
    }

    @Override
    public List<OrderItem> findByOrderId(String orderId) {
        // TODO
        return null;
    }
}
