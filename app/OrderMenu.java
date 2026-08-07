package app;

import model.Order;
import model.OrderItem;
import model.OrderItemRequest;
import model.OrderStatus;
import service.OrderService;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class OrderMenu {

    private final OrderService orderService;
    private final Scanner scanner;

    public OrderMenu(OrderService orderService, Scanner scanner) {
        this.orderService = orderService;
        this.scanner = scanner;
    }

    public void showMenu() {
        while (true) {
            System.out.println("\n=== Order Menu ===");
            System.out.println("1. Create Order");
            System.out.println("2. View Order");
            System.out.println("3. Change Order Status (Confirm, Ship, etc.)");
            System.out.println("4. Cancel Order");
            System.out.println("0. Back to Main Menu");
            System.out.print("Select an option: ");

            String choice = scanner.nextLine().trim();

            try {
                switch (choice) {
                    case "1":
                        handleCreateOrder();
                        break;
                    case "2":
                        handleViewOrder();
                        break;
                    case "3":
                        handleChangeOrderStatus();
                        break;
                    case "4":
                        handleCancelOrder();
                        break;
                    case "0":
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }

    private void handleCreateOrder() {
        System.out.println("\n--- Create New Order ---");
        System.out.print("Enter Customer ID: ");
        int customerId = Integer.parseInt(scanner.nextLine().trim());

        List<OrderItemRequest> itemRequests = new ArrayList<>();
        
        while (true) {
            System.out.print("Enter Product ID (or 0 to finish adding items): ");
            int productId = Integer.parseInt(scanner.nextLine().trim());
            if (productId == 0) {
                break;
            }

            System.out.print("Enter Quantity: ");
            int quantity = Integer.parseInt(scanner.nextLine().trim());

            if (quantity <= 0) {
                System.out.println("Quantity must be greater than 0.");
                continue;
            }

            itemRequests.add(new OrderItemRequest(productId, quantity));
        }

        Order order = orderService.createOrder(customerId, itemRequests);
        System.out.println("Order created successfully! Order ID: " + order.getOrderId());
    }

    private void handleViewOrder() {
        System.out.println("\n--- View Order ---");
        System.out.print("Enter Order ID: ");
        int orderId = Integer.parseInt(scanner.nextLine().trim());

        Order order = orderService.getOrderDetails(orderId);
        if (order == null) {
            System.out.println("Order not found.");
            return;
        }

        System.out.println("\nOrder Details:");
        System.out.println("ID: " + order.getOrderId());
        System.out.println("Customer ID: " + order.getCustomerId());
        System.out.println("Date: " + order.getOrderDate());
        System.out.println("Status: " + order.getStatus());
        System.out.println("Subtotal: $" + order.getSubtotal());
        System.out.println("Discount: $" + order.getDiscountAmount());
        System.out.println("Tax: $" + order.getTaxAmount());
        System.out.println("Total: $" + order.getTotalAmount());
        System.out.println("Items:");
        for (OrderItem item : order.getItems()) {
            System.out.println("  - Product ID: " + item.getProductId() + ", Qty: " + item.getQuantity() 
                + ", Unit Price: $" + item.getUnitPrice() + ", Subtotal: $" + item.getSubtotal());
        }
    }

    private void handleChangeOrderStatus() {
        System.out.println("\n--- Change Order Status ---");
        System.out.print("Enter Order ID: ");
        int orderId = Integer.parseInt(scanner.nextLine().trim());

        System.out.println("Available Statuses:");
        for (OrderStatus status : OrderStatus.values()) {
            System.out.println("- " + status.name());
        }
        System.out.print("Enter New Status (e.g., CONFIRMED, PAID, SHIPPED): ");
        String statusStr = scanner.nextLine().trim().toUpperCase();

        try {
            OrderStatus newStatus = OrderStatus.valueOf(statusStr);
            if (newStatus == OrderStatus.CONFIRMED) {
                orderService.confirmOrder(orderId);
            } else if (newStatus == OrderStatus.CANCELLED) {
                orderService.cancelOrder(orderId);
            } else {
                orderService.changeOrderStatus(orderId, newStatus);
            }
            System.out.println("Order status updated to " + newStatus);
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid status: " + statusStr);
        }
    }

    private void handleCancelOrder() {
        System.out.println("\n--- Cancel Order ---");
        System.out.print("Enter Order ID: ");
        int orderId = Integer.parseInt(scanner.nextLine().trim());

        orderService.cancelOrder(orderId);
        System.out.println("Order cancelled successfully.");
    }
}
