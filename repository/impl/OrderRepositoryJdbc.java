package repository.impl;

import model.Order;
import model.OrderStatus;
import repository.OrderRepository;

import java.math.BigDecimal;
import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class OrderRepositoryJdbc implements OrderRepository {

    private final Connection connection;

    public OrderRepositoryJdbc(Connection connection) {
        this.connection = connection;
    }

    @Override
    public int createOrder(Order order) {
        String sql = "INSERT INTO orders (customer_id, order_date, status, subtotal, discount_percentage, discount_amount, tax_percentage, tax_amount, total_amount) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement stmt = connection.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            stmt.setInt(1, order.getCustomerId());
            stmt.setObject(2, order.getOrderDate());
            stmt.setString(3, order.getStatus().name());
            stmt.setBigDecimal(4, order.getSubtotal());
            stmt.setBigDecimal(5, order.getDiscountPercentage());
            stmt.setBigDecimal(6, order.getDiscountAmount());
            stmt.setBigDecimal(7, order.getTaxPercentage());
            stmt.setBigDecimal(8, order.getTaxAmount());
            stmt.setBigDecimal(9, order.getTotalAmount());
            
            stmt.executeUpdate();
            
            try (ResultSet rs = stmt.getGeneratedKeys()) {
                if (rs.next()) {
                    int id = rs.getInt(1);
                    order.setOrderId(id);
                    return id;
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error creating order", e);
        }
        return -1;
    }

    @Override
    public void updateOrder(Order order) {
        String sql = "UPDATE orders SET customer_id = ?, order_date = ?, status = ?, subtotal = ?, discount_percentage = ?, discount_amount = ?, tax_percentage = ?, tax_amount = ?, total_amount = ? WHERE order_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, order.getCustomerId());
            stmt.setObject(2, order.getOrderDate());
            stmt.setString(3, order.getStatus().name());
            stmt.setBigDecimal(4, order.getSubtotal());
            stmt.setBigDecimal(5, order.getDiscountPercentage());
            stmt.setBigDecimal(6, order.getDiscountAmount());
            stmt.setBigDecimal(7, order.getTaxPercentage());
            stmt.setBigDecimal(8, order.getTaxAmount());
            stmt.setBigDecimal(9, order.getTotalAmount());
            stmt.setInt(10, order.getOrderId());
            
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating order", e);
        }
    }

    @Override
    public void deleteOrder(int orderId) {
        String sql = "DELETE FROM orders WHERE order_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error deleting order", e);
        }
    }

    @Override
    public Order getOrderById(int orderId) {
        String sql = "SELECT * FROM orders WHERE order_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setInt(1, orderId);
            try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapRowToOrder(rs);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting order by id", e);
        }
        return null;
    }

    @Override
    public List<Order> getAllOrders() {
        List<Order> orders = new ArrayList<>();
        String sql = "SELECT * FROM orders";
        try (Statement stmt = connection.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                orders.add(mapRowToOrder(rs));
            }
        } catch (SQLException e) {
            throw new RuntimeException("Error getting all orders", e);
        }
        return orders;
    }

    @Override
    public void updateOrderStatus(int orderId, OrderStatus status) {
        String sql = "UPDATE orders SET status = ? WHERE order_id = ?";
        try (PreparedStatement stmt = connection.prepareStatement(sql)) {
            stmt.setString(1, status.name());
            stmt.setInt(2, orderId);
            stmt.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException("Error updating order status", e);
        }
    }

    private Order mapRowToOrder(ResultSet rs) throws SQLException {
        Order order = new Order();
        order.setOrderId(rs.getInt("order_id"));
        order.setCustomerId(rs.getInt("customer_id"));
        // Handle SQLite date strings or objects
        String dateStr = rs.getString("order_date");
        if (dateStr != null) {
            order.setOrderDate(LocalDateTime.parse(dateStr));
        }
        order.setStatus(OrderStatus.valueOf(rs.getString("status")));
        
        order.setSubtotal(rs.getBigDecimal("subtotal"));
        order.setDiscountPercentage(rs.getBigDecimal("discount_percentage"));
        order.setDiscountAmount(rs.getBigDecimal("discount_amount"));
        order.setTaxPercentage(rs.getBigDecimal("tax_percentage"));
        order.setTaxAmount(rs.getBigDecimal("tax_amount"));
        order.setTotalAmount(rs.getBigDecimal("total_amount"));
        
        return order;
    }
}
