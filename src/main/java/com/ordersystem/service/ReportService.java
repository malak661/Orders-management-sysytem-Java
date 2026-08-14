package com.ordersystem.service;

import com.ordersystem.model.Order;
import com.ordersystem.model.Product;
import com.ordersystem.repository.OrderRepository;
import com.ordersystem.repository.ProductRepository;

import java.math.BigDecimal;
import java.util.Collections;
import java.util.List;

/**
 * Business Logic Layer for reporting (AC10).
 */
public class ReportService {

    private final OrderRepository orderRepository;
    private final ProductRepository productRepository;

    public ReportService(OrderRepository orderRepository, ProductRepository productRepository) {
        this.orderRepository = orderRepository;
        this.productRepository = productRepository;
    }

    public List<Order> getCustomerOrderHistory(String customerId) {
        if (customerId == null || customerId.isBlank()) {
            return Collections.emptyList();
        }
        return orderRepository.findByCustomerId(customerId);
    }

    public List<Product> getLowStockProducts(int threshold) {
        return productRepository.findLowStock(Math.max(threshold, 0));
    }

    public List<Order> getUnpaidOrders() {
        return orderRepository.findUnpaid();
    }

    public BigDecimal getTotalSales() {
        List<Order> orders = orderRepository.findAll();
        if (orders == null || orders.isEmpty()) {
            return BigDecimal.ZERO;
        }
        return orders.stream()
                .map(Order::getTotal)
                .filter(total -> total != null)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }
}
