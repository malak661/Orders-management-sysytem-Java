package com.ordersystem.repository.impl;

import com.ordersystem.model.Product;
import com.ordersystem.repository.ProductRepository;
import com.ordersystem.util.DbConnection;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;

/**
 * SQLite/JDBC implementation of ProductRepository.
 * TODO: implement all methods using java.sql
 * TODO: create the "products" table via schema.sql (see resources/)
 */
public class ProductRepositoryJdbc implements ProductRepository {
    private static final String SELECT_COLUMNS = "id, name, price, stock_quantity";

    private static final String INSERT_SQL =
            "INSERT INTO products (id, name, price, stock_quantity) VALUES (?, ?, ?, ?)";

    private static final String UPDATE_SQL =
            "UPDATE products SET name = ?, price = ?, stock_quantity = ? WHERE id = ?";

    private static final String FIND_BY_ID_SQL =
            "SELECT " + SELECT_COLUMNS + " FROM products WHERE id = ?";

    private static final String FIND_ALL_SQL =
            "SELECT " + SELECT_COLUMNS + " FROM products ORDER BY name";

    private static final String FIND_LOW_STOCK_SQL =
            "SELECT " + SELECT_COLUMNS + " FROM products WHERE stock_quantity <= ? ORDER BY stock_quantity ASC";

    private static final String DELETE_SQL =
            "DELETE FROM products WHERE id = ?";

    @Override
    public Product save(Product product) {
      requireValidProduct(product);

        try (Connection connection = DbConnection.getConnection()) {
            try (PreparedStatement updateStatement = connection.prepareStatement(UPDATE_SQL)) {
                bindProductValues(updateStatement, product, 1);
                updateStatement.setString(4, product.getId());
                if (updateStatement.executeUpdate() > 0) {
                    return product;
                }
            }
            try (PreparedStatement insertStatement = connection.prepareStatement(INSERT_SQL)) {
                insertStatement.setString(1, product.getId());
                bindProductValues(insertStatement, product, 2);
                insertStatement.executeUpdate();
            }
            return product;
        } catch (SQLException e) {
            throw new RuntimeException("Failed to save product with id: " + product.getId(), e);
        }
    }

    @Override
    public Optional<Product> findById(String id) {
        if (id == null || id.isEmpty()) {
            return Optional.empty();
        }
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_BY_ID_SQL)) {
            statement.setString(1, id);
            try (ResultSet resultSet = statement.executeQuery()) {
                return resultSet.next() ? Optional.of(mapRow(resultSet)) : Optional.empty();
            }
        } catch (SQLException e) {
            throw new RuntimeException("Failed to find product with id: " + id, e);
        }
    }

    @Override
    public List<Product> findAll() {
        try (Connection connection = DbConnection.getConnection();
             PreparedStatement statement = connection.prepareStatement(FIND_ALL_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            return mapRows(resultSet);
        } catch (SQLException e) {
            throw new RuntimeException("Failed to list all products", e);
        }
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
