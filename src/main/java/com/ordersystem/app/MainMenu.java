package com.ordersystem.app;

import com.ordersystem.util.ConsoleInputHelper;

/**
 * Root console menu. Routes to the sub-menus.
 */
public class MainMenu {

    private final CustomerMenu customerMenu;
    private final ProductMenu productMenu;
    private final OrderMenu orderMenu;
    private final PaymentMenu paymentMenu;
    private final ReportMenu reportMenu;
    private final ConsoleInputHelper inputHelper;

    public MainMenu(CustomerMenu customerMenu,
                    ProductMenu productMenu,
                    OrderMenu orderMenu,
                    PaymentMenu paymentMenu,
                    ReportMenu reportMenu,
                    ConsoleInputHelper inputHelper) {
        this.customerMenu = customerMenu;
        this.productMenu = productMenu;
        this.orderMenu = orderMenu;
        this.paymentMenu = paymentMenu;
        this.reportMenu = reportMenu;
        this.inputHelper = inputHelper;
    }

    public void show() {
        while (true) {
            System.out.println();
            System.out.println("=== Main Menu ===");
            System.out.println("1. Customers");
            System.out.println("2. Products");
            System.out.println("3. Orders");
            System.out.println("4. Payments");
            System.out.println("5. Reports");
            System.out.println("0. Exit");
            int choice = inputHelper.readInt("Select an option: ");

            switch (choice) {
                case 1 -> customerMenu.show();
                case 2 -> productMenu.show();
                case 3 -> orderMenu.show();
                case 4 -> paymentMenu.show();
                case 5 -> reportMenu.show();
                case 0 -> {
                    System.out.println("Goodbye!");
                    return;
                }
                default -> System.out.println("Invalid option. Please choose a valid menu item.");
            }
        }
    }
}
