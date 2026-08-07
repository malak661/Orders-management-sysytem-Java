package model;

import java.math.BigDecimal;

/**
 * Minimal Product model needed for the Order module to compile and run on
 * its own.
 *
 * IMPORTANT (integration note): Product & Inventory Management is owned by a
 * different teammate. When this module is merged into the full project,
 * replace this class with the shared model.Product class (or make sure the
 * shared class has at least these fields) instead of keeping two versions.
 */
public class Product {

    private int productId;
    private String name;
    private String description;
    private BigDecimal price;
    private int quantityInStock;

    public Product() {
    }

    public Product(int productId, String name, String description, BigDecimal price, int quantityInStock) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.quantityInStock = quantityInStock;
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public BigDecimal getPrice() {
        return price;
    }

    public void setPrice(BigDecimal price) {
        this.price = price;
    }

    public int getQuantityInStock() {
        return quantityInStock;
    }

    public void setQuantityInStock(int quantityInStock) {
        this.quantityInStock = quantityInStock;
    }
}
