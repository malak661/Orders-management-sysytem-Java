package repository.impl;

import model.OrderItem;
import repository.OrderItemRepository;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class OrderItemRepositoryJdbc implements OrderItemRepository {

    private final Connection connection;

    public OrderItemRepositoryJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public void addOrderItem(OrderItem item) {
        String sql = "INSERT INTO order_items (order_id, product_id, quantity, unit_price, subtotal) VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, item.getOrderId());
            stmt.setInt(2, item.getProductId());
            stmt.setInt(3, item.getQuantity());
            stmt.setBigDecimal(4, item.getUnitPrice());
            stmt.setBigDecimal(5, item.getSubtotal());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    item.setOrderItemId(rs.getInt(1));
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error adding order item", e);
        }
    }

    @Override
    public void updateOrderItem(OrderItem item) {
        String sql = "UPDATE order_items SET order_id = ?, product_id = ?, quantity = ?, unit_price = ?, subtotal = ? WHERE order_item_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, item.getOrderId());
            stmt.setInt(2, item.getProductId());
            stmt.setInt(3, item.getQuantity());
            stmt.setBigDecimal(4, item.getUnitPrice());
            stmt.setBigDecimal(5, item.getSubtotal());
            stmt.setInt(6, item.getOrderItemId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating order item", e);
        }
    }

    @Override
    public void deleteOrderItem(int orderItemId) {
        String sql = "DELETE FROM order_items WHERE order_item_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderItemId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting order item", e);
        }
    }

    @Override
    public List<OrderItem> getOrderItemsByOrderId(int orderId) {
        List<OrderItem> items = new ArrayList<>();
        String sql = "SELECT * FROM order_items WHERE order_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    OrderItem item = new OrderItem();
                    item.setOrderItemId(rs.getInt("order_item_id"));
                    item.setOrderId(rs.getInt("order_id"));
                    item.setProductId(rs.getInt("product_id"));
                    item.setQuantity(rs.getInt("quantity"));
                    item.setUnitPrice(rs.getBigDecimal("unit_price"));
                    item.setSubtotal(rs.getBigDecimal("subtotal"));
                    items.add(item);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting order items by order id", e);
        }
        return items;
    }
}
