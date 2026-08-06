package com.ordersystem.service;

import com.ordersystem.model.Customer;
import com.ordersystem.repository.CustomerRepository;

import java.util.List;

/**
 * Business Logic Layer for Customer operations.
 * Responsibilities (AC1, AC2):
 * TODO: - create(Customer) -> reject duplicate email (throw DuplicateEmailException)
 * TODO: - update(Customer)
 * TODO: - search(String keyword) -> by name/email
 * TODO: - list() -> all customers
 * TODO: - delete(String id)
 */
public class CustomerService {

    private final CustomerRepository customerRepository;

    public CustomerService(CustomerRepository customerRepository) {
        this.customerRepository = customerRepository;
    }

    public Customer createCustomer(Customer customer) {
        // TODO: validate unique email, then save
        return null;
    }

    public Customer updateCustomer(Customer customer) {
        // TODO
        return null;
    }

    public List<Customer> searchCustomers(String keyword) {
        // TODO
        return null;
    }

    public List<Customer> listCustomers() {
        return customerRepository.findAll();
    }

    public void deleteCustomer(String id) {
        // TODO
    }
}
