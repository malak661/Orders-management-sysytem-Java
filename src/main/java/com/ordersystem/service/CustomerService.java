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
        if(customer == null || customer.getId() == null)
        {
            throw new IllegalArgumentException("Customer id must be provided for update");
        }
        if(customerRepository.findById(customer.getId()).isEmpty())
        {
            throw new IllegalArgumentException("Customer id " + customer.getId() + " not found");
        }
        return customerRepository.update(customer);
    }

    public List<Customer> searchCustomers(String keyword) {
        // TODO
        return null;
    }

    public List<Customer> listCustomers() {
        return customerRepository.findAll();
    }

    public void deleteCustomer(long id) {
        Customer customer = customerRepository.findById(id);
        if(customer == null)
        {
            throw new IllegalArgumentException("Customer with id " + id + " not found");
        }
        customerRepository.delete(customer);
    }
}
