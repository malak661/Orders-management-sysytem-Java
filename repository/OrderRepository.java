package repository;

import model.Order;
import model.OrderStatus;
import java.util.List;

public interface OrderRepository {
    int createOrder(Order order);
    void updateOrder(Order order);
    void deleteOrder(int orderId);
    Order getOrderById(int orderId);
    List<Order> getAllOrders();
    void updateOrderStatus(int orderId, OrderStatus status);
}
