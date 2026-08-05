package com.ordersystem.app;

import com.ordersystem.repository.impl.OrderRepositoryJdbc;
import com.ordersystem.repository.impl.ProductRepositoryJdbc;
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
            MainMenu mainMenu = createMainMenu(inputHelper);
            mainMenu.show();
        }

        System.out.println("Orders Management System - exiting.");
    }

    private static MainMenu createMainMenu(ConsoleInputHelper inputHelper) {
        var orderRepository = new OrderRepositoryJdbc();
        var productRepository = new ProductRepositoryJdbc();
        var reportService = new ReportService(orderRepository, productRepository);
        var reportMenu = new ReportMenu(reportService, inputHelper);

        return new MainMenu(
                new CustomerMenu(),
                new ProductMenu(),
                new OrderMenu(),
                new PaymentMenu(),
                reportMenu,
                inputHelper
        );
    }
}
