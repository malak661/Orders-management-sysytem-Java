package com.ordersystem.app;

<<<<<<< Updated upstream
/**
 * Application entry point.
 * TODO: initialize DB connection / schema (DbConnection.initializeSchema())
 * TODO: wire up repositories -> services -> menus (manual dependency injection)
 * TODO: start MainMenu loop
=======
import com.ordersystem.repository.impl.OrderRepositoryJdbc;
import com.ordersystem.repository.impl.PaymentRepositoryJdbc;
import com.ordersystem.repository.impl.ProductRepositoryJdbc;
import com.ordersystem.service.OrderService;
import com.ordersystem.service.PaymentService;
import com.ordersystem.service.ReportService;
import com.ordersystem.util.ConsoleInputHelper;
import com.ordersystem.util.DbConnection;

import java.util.Scanner;

/**
 * Application entry point.
 *
 * Dependency wiring (innermost dependencies first):
 *
 *   Scanner ──────────────────────────────────── shared single instance
 *   ConsoleInputHelper(scanner)
 *
 *   OrderRepositoryJdbc()   ──> OrderService(orderRepository)
 *   ProductRepositoryJdbc()
 *   PaymentRepositoryJdbc() ──> PaymentService(paymentRepository, orderService)
 *                           ──> PaymentMenu(paymentService, inputHelper, scanner)
 *
 *   ReportService(orderRepository, productRepository)
 *   ReportMenu(reportService, inputHelper)
 *
 *   MainMenu(customerMenu, productMenu, orderMenu, paymentMenu, reportMenu, inputHelper)
>>>>>>> Stashed changes
 */
public class Main {

    public static void main(String[] args) {
        // TODO
        System.out.println("Orders Management System - starting...");
<<<<<<< Updated upstream
=======
        DbConnection.initializeSchema();

        try (Scanner scanner = new Scanner(System.in)) {
            ConsoleInputHelper inputHelper = new ConsoleInputHelper(scanner);
            MainMenu mainMenu = createMainMenu(inputHelper, scanner);
            mainMenu.show();
        }

        System.out.println("Orders Management System - exiting.");
    }

    /**
     * Wires the full dependency graph and returns a ready-to-use MainMenu.
     *
     * @param inputHelper the shared ConsoleInputHelper (wraps scanner)
     * @param scanner     raw Scanner — passed directly to PaymentMenu because
     *                    PaymentMenu performs its own readLine() until
     *                    ConsoleInputHelper call-sites are fully migrated
     */
    private static MainMenu createMainMenu(ConsoleInputHelper inputHelper, Scanner scanner) {

        // ── Repositories ──────────────────────────────────────────────────────
        var orderRepository   = new OrderRepositoryJdbc();
        var productRepository = new ProductRepositoryJdbc();
        var paymentRepository = new PaymentRepositoryJdbc();

        // ── Services ──────────────────────────────────────────────────────────
        // OrderService must be created before PaymentService because
        // PaymentService depends on it for order-total lookup and PAID transition.
        var orderService   = new OrderService(orderRepository);
        var paymentService = new PaymentService(paymentRepository, orderService);
        var reportService  = new ReportService(orderRepository, productRepository);

        // ── Menus ─────────────────────────────────────────────────────────────
        // PaymentMenu requires (PaymentService, ConsoleInputHelper, Scanner).
        // The same scanner created in main() is passed here — no second Scanner.
        var paymentMenu = new PaymentMenu(paymentService, inputHelper, scanner);
        var reportMenu  = new ReportMenu(reportService, inputHelper);

        return new MainMenu(
                new CustomerMenu(),
                new ProductMenu(),
                new OrderMenu(),
                paymentMenu,
                reportMenu,
                inputHelper
        );
>>>>>>> Stashed changes
    }
}
