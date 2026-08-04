package com.ordersystem.model;

import java.math.BigDecimal;
import java.util.Objects;

public class OrderItem {

    private String id;
    private String productId;
    private int quantity;
    private BigDecimal unitPriceAtOrderTime;

    public OrderItem() {
    }

    public OrderItem(String id, String productId, int quantity, BigDecimal unitPriceAtOrderTime) {
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPriceAtOrderTime = unitPriceAtOrderTime;
    }

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getProductId() { return productId; }
    public void setProductId(String productId) { this.productId = productId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public BigDecimal getUnitPriceAtOrderTime() { return unitPriceAtOrderTime; }
    public void setUnitPriceAtOrderTime(BigDecimal unitPriceAtOrderTime) { this.unitPriceAtOrderTime = unitPriceAtOrderTime; }

    public BigDecimal getLineTotal() {
        return unitPriceAtOrderTime.multiply(BigDecimal.valueOf(quantity));
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof OrderItem)) return false;
        OrderItem that = (OrderItem) o;
        return Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() { return Objects.hash(id); }

    @Override
    public String toString() {
        return "OrderItem{productId='" + productId + "', quantity=" + quantity + ", unitPrice=" + unitPriceAtOrderTime + "}";
    }
}