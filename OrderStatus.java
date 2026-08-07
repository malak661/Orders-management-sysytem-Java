package model;

/**
 * All possible states of an Order, as defined by the project requirements.
 */
public enum OrderStatus {
    CREATED,
    CONFIRMED,
    PAID,
    PROCESSING,
    SHIPPED,
    DELIVERED,
    CANCELLED,
    REFUNDED
}
