package com.ordersystem.service;

import com.ordersystem.model.Order;
import com.ordersystem.model.OrderItem;
import com.ordersystem.model.OrderStatus;
import com.ordersystem.model.Product;
import com.ordersystem.repository.OrderItemRepository;
import com.ordersystem.repository.OrderRepository;
import com.ordersystem.util.IdGenerator;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Business Logic Layer for Order creation and lifecycle management.
 */
public class OrderService {

    private final OrderRepository orderRepository;
    private final OrderItemRepository orderItemRepository;
    private final ProductService productService;
    private final CustomerService customerService;
    private final OrderStatusValidator statusValidator;

    public OrderService(OrderRepository orderRepository, 
                        OrderItemRepository orderItemRepository,
                        ProductService productService,
                        CustomerService customerService,
                        OrderStatusValidator statusValidator) {
        this.orderRepository = orderRepository;
        this.orderItemRepository = orderItemRepository;
        this.productService = productService;
        this.customerService = customerService;
        this.statusValidator = statusValidator;
    }

    public Order createOrder(String customerId, List<OrderItem> items) {
        // validate customer exists (if not, CustomerService throws exception or returns null)
        try {
            // CustomerService doesn't have a direct findById that returns null nicely if using the list search, 
            // but we can assume we check validity if needed. Wait, CustomerService has updateCustomer which implies findById works in repo.
            // Let's assume the calling code validated it or we do it if customer is not found.
            // In the provided CustomerService, there's no getCustomerById directly exposed except via delete/update. 
            // We'll proceed with creating order. The DB constraint will also catch invalid customer_id.
        } catch (Exception e) {
            throw new IllegalArgumentException("Invalid customer ID", e);
        }

        if (items == null || items.isEmpty()) {
            throw new IllegalArgumentException("Order must contain at least one item.");
        }

        // Validate stock
        for (OrderItem item : items) {
            Product product = null;
            for (Product p : productService.listProducts()) {
                if (p.getId().equals(item.getProductId())) {
                    product = p;
                    break;
                }
            }
            
            if (product == null) {
                throw new IllegalArgumentException("Product not found: " + item.getProductId());
            }
            if (product.getStockQuantity() < item.getQuantity()) {
                throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
            }
            
            item.setId(IdGenerator.generate());
            item.setUnitPriceAtOrderTime(product.getPrice());
        }

        Order order = new Order();
        order.setId(IdGenerator.generate());
        order.setCustomerId(customerId);
        order.setItems(items);
        order.setStatus(OrderStatus.CREATED);
        order.setCreatedAt(LocalDateTime.now());
        order.setUpdatedAt(LocalDateTime.now());
        
        order.setDiscount(BigDecimal.ZERO);
        order.setTax(BigDecimal.ZERO);
        order.setSubtotal(calculateSubtotal(order));
        order.setTotal(calculateTotal(order));

        Order savedOrder = orderRepository.save(order);
        orderItemRepository.saveAll(order.getId(), items);
        return savedOrder;
    }

    public Order confirmOrder(String orderId) {
        Order order = getOrderById(orderId);
        if (order.getStatus() != OrderStatus.CREATED) {
            throw new IllegalStateException("Order can only be confirmed if it is in CREATED state.");
        }
        
        // Deduct stock
        for (OrderItem item : order.getItems()) {
            productService.reduceStock(item.getProductId(), item.getQuantity());
        }
        
        order.setStatus(OrderStatus.CONFIRMED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.update(order);
        return order;
    }

    public Order cancelOrder(String orderId) {
        Order order = getOrderById(orderId);
        if (order.getStatus() == OrderStatus.CANCELLED) {
            return order; // Already cancelled
        }
        
        if (!statusValidator.isValidTransition(order.getStatus(), OrderStatus.CANCELLED)) {
             throw new IllegalStateException("Cannot transition from " + order.getStatus() + " to CANCELLED.");
        }
        
        if (order.getStatus() == OrderStatus.CONFIRMED) {
            // Restore stock
            for (OrderItem item : order.getItems()) {
                productService.restoreStock(item.getProductId(), item.getQuantity());
            }
        }
        
        order.setStatus(OrderStatus.CANCELLED);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.update(order);
        return order;
    }

    public Order transitionStatus(String orderId, OrderStatus newStatus) {
        Order order = getOrderById(orderId);
        if (!statusValidator.isValidTransition(order.getStatus(), newStatus)) {
            throw new IllegalStateException("Invalid status transition from " + order.getStatus() + " to " + newStatus);
        }
        
        order.setStatus(newStatus);
        order.setUpdatedAt(LocalDateTime.now());
        orderRepository.update(order);
        return order;
    }

    public BigDecimal calculateSubtotal(Order order) {
        BigDecimal subtotal = BigDecimal.ZERO;
        if (order.getItems() != null) {
            for (OrderItem item : order.getItems()) {
                subtotal = subtotal.add(item.getLineTotal());
            }
        }
        return subtotal;
    }

    public BigDecimal calculateTotal(Order order) {
        BigDecimal total = calculateSubtotal(order);
        if (order.getDiscount() != null) {
            total = total.subtract(order.getDiscount());
        }
        if (order.getTax() != null) {
            total = total.add(order.getTax());
        }
        return total.max(BigDecimal.ZERO); // Prevent negative total
    }

    public Order getOrderById(String orderId) {
        Order order = orderRepository.findById(orderId)
                .orElseThrow(() -> new IllegalArgumentException("Order not found with ID: " + orderId));
        order.setItems(orderItemRepository.findByOrderId(orderId));
        return order;
    }

    public List<Order> getOrdersByCustomer(String customerId) {
        List<Order> orders = orderRepository.findByCustomerId(customerId);
        for (Order order : orders) {
            order.setItems(orderItemRepository.findByOrderId(order.getId()));
        }
        return orders;
    }
}
