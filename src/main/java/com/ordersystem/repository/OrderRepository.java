package com.ordersystem.repository;

import com.ordersystem.model.Order;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Layer contract for Order persistence.
 * TODO: implement with OrderRepositoryJdbc (see repository/impl)
 * NOTE: Decide whether Order items are persisted together (cascade) via OrderItemRepository
 *       or loaded separately when fetching an Order.
 */
public interface OrderRepository {

    Order save(Order order);

    Optional<Order> findById(String id);

    List<Order> findAll();

    List<Order> findByCustomerId(long customerId);

    List<Order> findUnpaid();

    void update(Order order);

    void delete(String id);
}
