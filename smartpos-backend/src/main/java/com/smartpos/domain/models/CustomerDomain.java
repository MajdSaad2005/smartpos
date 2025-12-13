package com.smartpos.domain.models;

/**
 * Pure Domain Model - Customer
 * NO JPA annotations - independent of persistence framework
 */
public class CustomerDomain {
    
    private Long id;
    private String code;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String taxId;
    private String address;
    private Boolean active;
    
    public CustomerDomain(Long id, String code, String firstName, String lastName, 
                         String email, String phone, String taxId, String address, Boolean active) {
        validateCustomer(code, firstName, lastName, email);
        this.id = id;
        this.code = code;
        this.firstName = firstName;
        this.lastName = lastName;
        this.email = email;
        this.phone = phone;
        this.taxId = taxId;
        this.address = address;
        this.active = active != null ? active : true;
    }
    
    /**
     * Domain logic: Validate customer data
     */
    private void validateCustomer(String code, String firstName, String lastName, String email) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Customer code cannot be empty");
        }
        if (firstName == null || firstName.trim().isEmpty()) {
            throw new IllegalArgumentException("First name cannot be empty");
        }
        if (lastName == null || lastName.trim().isEmpty()) {
            throw new IllegalArgumentException("Last name cannot be empty");
        }
        if (email != null && !isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
    }
    
    /**
     * Domain logic: Email validation
     */
    private boolean isValidEmail(String email) {
        return email.matches("^[A-Za-z0-9+_.-]+@(.+)$");
    }
    
    /**
     * Domain logic: Get full name
     */
    public String getFullName() {
        return firstName + " " + lastName;
    }
    
    /**
     * Domain logic: Deactivate customer
     */
    public void deactivate() {
        this.active = false;
    }
    
    /**
     * Domain logic: Reactivate customer
     */
    public void activate() {
        this.active = true;
    }
    
    /**
     * Domain logic: Check if customer can make purchases
     */
    public boolean canMakePurchase() {
        return this.active;
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getFirstName() {
        return firstName;
    }
    
    public String getLastName() {
        return lastName;
    }
    
    public String getEmail() {
        return email;
    }
    
    public String getPhone() {
        return phone;
    }
    
    public String getTaxId() {
        return taxId;
    }
    
    public String getAddress() {
        return address;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    // Setters for business operations
    public void updateContactInfo(String email, String phone, String address) {
        if (email != null && !isValidEmail(email)) {
            throw new IllegalArgumentException("Invalid email format");
        }
        this.email = email;
        this.phone = phone;
        this.address = address;
    }
}
