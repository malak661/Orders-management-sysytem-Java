package com.ordersystem.app;

import com.ordersystem.repository.impl.CustomerRepositoryJdbc;
import com.ordersystem.repository.impl.OrderItemRepositoryJdbc;
import com.ordersystem.repository.impl.OrderRepositoryJdbc;
import com.ordersystem.repository.impl.PaymentRepositoryJdbc;
import com.ordersystem.repository.impl.ProductRepositoryJdbc;
import com.ordersystem.service.CustomerService;
import com.ordersystem.service.OrderService;
import com.ordersystem.service.OrderStatusValidator;
import com.ordersystem.service.PaymentService;
import com.ordersystem.service.ProductService;
import com.ordersystem.service.ReportService;
import com.ordersystem.util.ConsoleInputHelper;
import com.ordersystem.util.DbConnection;

import java.util.Scanner;

/**
 * Application entry point.
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("Orders Management System - starting...");
        DbConnection.initializeSchema();

        try (Scanner scanner = new Scanner(System.in)) {
            ConsoleInputHelper inputHelper = new ConsoleInputHelper(scanner);
            MainMenu mainMenu = createMainMenu(inputHelper, scanner);
            mainMenu.show();
        }

        System.out.println("Orders Management System - exiting.");
    }

    private static MainMenu createMainMenu(ConsoleInputHelper inputHelper, Scanner scanner) {
        // Repositories
        var customerRepository  = new CustomerRepositoryJdbc();
        var productRepository   = new ProductRepositoryJdbc();
        var orderRepository     = new OrderRepositoryJdbc();
        var orderItemRepository = new OrderItemRepositoryJdbc();
        var paymentRepository   = new PaymentRepositoryJdbc();

        // Services
        var customerService  = new CustomerService(customerRepository);
        var productService   = new ProductService(productRepository);
        var statusValidator  = new OrderStatusValidator();
        var orderService     = new OrderService(orderRepository, orderItemRepository,
                                                productService, customerService, statusValidator);
        var paymentService   = new PaymentService(paymentRepository, orderService);
        var reportService    = new ReportService(orderRepository, productRepository);

        // Menus
        var orderMenu   = new OrderMenu(orderService, customerService, productService, inputHelper);
        var paymentMenu = new PaymentMenu(paymentService, inputHelper, scanner);
        var reportMenu  = new ReportMenu(reportService, inputHelper);

        return new MainMenu(
                new CustomerMenu(),
                new ProductMenu(),
                orderMenu,
                paymentMenu,
                reportMenu,
                inputHelper
        );
    }
}
