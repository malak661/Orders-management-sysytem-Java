package com.ordersystem.repository.impl;

import com.ordersystem.model.Order;
import com.ordersystem.model.OrderStatus;
import com.ordersystem.repository.OrderRepository;
import com.ordersystem.util.DbConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

/**
 * SQLite/JDBC implementation of OrderRepository.
 */
public class OrderRepositoryJdbc implements OrderRepository {

    private static final DateTimeFormatter formatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME;

    @Override
    public Order save(Order order) {
        String sql = "INSERT INTO orders (id, customer_id, status, discount, tax, subtotal, total, created_at, updated_at) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, order.getId());
            pstmt.setString(2, order.getCustomerId());
            pstmt.setString(3, order.getStatus().name());
            pstmt.setString(4, order.getDiscount() != null ? order.getDiscount().toString() : null);
            pstmt.setString(5, order.getTax() != null ? order.getTax().toString() : null);
            pstmt.setString(6, order.getSubtotal() != null ? order.getSubtotal().toString() : null);
            pstmt.setString(7, order.getTotal() != null ? order.getTotal().toString() : null);
            pstmt.setString(8, order.getCreatedAt() != null ? order.getCreatedAt().format(formatter) : null);
            pstmt.setString(9, order.getUpdatedAt() != null ? order.getUpdatedAt().format(formatter) : null);
            
            pstmt.executeUpdate();
            return order;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error saving order", e);
        }
    }

    @Override
    public Optional<Order> findById(String id) {
        String sql = "SELECT * FROM orders WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, id);
            ResultSet rs = pstmt.executeQuery();
            
            if (rs.next()) {
                return Optional.of(mapRowToOrder(rs));
            }
            return Optional.empty();
            
        } catch (SQLException e) {
            throw new RuntimeException("Error finding order by ID", e);
        }
    }

    @Override
    public List<Order> findAll() {
        String sql = "SELECT * FROM orders";
        return fetchList(sql);
    }

    @Override
    public List<Order> findByCustomerId(String customerId) {
        String sql = "SELECT * FROM orders WHERE customer_id = ?";
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, customerId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                orders.add(mapRowToOrder(rs));
            }
            return orders;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error finding orders by customer ID", e);
        }
    }

    @Override
    public List<Order> findUnpaid() {
        String sql = "SELECT * FROM orders WHERE status = 'CONFIRMED'";
        return fetchList(sql);
    }

    @Override
    public void update(Order order) {
        String sql = "UPDATE orders SET status = ?, discount = ?, tax = ?, subtotal = ?, total = ?, updated_at = ? WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, order.getStatus().name());
            pstmt.setString(2, order.getDiscount() != null ? order.getDiscount().toString() : null);
            pstmt.setString(3, order.getTax() != null ? order.getTax().toString() : null);
            pstmt.setString(4, order.getSubtotal() != null ? order.getSubtotal().toString() : null);
            pstmt.setString(5, order.getTotal() != null ? order.getTotal().toString() : null);
            pstmt.setString(6, order.getUpdatedAt() != null ? order.getUpdatedAt().format(formatter) : null);
            pstmt.setString(7, order.getId());
            
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Error updating order", e);
        }
    }

    @Override
    public void delete(String id) {
        String sql = "DELETE FROM orders WHERE id = ?";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, id);
            pstmt.executeUpdate();
            
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting order", e);
        }
    }
    
    private List<Order> fetchList(String sql) {
        List<Order> orders = new ArrayList<>();
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
             
            while (rs.next()) {
                orders.add(mapRowToOrder(rs));
            }
            return orders;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error executing query", e);
        }
    }
    
    private Order mapRowToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setId(rs.getString("id"));
        order.setCustomerId(rs.getString("customer_id"));
        order.setStatus(OrderStatus.valueOf(rs.getString("status")));
        
        String discountStr = rs.getString("discount");
        if (discountStr != null) order.setDiscount(new BigDecimal(discountStr));
        
        String taxStr = rs.getString("tax");
        if (taxStr != null) order.setTax(new BigDecimal(taxStr));
        
        String subtotalStr = rs.getString("subtotal");
        if (subtotalStr != null) order.setSubtotal(new BigDecimal(subtotalStr));
        
        String totalStr = rs.getString("total");
        if (totalStr != null) order.setTotal(new BigDecimal(totalStr));
        
        String createdStr = rs.getString("created_at");
        if (createdStr != null) order.setCreatedAt(LocalDateTime.parse(createdStr, formatter));
        
        String updatedStr = rs.getString("updated_at");
        if (updatedStr != null) order.setUpdatedAt(LocalDateTime.parse(updatedStr, formatter));
        
        return order;
    }
}
