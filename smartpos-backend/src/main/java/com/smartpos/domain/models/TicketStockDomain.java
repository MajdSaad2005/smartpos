package com.smartpos.domain.models;

import java.math.BigDecimal;

/**
 * Pure Domain Model - TicketStock (Stock Movement Audit Trail)
 * NO JPA annotations - independent of persistence framework
 */
public class TicketStockDomain {
    
    private Long id;
    private Long ticketId;
    private Long productId;
    private Integer quantity;
    private StockMovementType type;
    
    public enum StockMovementType {
        INCREASE, DECREASE
    }
    
    public TicketStockDomain(Long id, Long ticketId, Long productId, Integer quantity, 
                            StockMovementType type) {
        validateMovement(quantity, type);
        this.id = id;
        this.ticketId = ticketId;
        this.productId = productId;
        this.quantity = quantity;
        this.type = type;
    }
    
    /**
     * Domain logic: Validate stock movement
     */
    private void validateMovement(Integer quantity, StockMovementType type) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Movement quantity must be positive");
        }
        if (type == null) {
            throw new IllegalArgumentException("Movement type is required");
        }
    }
    
    /**
     * Domain logic: Check if movement is increase
     */
    public boolean isIncrease() {
        return this.type == StockMovementType.INCREASE;
    }
    
    /**
     * Domain logic: Check if movement is decrease
     */
    public boolean isDecrease() {
        return this.type == StockMovementType.DECREASE;
    }
    
    /**
     * Domain logic: Get signed quantity (positive for increase, negative for decrease)
     */
    public Integer getSignedQuantity() {
        return isIncrease() ? quantity : -quantity;
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public Long getTicketId() {
        return ticketId;
    }
    
    public Long getProductId() {
        return productId;
    }
    
    public Integer getQuantity() {
        return quantity;
    }
    
    public StockMovementType getType() {
        return type;
    }
}
