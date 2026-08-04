package com.ordersystem.repository;

import com.ordersystem.model.OrderItem;

import java.util.List;

/**
 * Data Access Layer contract for OrderItem persistence.
 * TODO: implement with OrderItemRepositoryJdbc (see repository/impl)
 */
public interface OrderItemRepository {

    void saveAll(String orderId, List<OrderItem> items);

    List<OrderItem> findByOrderId(String orderId);
}
