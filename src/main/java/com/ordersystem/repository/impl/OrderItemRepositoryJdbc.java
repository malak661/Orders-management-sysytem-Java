package com.ordersystem.repository.impl;

import com.ordersystem.model.OrderItem;
import com.ordersystem.repository.OrderItemRepository;
import com.ordersystem.util.DbConnection;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * SQLite/JDBC implementation of OrderItemRepository.
 */
public class OrderItemRepositoryJdbc implements OrderItemRepository {

    @Override
    public void saveAll(String orderId, List<OrderItem> items) {
        String sql = "INSERT INTO order_items (id, order_id, product_id, quantity, unit_price) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            for (OrderItem item : items) {
                pstmt.setString(1, item.getId());
                pstmt.setString(2, orderId);
                pstmt.setString(3, item.getProductId());
                pstmt.setInt(4, item.getQuantity());
                pstmt.setString(5, item.getUnitPriceAtOrderTime().toString());
                pstmt.addBatch();
            }
            pstmt.executeBatch();
            
        } catch (SQLException e) {
            throw new RuntimeException("Error saving order items", e);
        }
    }

    @Override
    public List<OrderItem> findByOrderId(String orderId) {
        String sql = "SELECT id, product_id, quantity, unit_price FROM order_items WHERE order_id = ?";
        List<OrderItem> items = new ArrayList<>();
        try (Connection conn = DbConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
             
            pstmt.setString(1, orderId);
            ResultSet rs = pstmt.executeQuery();
            
            while (rs.next()) {
                OrderItem item = new OrderItem();
                item.setId(rs.getString("id"));
                item.setProductId(rs.getString("product_id"));
                item.setQuantity(rs.getInt("quantity"));
                item.setUnitPriceAtOrderTime(new BigDecimal(rs.getString("unit_price")));
                items.add(item);
            }
            return items;
            
        } catch (SQLException e) {
            throw new RuntimeException("Error finding order items by order ID", e);
        }
    }
}
