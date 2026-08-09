package com.ordersystem.app;

import com.ordersystem.model.Order;
import com.ordersystem.model.Product;
import com.ordersystem.service.ReportService;
import com.ordersystem.util.ConsoleInputHelper;

import java.math.BigDecimal;
import java.util.List;

/**
 * Console menu for reports (AC10).
 */
public class ReportMenu {

    private final ReportService reportService;
    private final ConsoleInputHelper inputHelper;

    public ReportMenu(ReportService reportService, ConsoleInputHelper inputHelper) {
        this.reportService = reportService;
        this.inputHelper = inputHelper;
    }

    public void show() {
        while (true) {
            System.out.println();
            System.out.println("=== Reports Menu ===");
            System.out.println("1. Customer order history");
            System.out.println("2. Low-stock products");
            System.out.println("3. Unpaid orders");
            System.out.println("4. Total sales summary");
            System.out.println("0. Back to main menu");
            int choice = inputHelper.readInt("Select an option: ");

            switch (choice) {
                case 1 -> showCustomerOrderHistory();
                case 2 -> showLowStockProducts();
                case 3 -> showUnpaidOrders();
                case 4 -> showTotalSalesSummary();
                case 0 -> {
                    return;
                }
                default -> System.out.println("Invalid option. Please choose a valid menu item.");
            }
        }
    }

    private void showCustomerOrderHistory() {
        long customerId = inputHelper.readLong("Enter customer ID: ");
        List<Order> orders = reportService.getCustomerOrderHistory(customerId);

        if (orders == null || orders.isEmpty()) {
            System.out.println("No orders found for customer ID '" + customerId + "'.");
            return;
        }

        System.out.println("Customer order history for " + customerId + ":");
        orders.forEach(order -> System.out.println("- " + order));
    }

    private void showLowStockProducts() {
        int threshold = inputHelper.readInt("Enter stock threshold: ");
        List<Product> products = reportService.getLowStockProducts(threshold);

        if (products == null || products.isEmpty()) {
            System.out.println("No low-stock products found for threshold " + threshold + ".");
            return;
        }

        System.out.println("Low-stock products (threshold=" + threshold + "):");
        products.forEach(product -> System.out.println("- " + product));
    }

    private void showUnpaidOrders() {
        List<Order> orders = reportService.getUnpaidOrders();

        if (orders == null || orders.isEmpty()) {
            System.out.println("No unpaid orders were found.");
            return;
        }

        System.out.println("Unpaid orders:");
        orders.forEach(order -> System.out.println("- " + order));
    }

    private void showTotalSalesSummary() {
        BigDecimal totalSales = reportService.getTotalSales();
        System.out.println("Total sales: " + totalSales);
    }
}
