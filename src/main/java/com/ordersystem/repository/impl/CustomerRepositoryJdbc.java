package com.ordersystem.repository.impl;

import com.ordersystem.model.Customer;
import com.ordersystem.repository.CustomerRepository;
import com.ordersystem.util.DbConnection;

import java.util.List;
import java.util.Optional;

/**
 * SQLite/JDBC implementation of CustomerRepository.
 * TODO: implement all methods using java.sql (PreparedStatement, ResultSet)
 * TODO: create the "customers" table via schema.sql (see resources/)
 */
public class CustomerRepositoryJdbc implements CustomerRepository {

    @Override
    public Customer save(Customer customer) {
        // TODO
        return null;
    }

    @Override
    public Optional<Customer> findById(String id) {
        // TODO
        return Optional.empty();
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        // TODO
        return Optional.empty();
    }

    @Override
    public List<Customer> findAll() {
        // TODO
        return null;
    }

    @Override
    public void update(Customer customer) {
        // TODO
    }

    @Override
    public void delete(String id) {
        // TODO
    }
}
