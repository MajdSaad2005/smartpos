package com.smartpos.domain.models;

/**
 * Pure Domain Model - NO JPA annotations
 * This represents the business logic for stock levels
 * Independent of any persistence framework
 */
public class StockLevelDomain {
    
    private Long id;
    private Long productId;
    private Integer minimumLevel;
    private Integer maximumLevel;
    
    public StockLevelDomain(Long id, Long productId, Integer minimumLevel, Integer maximumLevel) {
        validateLevels(minimumLevel, maximumLevel);
        this.id = id;
        this.productId = productId;
        this.minimumLevel = minimumLevel;
        this.maximumLevel = maximumLevel;
    }
    
    /**
     * Domain logic: Validate that minimum <= maximum
     */
    private void validateLevels(Integer minimumLevel, Integer maximumLevel) {
        if (minimumLevel < 0 || maximumLevel < 0) {
            throw new IllegalArgumentException("Stock levels cannot be negative");
        }
        if (minimumLevel > maximumLevel) {
            throw new IllegalArgumentException("Minimum level cannot exceed maximum level");
        }
    }
    
    /**
     * Domain logic: Check if current stock is below minimum
     */
    public boolean isBelowMinimum(Integer currentStock) {
        return currentStock < this.minimumLevel;
    }
    
    /**
     * Domain logic: Check if current stock exceeds maximum
     */
    public boolean exceedsMaximum(Integer currentStock) {
        return currentStock > this.maximumLevel;
    }
    
    /**
     * Domain logic: Calculate reorder quantity
     */
    public Integer calculateReorderQuantity(Integer currentStock) {
        if (exceedsMaximum(currentStock)) {
            return 0;
        }
        return this.maximumLevel - currentStock;
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public Integer getMinimumLevel() {
        return minimumLevel;
    }
    
    public Integer getMaximumLevel() {
        return maximumLevel;
    }
    
    // Setters for business operations
    public void updateLevels(Integer newMinimum, Integer newMaximum) {
        validateLevels(newMinimum, newMaximum);
        this.minimumLevel = newMinimum;
        this.maximumLevel = newMaximum;
    }
}
