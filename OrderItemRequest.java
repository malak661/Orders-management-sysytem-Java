package model;

/**
 * Input DTO used by callers (e.g. the console menu) to request that a
 * product/quantity pair be added to a new order. This is intentionally NOT
 * the same class as {@link OrderItem}: OrderItem is the persisted line item
 * (it also stores the captured unit price and subtotal), while this class
 * only carries what the user typed in.
 */
public class OrderItemRequest {

    private final int productId;
    private final int quantity;

    public OrderItemRequest(int productId, int quantity) {
        this.productId = productId;
        this.quantity = quantity;
    }

    public int getProductId() {
        return productId;
    }

    public int getQuantity() {
        return quantity;
    }
}
