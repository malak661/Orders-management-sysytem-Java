package com.ordersystem.service;

import com.ordersystem.model.Order;
import com.ordersystem.model.OrderStatus;
import com.ordersystem.model.Product;
import com.ordersystem.model.Customer;
import com.ordersystem.repository.impl.CustomerRepositoryJdbc;
import com.ordersystem.repository.impl.OrderRepositoryJdbc;
import com.ordersystem.repository.impl.ProductRepositoryJdbc;
import com.ordersystem.util.DbConnection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Tests for ReportService
 * Tests reporting functionality including customer order history, low-stock products,
 * unpaid orders, and total sales calculation (AC10)
 */
@DisplayName("ReportService Integration Tests")
class ReportServiceIntegrationTest {

    private ReportService reportService;
    private ProductRepositoryJdbc productRepository;
    private OrderRepositoryJdbc orderRepository;
    private CustomerRepositoryJdbc customerRepository;
    private ProductService productService;

    @BeforeAll
    static void setupDatabase() {
        // Clean up existing test database if it exists
        try {
            Files.deleteIfExists(Path.of("orders.db"));
        } catch (Exception e) {
            // Ignore if file doesn't exist
        }
        
        // Initialize schema
        DbConnection.initializeSchema();
    }

    @BeforeEach
    void setUp() {
        // Clean up database before each test
        try {
            Files.deleteIfExists(Path.of("orders.db"));
            DbConnection.initializeSchema();
        } catch (Exception e) {
            fail("Failed to setup test database: " + e.getMessage());
        }
        
        // Create fresh repository and service instances
        productRepository = new ProductRepositoryJdbc();
        orderRepository = new OrderRepositoryJdbc();
        customerRepository = new CustomerRepositoryJdbc();
        productService = new ProductService(productRepository);
        
        reportService = new ReportService(orderRepository, productRepository);
    }

    // ==================== CUSTOMER ORDER HISTORY TESTS ====================

    @Test
    @DisplayName("AC10-1: Should return empty list when customer ID is null")
    void testGetCustomerOrderHistoryWithNullCustomerId() {
        // Act
        List<Order> orders = reportService.getCustomerOrderHistory(null);

        // Assert
        assertNotNull(orders);
        assertTrue(orders.isEmpty(), "Should return empty list for null customer ID");
    }

    @Test
    @DisplayName("AC10-2: Should return empty list when customer ID is blank")
    void testGetCustomerOrderHistoryWithBlankCustomerId() {
        // Act
        List<Order> orders = reportService.getCustomerOrderHistory("   ");

        // Assert
        assertNotNull(orders);
        assertTrue(orders.isEmpty(), "Should return empty list for blank customer ID");
    }

    @Test
    @DisplayName("AC10-3: Should return empty list when customer has no orders")
    void testGetCustomerOrderHistoryWithNoOrders() {
        // Arrange
        Customer customer = new Customer(0, "John Doe", "john@email.com", 1234567890, "123 Main St");
        Customer savedCustomer = customerRepository.save(customer);

        // Act
        List<Order> orders = reportService.getCustomerOrderHistory(String.valueOf(savedCustomer.getId()));

        // Assert
        assertNotNull(orders);
        assertTrue(orders.isEmpty(), "Should return empty list when customer has no orders");
    }

    @Test
    @DisplayName("AC10-4: Should return all orders for a specific customer")
    void testGetCustomerOrderHistoryWithMultipleOrders() {
        // Arrange
        Customer customer = new Customer(0, "John Doe", "john@email.com", 1234567890, "123 Main St");
        Customer savedCustomer = customerRepository.save(customer);
        
        String customerId = String.valueOf(savedCustomer.getId());
        Order order1 = new Order("O001", customerId, List.of(), OrderStatus.CREATED, 
                BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("100.00"), new BigDecimal("100.00"), 
                LocalDateTime.now(), LocalDateTime.now());
        Order order2 = new Order("O002", customerId, List.of(), OrderStatus.PAID, 
                BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("200.00"), new BigDecimal("200.00"), 
                LocalDateTime.now(), LocalDateTime.now());
        
        orderRepository.save(order1);
        orderRepository.save(order2);

        // Act
        List<Order> orders = reportService.getCustomerOrderHistory(customerId);

        // Assert
        assertNotNull(orders);
        assertEquals(2, orders.size(), "Should return 2 orders for the customer");
        assertTrue(orders.stream().allMatch(o -> o.getCustomerId().equals(customerId)),
                "All orders should belong to the customer");
    }

    // ==================== LOW STOCK PRODUCTS TESTS ====================

    @Test
    @DisplayName("AC10-5: Should return empty list when no products are below threshold")
    void testGetLowStockProductsWithNoLowStock() {
        // Arrange
        Product product = new Product(null, "Laptop", new BigDecimal("999.99"), 100);
        productService.createProduct(product);

        // Act
        List<Product> lowStockProducts = reportService.getLowStockProducts(50);

        // Assert
        assertNotNull(lowStockProducts);
        assertTrue(lowStockProducts.isEmpty(), "Should return empty list when no products are below threshold");
    }

    @Test
    @DisplayName("AC10-6: Should return products with stock below threshold")
    void testGetLowStockProductsWithMultipleLowStockProducts() {
        // Arrange
        Product product1 = new Product(null, "Laptop", new BigDecimal("999.99"), 5);
        Product product2 = new Product(null, "Mouse", new BigDecimal("29.99"), 15);
        Product product3 = new Product(null, "Keyboard", new BigDecimal("79.99"), 100);
        
        productService.createProduct(product1);
        productService.createProduct(product2);
        productService.createProduct(product3);

        // Act
        List<Product> lowStockProducts = reportService.getLowStockProducts(20);

        // Assert
        assertNotNull(lowStockProducts);
        assertEquals(2, lowStockProducts.size(), "Should return 2 products with stock below 20");
        assertTrue(lowStockProducts.stream().allMatch(p -> p.getStockQuantity() < 20),
                "All returned products should have stock below threshold");
    }

    @Test
    @DisplayName("AC10-7: Should handle zero threshold")
    void testGetLowStockProductsWithZeroThreshold() {
        // Arrange
        Product product1 = new Product(null, "Laptop", new BigDecimal("999.99"), 0);
        Product product2 = new Product(null, "Mouse", new BigDecimal("29.99"), 10);
        
        productService.createProduct(product1);
        productService.createProduct(product2);

        // Act
        List<Product> lowStockProducts = reportService.getLowStockProducts(0);

        // Assert
        assertNotNull(lowStockProducts);
        assertEquals(1, lowStockProducts.size(), "Should return only products with stock = 0");
    }

    @Test
    @DisplayName("AC10-8: Should handle negative threshold by treating it as 0")
    void testGetLowStockProductsWithNegativeThreshold() {
        // Arrange
        Product product = new Product(null, "Laptop", new BigDecimal("999.99"), 0);
        productService.createProduct(product);

        // Act
        List<Product> lowStockProducts = reportService.getLowStockProducts(-10);

        // Assert
        assertNotNull(lowStockProducts);
        assertTrue(lowStockProducts.size() > 0, "Should treat negative threshold as 0");
    }

    // ==================== UNPAID ORDERS TESTS ====================

    @Test
    @DisplayName("AC10-9: Should return empty list when there are no unpaid orders")
    void testGetUnpaidOrdersWithNoUnpaidOrders() {
        // Arrange
        Customer customer = new Customer(0, "John Doe", "john@email.com", 1234567890, "123 Main St");
        Customer savedCustomer = customerRepository.save(customer);
        String customerId = String.valueOf(savedCustomer.getId());
        
        Order order = new Order("O001", customerId, List.of(), OrderStatus.PAID, 
                BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("100.00"), new BigDecimal("100.00"), 
                LocalDateTime.now(), LocalDateTime.now());
        orderRepository.save(order);

        // Act
        List<Order> unpaidOrders = reportService.getUnpaidOrders();

        // Assert
        assertNotNull(unpaidOrders);
        assertTrue(unpaidOrders.isEmpty(), "Should return empty list when all orders are paid");
    }

    @Test
    @DisplayName("AC10-10: Should return all unpaid orders")
    void testGetUnpaidOrdersWithMultipleUnpaidOrders() {
        // Arrange
        Customer customer = new Customer(0, "John Doe", "john@email.com", 1234567890, "123 Main St");
        Customer savedCustomer = customerRepository.save(customer);
        String customerId = String.valueOf(savedCustomer.getId());
        
        // Note: findUnpaid() only returns orders with status = CONFIRMED
        Order order1 = new Order("O001", customerId, List.of(), OrderStatus.CONFIRMED, 
                BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("100.00"), new BigDecimal("100.00"), 
                LocalDateTime.now(), LocalDateTime.now());
        Order order2 = new Order("O002", customerId, List.of(), OrderStatus.CONFIRMED, 
                BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("200.00"), new BigDecimal("200.00"), 
                LocalDateTime.now(), LocalDateTime.now());
        Order order3 = new Order("O003", customerId, List.of(), OrderStatus.PAID, 
                BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("300.00"), new BigDecimal("300.00"), 
                LocalDateTime.now(), LocalDateTime.now());
        
        orderRepository.save(order1);
        orderRepository.save(order2);
        orderRepository.save(order3);

        // Act
        List<Order> unpaidOrders = reportService.getUnpaidOrders();

        // Assert
        assertNotNull(unpaidOrders);
        assertEquals(2, unpaidOrders.size(), "Should return 2 unpaid orders with CONFIRMED status");
        assertTrue(unpaidOrders.stream().allMatch(o -> o.getStatus() == OrderStatus.CONFIRMED),
                "All returned orders should have CONFIRMED status");
    }

    // ==================== TOTAL SALES TESTS ====================

    @Test
    @DisplayName("AC10-11: Should return zero when there are no orders")
    void testGetTotalSalesWithNoOrders() {
        // Act
        BigDecimal totalSales = reportService.getTotalSales();

        // Assert
        assertNotNull(totalSales);
        assertEquals(BigDecimal.ZERO, totalSales, "Should return zero when there are no orders");
    }

    @Test
    @DisplayName("AC10-12: Should calculate total sales from all orders")
    void testGetTotalSalesWithMultipleOrders() {
        // Arrange
        Customer customer = new Customer(0, "John Doe", "john@email.com", 1234567890, "123 Main St");
        Customer savedCustomer = customerRepository.save(customer);
        String customerId = String.valueOf(savedCustomer.getId());
        
        Order order1 = new Order("O001", customerId, List.of(), OrderStatus.PAID, 
                BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("100.00"), new BigDecimal("100.00"), 
                LocalDateTime.now(), LocalDateTime.now());
        Order order2 = new Order("O002", customerId, List.of(), OrderStatus.PAID, 
                BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("250.50"), new BigDecimal("250.50"), 
                LocalDateTime.now(), LocalDateTime.now());
        Order order3 = new Order("O003", customerId, List.of(), OrderStatus.CREATED, 
                BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("50.00"), new BigDecimal("50.00"), 
                LocalDateTime.now(), LocalDateTime.now());
        
        orderRepository.save(order1);
        orderRepository.save(order2);
        orderRepository.save(order3);

        // Act
        BigDecimal totalSales = reportService.getTotalSales();

        // Assert
        assertNotNull(totalSales);
        assertEquals(new BigDecimal("400.50"), totalSales, "Should sum all order totals correctly");
    }

    @Test
    @DisplayName("AC10-13: Should handle null order totals gracefully")
    void testGetTotalSalesWithNullOrderTotals() {
        // Arrange
        Customer customer = new Customer(0, "John Doe", "john@email.com", 1234567890, "123 Main St");
        Customer savedCustomer = customerRepository.save(customer);
        String customerId = String.valueOf(savedCustomer.getId());
        
        Order order1 = new Order("O001", customerId, List.of(), OrderStatus.PAID, 
                BigDecimal.ZERO, BigDecimal.ZERO, new BigDecimal("100.00"), new BigDecimal("100.00"), 
                LocalDateTime.now(), LocalDateTime.now());
        Order order2 = new Order("O002", customerId, List.of(), OrderStatus.CREATED, 
                BigDecimal.ZERO, BigDecimal.ZERO, null, null, 
                LocalDateTime.now(), LocalDateTime.now());
        
        orderRepository.save(order1);
        orderRepository.save(order2);

        // Act
        BigDecimal totalSales = reportService.getTotalSales();

        // Assert
        assertNotNull(totalSales);
        assertEquals(new BigDecimal("100.00"), totalSales, "Should skip null totals and sum valid ones");
    }

    @Test
    @DisplayName("AC10-14: Should return zero for single order with zero amount")
    void testGetTotalSalesWithZeroAmountOrder() {
        // Arrange
        Customer customer = new Customer(0, "John Doe", "john@email.com", 1234567890, "123 Main St");
        Customer savedCustomer = customerRepository.save(customer);
        String customerId = String.valueOf(savedCustomer.getId());
        
        Order order = new Order("O001", customerId, List.of(), OrderStatus.CREATED, 
                BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, 
                LocalDateTime.now(), LocalDateTime.now());
        orderRepository.save(order);

        // Act
        BigDecimal totalSales = reportService.getTotalSales();

        // Assert
        assertNotNull(totalSales);
        assertEquals(BigDecimal.ZERO, totalSales, "Should correctly handle zero-amount orders");
    }
}
