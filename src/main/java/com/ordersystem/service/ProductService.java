package com.ordersystem.service;

import com.ordersystem.model.Product;
import com.ordersystem.repository.ProductRepository;
import com.ordersystem.exception.InvalidPriceException;
import com.ordersystem.exception.InvalidStockQuantityException;
import com.ordersystem.exception.ProductNotFoundException;
import com.ordersystem.exception.InsufficientStockException;

import java.util.List;

/**
 * Business Logic Layer for Product & Inventory operations.
 * Responsibilities (AC1, AC2, AC4, AC5):
 * - create(Product) -> reject negative price/stock
 * - update(Product) -> validate and update product
 * - search / list -> retrieve products
 * - delete(String id) -> remove product
 * - reduceStock(productId, quantity) -> used when order is confirmed
 * - restoreStock(productId, quantity) -> used when confirmed order is cancelled
 * - findLowStock(threshold) -> for reports
 */
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    /**
     * Creates a new product with validation (AC1).
     * Throws InvalidPriceException if price is negative.
     * Throws InvalidStockQuantityException if stock quantity is negative.
     */
    public Product createProduct(Product product) {
        // Validate price >= 0 (AC1)
        if (product.getPrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new InvalidPriceException(product.getPrice());
        }
        // Validate stock >= 0 (AC1)
        if (product.getStockQuantity() < 0) {
            throw new InvalidStockQuantityException(product.getStockQuantity());
        }
        product.setId(java.util.UUID.randomUUID().toString());
        product.setName(product.getName());
        product.setPrice(product.getPrice());
        product.setStockQuantity(product.getStockQuantity());
        return productRepository.save(product);
    }

    /**
     * Updates an existing product with validation (AC2).
     * Throws ProductNotFoundException if product doesn't exist.
     * Throws InvalidPriceException if price is negative.
     * Throws InvalidStockQuantityException if stock quantity is negative.
     */
    public Product updateProduct(Product product) {
        // Validate product id exists (AC2)
        if (product.getId() == null || product.getId().isEmpty()) {
            throw new ProductNotFoundException(product.getId());
        }
        Product existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new ProductNotFoundException(product.getId()));

        // Validate price if provided (AC2)
        if (product.getPrice() != null && product.getPrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new InvalidPriceException(product.getPrice());
        }
        // Validate stock quantity if provided (AC2)
        if (product.getStockQuantity() < 0) {
            throw new InvalidStockQuantityException(product.getStockQuantity());
        }

        Product updatedProduct = new Product(
                existingProduct.getId(),
                product.getName() != null ? product.getName() : existingProduct.getName(),
                product.getPrice() != null ? product.getPrice() : existingProduct.getPrice(),
                product.getStockQuantity() >= 0 ? product.getStockQuantity() : existingProduct.getStockQuantity()
        );

        return productRepository.save(updatedProduct);
    }

    /**
     * Retrieves and displays all products (AC2).
     */
    public List<Product> listProducts() {
        for (Product product : productRepository.findAll()) {
            System.out.println(product);
        }
        return productRepository.findAll();
    }

    /**
     * Deletes a product by id (AC2).
     * Throws ProductNotFoundException if product doesn't exist.
     */
    public void deleteProduct(String id) {
        // Validate product exists before deletion (AC2)
        if (id == null || id.isEmpty()) {
            throw new ProductNotFoundException(id);
        }
        if (productRepository.findById(id).isPresent()) {
            productRepository.delete(id);
            System.out.println("Product with ID " + id + " has been deleted.");
        } else {
            throw new ProductNotFoundException(id);
        }
    }

    /**
     * Reduces stock quantity when order is confirmed (AC4).
     * Throws InvalidStockQuantityException if quantity is negative or zero.
     * Throws ProductNotFoundException if product doesn't exist.
     * Throws InsufficientStockException if not enough stock available.
     */
    public void reduceStock(String productId, int quantity) {
        // Validate quantity is positive (AC4)
        if (quantity <= 0) {
            throw new InvalidStockQuantityException(quantity);
        }
        // Find product or throw ProductNotFoundException (AC4)
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        int newStock = product.getStockQuantity() - quantity;
        // Check if sufficient stock available (AC4)
        if (newStock < 0) {
            throw new InsufficientStockException(productId, quantity, product.getStockQuantity());
        }
        product.setStockQuantity(newStock);
        productRepository.save(product);
    }

    /**
     * Restores stock quantity when confirmed order is cancelled (AC5).
     * Throws InvalidStockQuantityException if quantity is negative or zero.
     * Throws ProductNotFoundException if product doesn't exist.
     */
    public void restoreStock(String productId, int quantity) {
        // Validate quantity is positive (AC5)
        if (quantity <= 0) {
            throw new InvalidStockQuantityException(quantity);
        }
        // Find product or throw ProductNotFoundException (AC5)
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ProductNotFoundException(productId));

        product.setStockQuantity(product.getStockQuantity() + quantity);
        productRepository.save(product);
    }

    /**
     * Finds all products with stock below the specified threshold.
     */
    public List<Product> findLowStockProducts(int threshold) {
        for (Product product : productRepository.findLowStock(threshold)) {
            System.out.println(product);
        }
        return productRepository.findLowStock(threshold);
    }
}
