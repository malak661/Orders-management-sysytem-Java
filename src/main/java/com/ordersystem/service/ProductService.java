package com.ordersystem.service;

import com.ordersystem.model.Product;
import com.ordersystem.repository.ProductRepository;

import java.util.List;

/**
 * Business Logic Layer for Product & Inventory operations.
 * Responsibilities (AC1, AC2, AC4, AC5):
 * TODO: - create(Product) -> reject negative price/stock
 * TODO: - update(Product)
 * TODO: - search / list
 * TODO: - delete(String id)
 * TODO: - reduceStock(productId, quantity) -> used when order is confirmed
 * TODO: - restoreStock(productId, quantity) -> used when confirmed order is cancelled
 * TODO: - findLowStock(threshold) -> for reports
 */
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(Product product) {
        // TODO: validate price >= 0 and stock >= 0
        return null;
    }

    public Product updateProduct(Product product) {
        // TODO
        return null;
    }

    public List<Product> listProducts() {
        // TODO
        return null;
    }

    public void deleteProduct(String id) {
        // TODO
    }

    public void reduceStock(String productId, int quantity) {
        // TODO: throw InsufficientStockException if not enough stock
    }

    public void restoreStock(String productId, int quantity) {
        // TODO
    }

    public List<Product> findLowStockProducts(int threshold) {
        // TODO
        return null;
    }
}
