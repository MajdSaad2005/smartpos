package com.smartpos.domain.models;

import java.math.BigDecimal;

/**
 * Pure Domain Model - Product
 * NO JPA annotations - independent of persistence framework
 */
public class ProductDomain {
    
    private Long id;
    private String code;
    private String name;
    private String description;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;
    private BigDecimal taxPercentage;
    private Boolean active;
    private Long supplierId;
    
    public ProductDomain(Long id, String code, String name, String description, 
                        BigDecimal purchasePrice, BigDecimal salePrice, 
                        BigDecimal taxPercentage, Boolean active, Long supplierId) {
        validateProduct(code, name, purchasePrice, salePrice);
        this.id = id;
        this.code = code;
        this.name = name;
        this.description = description;
        this.purchasePrice = purchasePrice;
        this.salePrice = salePrice;
        this.taxPercentage = taxPercentage != null ? taxPercentage : BigDecimal.ZERO;
        this.active = active != null ? active : true;
        this.supplierId = supplierId;
    }
    
    /**
     * Domain logic: Validate product data
     */
    private void validateProduct(String code, String name, BigDecimal purchasePrice, BigDecimal salePrice) {
        if (code == null || code.trim().isEmpty()) {
            throw new IllegalArgumentException("Product code cannot be empty");
        }
        if (name == null || name.trim().isEmpty()) {
            throw new IllegalArgumentException("Product name cannot be empty");
        }
        if (purchasePrice == null || purchasePrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Purchase price cannot be negative");
        }
        if (salePrice == null || salePrice.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Sale price cannot be negative");
        }
        if (salePrice.compareTo(purchasePrice) < 0) {
            throw new IllegalArgumentException("Sale price cannot be less than purchase price");
        }
    }
    
    /**
     * Domain logic: Calculate profit margin
     */
    public BigDecimal calculateMargin() {
        if (purchasePrice.compareTo(BigDecimal.ZERO) == 0) {
            return BigDecimal.ZERO;
        }
        BigDecimal profit = salePrice.subtract(purchasePrice);
        return profit.divide(purchasePrice, 2, java.math.RoundingMode.HALF_UP);
    }
    
    /**
     * Domain logic: Calculate sale price with tax
     */
    public BigDecimal calculatePriceWithTax() {
        BigDecimal taxMultiplier = BigDecimal.ONE.add(taxPercentage.divide(new BigDecimal(100), 4, java.math.RoundingMode.HALF_UP));
        return salePrice.multiply(taxMultiplier).setScale(2, java.math.RoundingMode.HALF_UP);
    }
    
    /**
     * Domain logic: Calculate tax amount for given price
     */
    public BigDecimal calculateTaxAmount(BigDecimal basePrice) {
        BigDecimal taxAmount = basePrice.multiply(taxPercentage).divide(new BigDecimal(100), 2, java.math.RoundingMode.HALF_UP);
        return taxAmount;
    }
    
    /**
     * Domain logic: Deactivate product
     */
    public void deactivate() {
        this.active = false;
    }
    
    /**
     * Domain logic: Reactivate product
     */
    public void activate() {
        this.active = true;
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
    
    public String getDescription() {
        return description;
    }
    
    public BigDecimal getPurchasePrice() {
        return purchasePrice;
    }
    
    public BigDecimal getSalePrice() {
        return salePrice;
    }
    
    public BigDecimal getTaxPercentage() {
        return taxPercentage;
    }
    
    public Boolean getActive() {
        return active;
    }
    
    public Long getSupplierId() {
        return supplierId;
    }
    
    // Setters for business operations
    public void updatePrices(BigDecimal newPurchasePrice, BigDecimal newSalePrice) {
        validateProduct(this.code, this.name, newPurchasePrice, newSalePrice);
        this.purchasePrice = newPurchasePrice;
        this.salePrice = newSalePrice;
    }
    
    public void updateTaxPercentage(BigDecimal newTaxPercentage) {
        if (newTaxPercentage.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Tax percentage cannot be negative");
        }
        this.taxPercentage = newTaxPercentage;
    }
}
