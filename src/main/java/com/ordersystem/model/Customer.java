package com.ordersystem.model;

import java.util.Objects;

public class Customer {

    private long id;
    private String name;
    private String email;
    private int phone;
    private String address;

    public Customer() {
    }

    public Customer(long id, String name, String email, int phone, String address) {
        this.id = id;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.address = address;
    }

    public long getId() { return id; }
    public void setId(long id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    public int getPhone() { return phone; }
    public void setPhone(int phone) { this.phone = phone; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }
/* 
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Customer)) return false;
        Customer customer = (Customer) o;
        return Objects.equals(id, customer.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "Customer{id='" + id + "', name='" + name + "', email='" + email + "'}";
    }
*/
}