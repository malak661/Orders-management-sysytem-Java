package repository;

import model.Customer;
import java.util.List;

public interface CustomerRepository {
    Customer getCustomerById(int customerId);
}
