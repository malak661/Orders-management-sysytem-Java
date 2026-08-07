package repository;

import model.OrderItem;
import java.util.List;

public interface OrderItemRepository {
    void addOrderItem(OrderItem item);
    void updateOrderItem(OrderItem item);
    void deleteOrderItem(int orderItemId);
    List<OrderItem> getOrderItemsByOrderId(int orderId);
}
