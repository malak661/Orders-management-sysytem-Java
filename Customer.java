package model;

/**
 * Minimal Customer model needed for the Order module to compile and run on
 * its own.
 *
 * IMPORTANT (integration note): Customer Management is owned by a different
 * teammate. When this module is merged into the full project, replace this
 * class with the shared model.Customer class (or make sure the shared class
 * has at least these fields) instead of keeping two versions.
 */
public class Customer {

    private int customerId;
    private String name;
    private String email;
    private String phoneNumber;
    private String address;

    public Customer() {
    }

    public Customer(int customerId, String name, String email, String phoneNumber, String address) {
        this.customerId = customerId;
        this.name = name;
        this.email = email;
        this.phoneNumber = phoneNumber;
        this.address = address;
    }

    public int getCustomerId() {
        return customerId;
    }

    public void setCustomerId(int customerId) {
        this.customerId = customerId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }
}
