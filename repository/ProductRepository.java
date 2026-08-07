package repository;

import model.Product;
import java.util.List;

public interface ProductRepository {
    Product getProductById(int productId);
    void updateProductStock(int productId, int quantityInStock);
}
