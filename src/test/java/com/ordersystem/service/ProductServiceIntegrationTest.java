package com.ordersystem.service;

import com.ordersystem.exception.InvalidPriceException;
import com.ordersystem.exception.InvalidStockQuantityException;
import com.ordersystem.exception.ProductNotFoundException;
import com.ordersystem.exception.InsufficientStockException;
import com.ordersystem.model.Product;
import com.ordersystem.repository.impl.ProductRepositoryJdbc;
import com.ordersystem.util.DbConnection;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;

/**
 * Integration Tests for ProductService
 * Tests exception handling and integration with ProductRepositoryJdbc and SQLite database
 */
@DisplayName("ProductService Integration Tests")
class ProductServiceIntegrationTest {

    private ProductService productService;
    private ProductRepositoryJdbc productRepository;

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
        
        // Create fresh service and repository instances
        productRepository = new ProductRepositoryJdbc();
        productService = new ProductService(productRepository);
    }

    // ==================== CREATE PRODUCT TESTS ====================

    @Test
    @DisplayName("AC1-1: Should create product with valid price and stock")
    void testCreateProductWithValidPriceAndStock() {
        // Arrange
        Product product = new Product(null, "Laptop", new BigDecimal("999.99"), 10);

        // Act
        Product createdProduct = productService.createProduct(product);

        // Assert
        assertNotNull(createdProduct.getId(), "Product ID should be generated");
        assertEquals("Laptop", createdProduct.getName());
        assertEquals(new BigDecimal("999.99"), createdProduct.getPrice());
        assertEquals(10, createdProduct.getStockQuantity());
    }

    @Test
    @DisplayName("AC1-2: Should throw InvalidPriceException when price is negative")
    void testCreateProductWithNegativePrice() {
        // Arrange
        Product product = new Product(null, "Laptop", new BigDecimal("-100.00"), 10);

        // Act & Assert
        InvalidPriceException exception = assertThrows(InvalidPriceException.class, () -> {
            productService.createProduct(product);
        });

        assertTrue(exception.getMessage().contains("Invalid price"));
        assertTrue(exception.getMessage().contains("Price cannot be negative"));
    }

    @Test
    @DisplayName("AC1-3: Should throw InvalidStockQuantityException when stock is negative")
    void testCreateProductWithNegativeStock() {
        // Arrange
        Product product = new Product(null, "Laptop", new BigDecimal("999.99"), -5);

        // Act & Assert
        InvalidStockQuantityException exception = assertThrows(InvalidStockQuantityException.class, () -> {
            productService.createProduct(product);
        });

        assertTrue(exception.getMessage().contains("Invalid stock quantity"));
        assertTrue(exception.getMessage().contains("Quantity cannot be negative"));
    }

    @Test
    @DisplayName("AC1-4: Should create product with zero price (edge case)")
    void testCreateProductWithZeroPrice() {
        // Arrange
        Product product = new Product(null, "Free Item", new BigDecimal("0.00"), 5);

        // Act
        Product createdProduct = productService.createProduct(product);

        // Assert
        assertNotNull(createdProduct.getId());
        assertEquals(new BigDecimal("0.00"), createdProduct.getPrice());
    }

    @Test
    @DisplayName("AC1-5: Should create product with zero stock (edge case)")
    void testCreateProductWithZeroStock() {
        // Arrange
        Product product = new Product(null, "Out of Stock Item", new BigDecimal("50.00"), 0);

        // Act
        Product createdProduct = productService.createProduct(product);

        // Assert
        assertNotNull(createdProduct.getId());
        assertEquals(0, createdProduct.getStockQuantity());
    }

    // ==================== UPDATE PRODUCT TESTS ====================

    @Test
    @DisplayName("AC2-1: Should update product with valid data")
    void testUpdateProductWithValidData() {
        // Arrange
        Product original = productService.createProduct(
            new Product(null, "Keyboard", new BigDecimal("50.00"), 20)
        );
        Product update = new Product(original.getId(), "Mechanical Keyboard", new BigDecimal("75.00"), 15);

        // Act
        Product updated = productService.updateProduct(update);

        // Assert
        assertEquals("Mechanical Keyboard", updated.getName());
        assertEquals(new BigDecimal("75.00"), updated.getPrice());
        assertEquals(15, updated.getStockQuantity());
    }

    @Test
    @DisplayName("AC2-2: Should throw ProductNotFoundException when updating non-existent product")
    void testUpdateNonExistentProduct() {
        // Arrange
        Product product = new Product("non-existent-id", "Mouse", new BigDecimal("25.00"), 50);

        // Act & Assert
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.updateProduct(product);
        });

        assertTrue(exception.getMessage().contains("Product not found"));
    }

    @Test
    @DisplayName("AC2-3: Should throw ProductNotFoundException when product ID is null")
    void testUpdateProductWithNullId() {
        // Arrange
        Product product = new Product(null, "Mouse", new BigDecimal("25.00"), 50);

        // Act & Assert
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.updateProduct(product);
        });

        assertTrue(exception.getMessage().contains("Product not found"));
    }

    @Test
    @DisplayName("AC2-4: Should throw InvalidPriceException when updating with negative price")
    void testUpdateProductWithNegativePrice() {
        // Arrange
        Product original = productService.createProduct(
            new Product(null, "Monitor", new BigDecimal("300.00"), 5)
        );
        Product update = new Product(original.getId(), "Monitor", new BigDecimal("-100.00"), 5);

        // Act & Assert
        InvalidPriceException exception = assertThrows(InvalidPriceException.class, () -> {
            productService.updateProduct(update);
        });

        assertTrue(exception.getMessage().contains("Invalid price"));
    }

    @Test
    @DisplayName("AC2-5: Should throw InvalidStockQuantityException when updating with negative stock")
    void testUpdateProductWithNegativeStock() {
        // Arrange
        Product original = productService.createProduct(
            new Product(null, "Headphones", new BigDecimal("80.00"), 10)
        );
        Product update = new Product(original.getId(), "Headphones", new BigDecimal("80.00"), -5);

        // Act & Assert
        InvalidStockQuantityException exception = assertThrows(InvalidStockQuantityException.class, () -> {
            productService.updateProduct(update);
        });

        assertTrue(exception.getMessage().contains("Invalid stock quantity"));
    }

    @Test
    @DisplayName("AC2-6: Should preserve original values when fields are not updated")
    void testUpdateProductPreservesUnchangedFields() {
        // Arrange
        Product original = productService.createProduct(
            new Product(null, "Router", new BigDecimal("120.00"), 8)
        );
        Product update = new Product(original.getId(), null, null, 0);

        // Act
        Product updated = productService.updateProduct(update);

        // Assert
        assertEquals("Router", updated.getName(), "Name should be preserved");
        assertEquals(new BigDecimal("120.00"), updated.getPrice(), "Price should be preserved");
    }

    // ==================== DELETE PRODUCT TESTS ====================

    @Test
    @DisplayName("AC2-7: Should delete existing product")
    void testDeleteExistingProduct() {
        // Arrange
        Product product = productService.createProduct(
            new Product(null, "Speaker", new BigDecimal("60.00"), 12)
        );

        // Act
        productService.deleteProduct(product.getId());

        // Assert
        assertThrows(ProductNotFoundException.class, () -> {
            productService.updateProduct(product);
        });
    }

    @Test
    @DisplayName("AC2-8: Should throw ProductNotFoundException when deleting non-existent product")
    void testDeleteNonExistentProduct() {
        // Act & Assert
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.deleteProduct("non-existent-id");
        });

        assertTrue(exception.getMessage().contains("Product not found"));
    }

    @Test
    @DisplayName("AC2-9: Should throw ProductNotFoundException when product ID is null")
    void testDeleteProductWithNullId() {
        // Act & Assert
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.deleteProduct(null);
        });

        assertTrue(exception.getMessage().contains("Product not found"));
    }

    // ==================== REDUCE STOCK TESTS ====================

    @Test
    @DisplayName("AC4-1: Should reduce stock when sufficient quantity available")
    void testReduceStockWithSufficientQuantity() {
        // Arrange
        Product product = productService.createProduct(
            new Product(null, "USB Cable", new BigDecimal("5.00"), 100)
        );

        // Act
        productService.reduceStock(product.getId(), 30);

        // Assert
        Product updated = productRepository.findById(product.getId()).orElse(null);
        assertNotNull(updated);
        assertEquals(70, updated.getStockQuantity());
    }

    @Test
    @DisplayName("AC4-2: Should throw InsufficientStockException when quantity exceeds available")
    void testReduceStockWithInsufficientQuantity() {
        // Arrange
        Product product = productService.createProduct(
            new Product(null, "Mouse Pad", new BigDecimal("10.00"), 20)
        );

        // Act & Assert
        InsufficientStockException exception = assertThrows(InsufficientStockException.class, () -> {
            productService.reduceStock(product.getId(), 50);
        });

        assertTrue(exception.getMessage().contains("Insufficient stock"));
        assertTrue(exception.getMessage().contains("requested 50"));
        assertTrue(exception.getMessage().contains("available 20"));
    }

    @Test
    @DisplayName("AC4-3: Should throw InvalidStockQuantityException when quantity is zero")
    void testReduceStockWithZeroQuantity() {
        // Arrange
        Product product = productService.createProduct(
            new Product(null, "HDMI Cable", new BigDecimal("15.00"), 50)
        );

        // Act & Assert
        InvalidStockQuantityException exception = assertThrows(InvalidStockQuantityException.class, () -> {
            productService.reduceStock(product.getId(), 0);
        });

        assertTrue(exception.getMessage().contains("Invalid stock quantity"));
    }

    @Test
    @DisplayName("AC4-4: Should throw InvalidStockQuantityException when quantity is negative")
    void testReduceStockWithNegativeQuantity() {
        // Arrange
        Product product = productService.createProduct(
            new Product(null, "Power Cable", new BigDecimal("8.00"), 75)
        );

        // Act & Assert
        InvalidStockQuantityException exception = assertThrows(InvalidStockQuantityException.class, () -> {
            productService.reduceStock(product.getId(), -10);
        });

        assertTrue(exception.getMessage().contains("Invalid stock quantity"));
    }

    @Test
    @DisplayName("AC4-5: Should throw ProductNotFoundException when product doesn't exist")
    void testReduceStockNonExistentProduct() {
        // Act & Assert
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.reduceStock("non-existent-id", 5);
        });

        assertTrue(exception.getMessage().contains("Product not found"));
    }

    @Test
    @DisplayName("AC4-6: Should reduce stock to exactly zero")
    void testReduceStockToZero() {
        // Arrange
        Product product = productService.createProduct(
            new Product(null, "Adapter", new BigDecimal("12.00"), 25)
        );

        // Act
        productService.reduceStock(product.getId(), 25);

        // Assert
        Product updated = productRepository.findById(product.getId()).orElse(null);
        assertNotNull(updated);
        assertEquals(0, updated.getStockQuantity());
    }

    // ==================== RESTORE STOCK TESTS ====================

    @Test
    @DisplayName("AC5-1: Should restore stock when valid quantity provided")
    void testRestoreStockWithValidQuantity() {
        // Arrange
        Product product = productService.createProduct(
            new Product(null, "Monitor Cable", new BigDecimal("18.00"), 30)
        );
        productService.reduceStock(product.getId(), 15);

        // Act
        productService.restoreStock(product.getId(), 10);

        // Assert
        Product updated = productRepository.findById(product.getId()).orElse(null);
        assertNotNull(updated);
        assertEquals(25, updated.getStockQuantity());
    }

    @Test
    @DisplayName("AC5-2: Should throw InvalidStockQuantityException when quantity is zero")
    void testRestoreStockWithZeroQuantity() {
        // Arrange
        Product product = productService.createProduct(
            new Product(null, "Keyboard Cable", new BigDecimal("7.00"), 20)
        );

        // Act & Assert
        InvalidStockQuantityException exception = assertThrows(InvalidStockQuantityException.class, () -> {
            productService.restoreStock(product.getId(), 0);
        });

        assertTrue(exception.getMessage().contains("Invalid stock quantity"));
    }

    @Test
    @DisplayName("AC5-3: Should throw InvalidStockQuantityException when quantity is negative")
    void testRestoreStockWithNegativeQuantity() {
        // Arrange
        Product product = productService.createProduct(
            new Product(null, "USB Hub", new BigDecimal("25.00"), 10)
        );

        // Act & Assert
        InvalidStockQuantityException exception = assertThrows(InvalidStockQuantityException.class, () -> {
            productService.restoreStock(product.getId(), -5);
        });

        assertTrue(exception.getMessage().contains("Invalid stock quantity"));
    }

    @Test
    @DisplayName("AC5-4: Should throw ProductNotFoundException when product doesn't exist")
    void testRestoreStockNonExistentProduct() {
        // Act & Assert
        ProductNotFoundException exception = assertThrows(ProductNotFoundException.class, () -> {
            productService.restoreStock("non-existent-id", 5);
        });

        assertTrue(exception.getMessage().contains("Product not found"));
    }

    @Test
    @DisplayName("AC5-5: Should restore large quantities correctly")
    void testRestoreStockWithLargeQuantity() {
        // Arrange
        Product product = productService.createProduct(
            new Product(null, "Webcam", new BigDecimal("50.00"), 100)
        );
        productService.reduceStock(product.getId(), 80);

        // Act
        productService.restoreStock(product.getId(), 60);

        // Assert
        Product updated = productRepository.findById(product.getId()).orElse(null);
        assertNotNull(updated);
        assertEquals(80, updated.getStockQuantity());
    }

    // ==================== FIND LOW STOCK TESTS ====================

    @Test
    @DisplayName("Should find all products below stock threshold")
    void testFindLowStockProducts() {
        // Arrange
        productService.createProduct(new Product(null, "Item1", new BigDecimal("10.00"), 5));
        productService.createProduct(new Product(null, "Item2", new BigDecimal("20.00"), 15));
        productService.createProduct(new Product(null, "Item3", new BigDecimal("30.00"), 2));

        // Act
        var lowStockProducts = productService.findLowStockProducts(10);

        // Assert
        assertEquals(2, lowStockProducts.size(), "Should find 2 products with stock <= 10");
    }

    // ==================== LIST PRODUCTS TEST ====================

    @Test
    @DisplayName("Should list all products")
    void testListProducts() {
        // Arrange
        productService.createProduct(new Product(null, "Product1", new BigDecimal("10.00"), 5));
        productService.createProduct(new Product(null, "Product2", new BigDecimal("20.00"), 10));

        // Act
        var allProducts = productService.listProducts();

        // Assert
        assertEquals(2, allProducts.size(), "Should list all 2 created products");
    }

    // ==================== COMPLEX SCENARIO TESTS ====================

    @Test
    @DisplayName("Complex Scenario: Create, reduce, restore, and verify stock")
    void testComplexStockManagementScenario() {
        // Arrange - Create product
        Product product = productService.createProduct(
            new Product(null, "Complex Product", new BigDecimal("99.99"), 100)
        );

        // Act & Assert - Reduce stock
        productService.reduceStock(product.getId(), 30);
        Product afterReduce = productRepository.findById(product.getId()).orElse(null);
        assertNotNull(afterReduce);
        assertEquals(70, afterReduce.getStockQuantity());

        // Act & Assert - Reduce more
        productService.reduceStock(product.getId(), 50);
        Product afterSecondReduce = productRepository.findById(product.getId()).orElse(null);
        assertNotNull(afterSecondReduce);
        assertEquals(20, afterSecondReduce.getStockQuantity());

        // Act & Assert - Restore stock
        productService.restoreStock(product.getId(), 30);
        Product afterRestore = productRepository.findById(product.getId()).orElse(null);
        assertNotNull(afterRestore);
        assertEquals(50, afterRestore.getStockQuantity());

        // Act & Assert - Verify insufficient stock exception
        InsufficientStockException exception = assertThrows(InsufficientStockException.class, () -> {
            productService.reduceStock(product.getId(), 100);
        });
        assertTrue(exception.getMessage().contains("Insufficient stock"));
    }

    @Test
    @DisplayName("Complex Scenario: Multiple products with stock management")
    void testMultipleProductsStockManagement() {
        // Arrange - Create multiple products
        Product product1 = productService.createProduct(
            new Product(null, "Product A", new BigDecimal("50.00"), 50)
        );
        Product product2 = productService.createProduct(
            new Product(null, "Product B", new BigDecimal("75.00"), 30)
        );

        // Act - Manage stock for both products
        productService.reduceStock(product1.getId(), 20);
        productService.reduceStock(product2.getId(), 10);

        // Assert - Verify all products
        Product p1Updated = productRepository.findById(product1.getId()).orElse(null);
        Product p2Updated = productRepository.findById(product2.getId()).orElse(null);
        
        assertNotNull(p1Updated);
        assertNotNull(p2Updated);
        assertEquals(30, p1Updated.getStockQuantity());
        assertEquals(20, p2Updated.getStockQuantity());

        // Act & Assert - Restore and verify
        productService.restoreStock(product1.getId(), 15);
        Product p1Final = productRepository.findById(product1.getId()).orElse(null);
        assertNotNull(p1Final);
        assertEquals(45, p1Final.getStockQuantity());
    }
}
