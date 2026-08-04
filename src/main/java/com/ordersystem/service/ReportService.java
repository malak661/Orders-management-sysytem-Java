package com.ordersystem.service;

import com.ordersystem.model.Order;
import com.ordersystem.model.Product;

import java.math.BigDecimal;
import java.util.List;

/**
 * Business Logic Layer for reporting (AC10).
 * TODO: - getCustomerOrderHistory(customerId)
 * TODO: - getLowStockProducts(threshold)
 * TODO: - getUnpaidOrders()
 * TODO: - getTotalSalesSummary(startDate, endDate) or overall
 */
public class ReportService {

    // TODO: inject OrderService / ProductService / PaymentService as needed

    public List<Order> getCustomerOrderHistory(String customerId) {
        // TODO
        return null;
    }

    public List<Product> getLowStockProducts(int threshold) {
        // TODO
        return null;
    }

    public List<Order> getUnpaidOrders() {
        // TODO
        return null;
    }

    public BigDecimal getTotalSales() {
        // TODO
        return null;
    }
}
