package com.ordersystem.repository;

import com.ordersystem.model.Customer;

import java.util.List;
import java.util.Optional;

/**
 * Data Access Layer contract for Customer persistence.
 * TODO: implement with CustomerRepositoryJdbc (see repository/impl)
 */
public interface CustomerRepository {

    Customer save(Customer customer);

    Optional<Customer> findById(String id);

    Optional<Customer> findByEmail(String email);

    List<Customer> findAll();

    void update(Customer customer);

    void delete(String id);
}
