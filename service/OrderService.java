package service;

import model.*;
import repository.CustomerRepository;
import repository.OrderItemRepository;
import repository.OrderRepository;
import repository.ProductRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderService {

    private final Connection connection;
    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final CustomerRepository customerRepository;
    private final ProductRepository productRepository;
    private final OrderStatusValidator statusValidator;

    private static final BigDecimal DEFAULT_TAX_PERCENTAGE = new BigDecimal("0.10"); // 10%
    private static final BigDecimal DEFAULT_DISCOUNT_PERCENTAGE = new BigDecimal("0.00"); // 0%

    public OrderService(Connection connection, 
                        OrderRepository orderRepository, 
                        OrderItemRepository orderItemRepository,
                        CustomerRepository customerRepository, 
                        ProductRepository productRepository, 
                        OrderStatusValidator statusValidator) {
        this.connection = connection;
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.customerRepository = customerRepository;
        this.productRepository = productRepository;
        this.statusValidator = statusValidator;
    }

    /**
     * AC3 - An order cannot be created without a valid customer and at least one valid order item.
     * AC4 - The system rejects quantities greater than available stock and does not partially create the order.
     * AC6 - Order subtotal, discount, tax, and final total are calculated correctly using BigDecimal.
     */
    public Order createOrder(int customerId, List<OrderItemRequest> requests) {
        if (requests == null || requests.isEmpty()) {
            throw new IllegalArgumentException("Cannot create an order without at least one valid order item.");
        }

        Customer customer = customerRepository.getCustomerById(customerId);
        if (customer == null) {
            throw new IllegalArgumentException("Invalid customer ID.");
        }

        List<OrderItem> items = new ArrayList<>();
        BigDecimal subtotal = BigDecimal.ZERO;

        try {
            connection.setAutoCommit(false);
            
            // Validate stock and build order items
            for (OrderItemRequest req : requests) {
                Product product = productRepository.getProductById(req.getProductId());
                if (product == null) {
                    throw new IllegalArgumentException("Invalid product ID: " + req.getProductId());
                }

                if (req.getQuantity() > product.getQuantityInStock()) {
                    throw new IllegalArgumentException("Requested quantity for product " + product.getName() 
                            + " exceeds available stock. Available: " + product.getQuantityInStock() 
                            + ", Requested: " + req.getQuantity());
                }

                BigDecimal itemSubtotal = product.getPrice().multiply(new BigDecimal(req.getQuantity()));
                subtotal = subtotal.add(itemSubtotal);

                OrderItem item = new OrderItem();
                item.setProductId(product.getProductId());
                item.setQuantity(req.getQuantity());
                item.setUnitPrice(product.getPrice());
                item.setSubtotal(itemSubtotal);
                items.add(item);
            }

            // Calculate totals
            BigDecimal discountAmount = subtotal.multiply(DEFAULT_DISCOUNT_PERCENTAGE).setScale(2, RoundingMode.HALF_UP);
            BigDecimal afterDiscount = subtotal.subtract(discountAmount);
            BigDecimal taxAmount = afterDiscount.multiply(DEFAULT_TAX_PERCENTAGE).setScale(2, RoundingMode.HALF_UP);
            BigDecimal totalAmount = afterDiscount.add(taxAmount);

            // Create Order
            Order order = new Order();
            order.setCustomerId(customerId);
            order.setOrderDate(LocalDateTime.now());
            order.setStatus(OrderStatus.CREATED);
            order.setSubtotal(subtotal);
            order.setDiscountPercentage(DEFAULT_DISCOUNT_PERCENTAGE);
            order.setDiscountAmount(discountAmount);
            order.setTaxPercentage(DEFAULT_TAX_PERCENTAGE);
            order.setTaxAmount(taxAmount);
            order.setTotalAmount(totalAmount);

            int orderId = orderRepository.createOrder(order);
            
            for (OrderItem item : items) {
                item.setOrderId(orderId);
                orderItemRepository.addOrderItem(item);
            }
            
            order.setItems(items);

            connection.commit();
            return order;

        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                e.addSuppressed(rollbackEx);
            }
            throw new RuntimeException("Failed to create order: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                // Ignore
            }
        }
    }

    /**
     * AC5 - Confirming an order reduces stock exactly once
     * AC7 - Invalid status transitions are rejected
     */
    public void confirmOrder(int orderId) {
        changeOrderStatus(orderId, OrderStatus.CONFIRMED);
    }

    /**
     * AC5 - cancelling a confirmed order restores the deducted stock
     * AC7 - Invalid status transitions are rejected
     */
    public void cancelOrder(int orderId) {
        changeOrderStatus(orderId, OrderStatus.CANCELLED);
    }

    public void changeOrderStatus(int orderId, OrderStatus newStatus) {
        Order order = orderRepository.getOrderById(orderId);
        if (order == null) {
            throw new IllegalArgumentException("Order not found: " + orderId);
        }

        statusValidator.validateTransition(order.getStatus(), newStatus);

        try {
            connection.setAutoCommit(false);
            
            // Handle stock deduction/restoration for CONFIRMED/CANCELLED
            if (newStatus == OrderStatus.CONFIRMED && order.getStatus() == OrderStatus.CREATED) {
                List<OrderItem> items = orderItemRepository.getOrderItemsByOrderId(orderId);
                for (OrderItem item : items) {
                    Product product = productRepository.getProductById(item.getProductId());
                    productRepository.updateProductStock(product.getProductId(), 
                        product.getQuantityInStock() - item.getQuantity());
                }
            } else if (newStatus == OrderStatus.CANCELLED && order.getStatus() != OrderStatus.CREATED) {
                // If cancelled after it was confirmed (meaning stock was deducted), restore stock
                List<OrderItem> items = orderItemRepository.getOrderItemsByOrderId(orderId);
                for (OrderItem item : items) {
                    Product product = productRepository.getProductById(item.getProductId());
                    productRepository.updateProductStock(product.getProductId(), 
                        product.getQuantityInStock() + item.getQuantity());
                }
            }

            orderRepository.updateOrderStatus(orderId, newStatus);
            connection.commit();

        } catch (Exception e) {
            try {
                connection.rollback();
            } catch (SQLException rollbackEx) {
                e.addSuppressed(rollbackEx);
            }
            throw new RuntimeException("Failed to change order status: " + e.getMessage(), e);
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                // Ignore
            }
        }
    }

    public Order getOrderDetails(int orderId) {
        Order order = orderRepository.getOrderById(orderId);
        if (order != null) {
            List<OrderItem> items = orderItemRepository.getOrderItemsByOrderId(orderId);
            order.setItems(items);
        }
        return order;
    }
}
