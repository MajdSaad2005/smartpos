package com.smartpos.domain.models;

/**
 * Pure Domain Model - StockCurrent
 * NO JPA annotations - independent of persistence framework
 */
public class StockCurrentDomain {
    
    private Long id;
    private Long productId;
    private Integer quantity;
    
    public StockCurrentDomain(Long id, Long productId, Integer quantity) {
        validateQuantity(quantity);
        this.id = id;
        this.productId = productId;
        this.quantity = quantity;
    }
    
    /**
     * Domain logic: Validate quantity
     */
    private void validateQuantity(Integer quantity) {
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Stock quantity cannot be negative");
        }
    }
    
    /**
     * Domain logic: Increase stock
     */
    public void increase(Integer amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount to increase must be positive");
        }
        this.quantity += amount;
    }
    
    /**
     * Domain logic: Decrease stock with validation
     */
    public void decrease(Integer amount) {
        if (amount == null || amount <= 0) {
            throw new IllegalArgumentException("Amount to decrease must be positive");
        }
        if (this.quantity < amount) {
            throw new IllegalArgumentException("Insufficient stock. Available: " + this.quantity + ", Requested: " + amount);
        }
        this.quantity -= amount;
    }
    
    /**
     * Domain logic: Check if stock is available
     */
    public boolean isAvailable(Integer requestedAmount) {
        return this.quantity >= requestedAmount;
    }
    
    /**
     * Domain logic: Check if stock is zero
     */
    public boolean isEmpty() {
        return this.quantity == 0;
    }
    
    /**
     * Domain logic: Reset stock
     */
    public void reset() {
        this.quantity = 0;
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    // Setters for business operations
    public void setQuantity(Integer newQuantity) {
        validateQuantity(newQuantity);
        this.quantity = newQuantity;
    }
}
