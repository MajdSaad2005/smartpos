package com.smartpos.domain.models;

import java.math.BigDecimal;

/**
 * Pure Domain Model - TicketLine (Line Item in Ticket)
 * NO JPA annotations - independent of persistence framework
 */
public class TicketLineDomain {
    
    private Long id;
    private Long ticketId;
    private Long productId;
    private Integer quantity;
    private BigDecimal unitPrice;
    private BigDecimal taxPercentage;
    private BigDecimal lineTotal;
    private BigDecimal taxAmount;
    
    public TicketLineDomain(Long id, Long ticketId, Long productId, Integer quantity, 
                           BigDecimal unitPrice, BigDecimal taxPercentage) {
        validateLine(quantity, unitPrice, taxPercentage);
        this.id = id;
        this.ticketId = ticketId;
        this.productId = productId;
        this.quantity = quantity;
        this.unitPrice = unitPrice;
        this.taxPercentage = taxPercentage;
        this.lineTotal = calculateLineTotal();
        this.taxAmount = calculateTaxAmount();
    }
    
    /**
     * Domain logic: Validate line data
     */
    private void validateLine(Integer quantity, BigDecimal unitPrice, BigDecimal taxPercentage) {
        if (quantity == null || quantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        if (unitPrice == null || unitPrice.compareTo(BigDecimal.ZERO) <= 0) {
            throw new IllegalArgumentException("Unit price must be greater than zero");
        }
        if (taxPercentage == null || taxPercentage.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Tax percentage cannot be negative");
        }
    }
    
    /**
     * Domain logic: Calculate line total (quantity × price, excluding tax)
     */
    private BigDecimal calculateLineTotal() {
        return unitPrice.multiply(new BigDecimal(quantity))
            .setScale(2, java.math.RoundingMode.HALF_UP);
    }
    
    /**
     * Domain logic: Calculate tax amount for this line
     */
    private BigDecimal calculateTaxAmount() {
        if (taxPercentage.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal baseTax = this.lineTotal.multiply(taxPercentage)
            .divide(new BigDecimal(100), 4, java.math.RoundingMode.HALF_UP);
        return baseTax.setScale(2, java.math.RoundingMode.HALF_UP);
    }
    
    /**
     * Domain logic: Get total with tax included
     */
    public BigDecimal getTotalWithTax() {
        return lineTotal.add(taxAmount);
    }
    
    /**
     * Domain logic: Update quantity and recalculate
     */
    public void updateQuantity(Integer newQuantity) {
        if (newQuantity == null || newQuantity <= 0) {
            throw new IllegalArgumentException("Quantity must be greater than zero");
        }
        this.quantity = newQuantity;
        this.lineTotal = calculateLineTotal();
        this.taxAmount = calculateTaxAmount();
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
    
    public BigDecimal getUnitPrice() {
        return unitPrice;
    }
    
    public BigDecimal getTaxPercentage() {
        return taxPercentage;
    }
    
    public BigDecimal getLineTotal() {
        return lineTotal;
    }
    
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }
}
