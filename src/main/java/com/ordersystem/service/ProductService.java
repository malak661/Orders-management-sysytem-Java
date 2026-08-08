package com.ordersystem.service;

import com.ordersystem.model.Product;
import com.ordersystem.repository.ProductRepository;

import java.util.List;

/**
 * Business Logic Layer for Product & Inventory operations.
 * Responsibilities (AC1, AC2, AC4, AC5):
 * TODO: - create(Product) -> reject negative price/stock done
 * TODO: - update(Product) done
 * TODO: - search / list done 
 * TODO: - delete(String id) done
 * TODO: - reduceStock(productId, quantity) -> used when order is confirmed done
 * TODO: - restoreStock(productId, quantity) -> used when confirmed order is cancelled done
 * TODO: - findLowStock(threshold) -> for reports DONE
 */
public class ProductService {

    private final ProductRepository productRepository;

    public ProductService(ProductRepository productRepository) {
        this.productRepository = productRepository;
    }

    public Product createProduct(Product product) {
        // TODO: validate price >= 0 and stock >= 0 (DONE)
        if (product.getPrice().compareTo(java.math.BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Price cannot be negative");
        }
        if(product.getStockQuantity() < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
        product.setId(java.util.UUID.randomUUID().toString());
        product.setName(product.getName());
        product.setPrice(product.getPrice());
        product.setStockQuantity(product.getStockQuantity());
        return productRepository.save(product);
    }

    public Product updateProduct(Product product) {
        // TODO
        if (product.getId() == null || product.getId().isEmpty()) {
            throw new IllegalArgumentException("Product Not Found");
        }
        Product existingProduct = productRepository.findById(product.getId())
                .orElseThrow(() -> new IllegalArgumentException("Product Not Found"));
        Product updatedProduct = new Product(
                existingProduct.getId(),
                product.getName() != null ? product.getName() : existingProduct.getName(),
                product.getPrice() != null ? product.getPrice() : existingProduct.getPrice(),
                product.getStockQuantity() >= 0 ? product.getStockQuantity() : existingProduct.getStockQuantity()
        );

        return productRepository.save(updatedProduct);
    }

    public List<Product> listProducts() {
        // TODO (DONE)
        for (Product product : productRepository.findAll()) {
            System.out.println(product);
        }
        return productRepository.findAll();
    }

    public void deleteProduct(String id) {
        // TODO (DONE)
        if (id == null || id.isEmpty()) {
            throw new IllegalArgumentException("Product Not Found");
        }
        if(productRepository.findById(id).isPresent()) {
            productRepository.delete(id);
            System.out.println("Product with ID " + id + " has been deleted.");
        } else {
            throw new IllegalArgumentException("Product Not Found");
        }
    }

    public void reduceStock(String productId, int quantity) {
        // TODO: throw InsufficientStockException if not enough stock (DONE)
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product Not Found"));
        Product productReduced = ReduceProductStock(product, product.getStockQuantity() - quantity);

    }

    private Product ReduceProductStock(Product product, int i) {
        // (DONE)
        if (i < 0) {
            throw new IllegalArgumentException("Insufficient stock for product: " + product.getName());
        }
        product.setStockQuantity(i);
        return product;
    }
    public void restoreStock(String productId, int quantity) {
        //(DONE)
        if (quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new IllegalArgumentException("Product Not Found"));
        product.setStockQuantity(product.getStockQuantity() + quantity);
        productRepository.save(product);
    }

    public List<Product> findLowStockProducts(int threshold) {
        for (Product product : productRepository.findLowStock(threshold)) {
            System.out.println(product);
        }
        return productRepository.findLowStock(threshold);
    }
}
