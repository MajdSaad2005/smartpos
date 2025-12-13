package com.smartpos.domain.models;

/**
 * Pure Domain Model - Supplier
 * NO JPA annotations - independent of persistence framework
 */
public class SupplierDomain {
    
    private Long id;
    private String code;
    private String name;
    private String email;
    private String phone;
    private String taxId;
    private String address;
    private Boolean active;
    
    public SupplierDomain(Long id, String code, String name, String email, 
                         String phone, String taxId, String address, Boolean active) {
        validateSupplier(code, name, email);
        this.id = id;
        this.code = code;
        this.name = name;
        this.email = email;
        this.phone = phone;
        this.taxId = taxId;
        this.address = address;
        this.active = active != null ? active : true;
    }
    
    /**
     * Domain logic: Validate supplier data
     */
    private void validateSupplier(String code, String name, String email) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Supplier code cannot be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Supplier name cannot be empty");
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
     * Domain logic: Deactivate supplier
     */
    public void deactivate() {
        this.active = false;
    }
    
    /**
     * Domain logic: Reactivate supplier
     */
    public void activate() {
        this.active = true;
    }
    
    /**
     * Domain logic: Check if supplier is available for ordering
     */
    public boolean isAvailable() {
        return this.active && this.email != null && !this.email.isEmpty();
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public String getCode() {
        return code;
    }
    
    public String getName() {
        return name;
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
