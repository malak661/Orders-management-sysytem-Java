package com.ordersystem.app;

import com.ordersystem.model.Order;
import com.ordersystem.model.OrderItem;
import com.ordersystem.model.OrderStatus;
import com.ordersystem.service.CustomerService;
import com.ordersystem.service.OrderService;
import com.ordersystem.service.ProductService;
import com.ordersystem.util.ConsoleInputHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Console menu for Order creation and lifecycle management (AC3-AC7).
 */
public class OrderMenu {

    private final OrderService orderService;
    private final CustomerService customerService;
    private final ProductService productService;
    private final ConsoleInputHelper inputHelper;

    public OrderMenu(OrderService orderService, CustomerService customerService, 
                     ProductService productService, ConsoleInputHelper inputHelper) {
        this.orderService = orderService;
        this.customerService = customerService;
        this.productService = productService;
        this.inputHelper = inputHelper;
    }

    public void show() {
        while (true) {
            System.out.println("\n--- Order Management Menu ---");
            System.out.println("1. Create Order");
            System.out.println("2. Confirm Order");
            System.out.println("3. Cancel Order");
            System.out.println("4. Change Order Status");
            System.out.println("5. View Order Details");
            System.out.println("6. List Orders by Customer");
            System.out.println("0. Back to Main Menu");
            
            int choice = inputHelper.readInt("Select an option: ");
            
            try {
                switch (choice) {
                    case 1:
                        createOrder();
                        break;
                    case 2:
                        confirmOrder();
                        break;
                    case 3:
                        cancelOrder();
                        break;
                    case 4:
                        changeOrderStatus();
                        break;
                    case 5:
                        viewOrder();
                        break;
                    case 6:
                        listCustomerOrders();
                        break;
                    case 0:
                        return;
                    default:
                        System.out.println("Invalid option. Please try again.");
                }
            } catch (Exception e) {
                System.out.println("Error: " + e.getMessage());
            }
        }
    }
    
    private void createOrder() {
        String customerId = inputHelper.readString("Enter Customer ID: ");
        List<OrderItem> items = new ArrayList<>();
        
        while (true) {
            String productId = inputHelper.readString("Enter Product ID (or 'done' to finish): ");
            if (productId.equalsIgnoreCase("done")) {
                break;
            }
            int quantity = inputHelper.readInt("Enter Quantity: ");
            if (quantity <= 0) {
                System.out.println("Quantity must be greater than 0.");
                continue;
            }
            
            OrderItem item = new OrderItem();
            item.setProductId(productId);
            item.setQuantity(quantity);
            items.add(item);
        }
        
        Order order = orderService.createOrder(customerId, items);
        System.out.println("Order created successfully! Order ID: " + order.getId());
        System.out.println("Subtotal: " + order.getSubtotal() + ", Total: " + order.getTotal());
    }
    
    private void confirmOrder() {
        String orderId = inputHelper.readString("Enter Order ID to confirm: ");
        Order order = orderService.confirmOrder(orderId);
        System.out.println("Order confirmed! Status is now " + order.getStatus());
    }
    
    private void cancelOrder() {
        String orderId = inputHelper.readString("Enter Order ID to cancel: ");
        Order order = orderService.cancelOrder(orderId);
        System.out.println("Order cancelled! Status is now " + order.getStatus());
    }
    
    private void changeOrderStatus() {
        String orderId = inputHelper.readString("Enter Order ID: ");
        System.out.println("Available statuses: PAID, PROCESSING, SHIPPED, DELIVERED, REFUNDED");
        String statusStr = inputHelper.readString("Enter new status: ").toUpperCase();
        
        try {
            OrderStatus status = OrderStatus.valueOf(statusStr);
            Order order = orderService.transitionStatus(orderId, status);
            System.out.println("Order status successfully changed to " + order.getStatus());
        } catch (IllegalArgumentException e) {
            System.out.println("Invalid status name entered.");
        }
    }
    
    private void viewOrder() {
        String orderId = inputHelper.readString("Enter Order ID: ");
        Order order = orderService.getOrderById(orderId);
        System.out.println("\nOrder Details:");
        System.out.println("ID: " + order.getId());
        System.out.println("Customer ID: " + order.getCustomerId());
        System.out.println("Status: " + order.getStatus());
        System.out.println("Subtotal: " + order.getSubtotal());
        System.out.println("Discount: " + order.getDiscount());
        System.out.println("Tax: " + order.getTax());
        System.out.println("Total: " + order.getTotal());
        System.out.println("Items:");
        for (OrderItem item : order.getItems()) {
            System.out.println("  - Product: " + item.getProductId() + ", Qty: " + item.getQuantity() + ", Unit Price: " + item.getUnitPriceAtOrderTime() + ", Line Total: " + item.getLineTotal());
        }
    }
    
    private void listCustomerOrders() {
        String customerId = inputHelper.readString("Enter Customer ID: ");
        List<Order> orders = orderService.getOrdersByCustomer(customerId);
        if (orders.isEmpty()) {
            System.out.println("No orders found for customer.");
        } else {
            System.out.println("Orders for customer " + customerId + ":");
            for (Order order : orders) {
                System.out.println("  - ID: " + order.getId() + ", Status: " + order.getStatus() + ", Total: " + order.getTotal());
            }
        }
    }
}
