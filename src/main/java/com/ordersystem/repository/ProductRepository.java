package com.ordersystem.repository;

import com.ordersystem.model.Product;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Layer contract for Product persistence.
 * TODO: implement with ProductRepositoryJdbc (see repository/impl)
 */
public interface ProductRepository {

    Product save(Product product);

    Optional<Product> findById(String id);

    List<Product> findAll();
    Produ

    List<Product> findLowStock(int threshold);

    void update(Product product);

    void delete(String id);
}
