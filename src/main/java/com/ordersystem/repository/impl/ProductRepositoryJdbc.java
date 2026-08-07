package com.ordersystem.repository.impl;

import com.ordersystem.model.Product;
import com.ordersystem.repository.ProductRepository;
import com.ordersystem.util.DbConnection;

import java.util.List;
import java.util.Optional;

/**
 * SQLite/JDBC implementation of ProductRepository.
 * TODO: implement all methods using java.sql
 * TODO: create the "products" table via schema.sql (see resources/)
 */
public class ProductRepositoryJdbc implements ProductRepository {

    @Override
    public Product save(Product product) {
        // TODO
        return null;
    }

    @Override
    public Optional<Product> findById(String id) {
        // TODO
        return Optional.empty();
    }

    @Override
    public List<Product> findAll() {
        // TODO
        return null;
    }

    @Override
    public List<Product> findLowStock(int threshold) {
        // TODO
        return null;
    }

    @Override
    public void update(Product product) {
        // TODO
    }

    @Override
    public void delete(String id) {
        // TODO
    }
}
