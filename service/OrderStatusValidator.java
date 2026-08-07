package service;

import model.OrderStatus;

public class OrderStatusValidator {

    public boolean isValidTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        if (currentStatus == null || newStatus == null) {
            return false;
        }
        
        switch (currentStatus) {
            case CREATED:
                return newStatus == OrderStatus.CONFIRMED || newStatus == OrderStatus.CANCELLED;
            case CONFIRMED:
                return newStatus == OrderStatus.PAID || newStatus == OrderStatus.CANCELLED;
            case PAID:
                return newStatus == OrderStatus.PROCESSING || newStatus == OrderStatus.CANCELLED || newStatus == OrderStatus.REFUNDED;
            case PROCESSING:
                return newStatus == OrderStatus.SHIPPED || newStatus == OrderStatus.CANCELLED || newStatus == OrderStatus.REFUNDED;
            case SHIPPED:
                return newStatus == OrderStatus.DELIVERED || newStatus == OrderStatus.REFUNDED;
            case DELIVERED:
                return newStatus == OrderStatus.REFUNDED;
            case CANCELLED:
            case REFUNDED:
                // Terminal states
                return false;
            default:
                return false;
        }
    }

    public void validateTransition(OrderStatus currentStatus, OrderStatus newStatus) {
        if (!isValidTransition(currentStatus, newStatus)) {
            throw new IllegalStateException(
                "Invalid status transition from " + currentStatus + " to " + newStatus);
        }
    }
}
