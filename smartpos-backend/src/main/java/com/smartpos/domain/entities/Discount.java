package com.smartpos.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "discounts")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Discount {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, length = 100)
    private String name;
    
    @Column(length = 255)
    private String description;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DiscountType discountType; // PERCENTAGE, FIXED_AMOUNT
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal discountValue;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private ApplicableOn applicableOn; // TOTAL, PRODUCT_CATEGORY, SPECIFIC_PRODUCT
    
    @Column
    private Long applicableProductId;
    
    @Column(precision = 19, scale = 2)
    private BigDecimal minimumPurchaseAmount;
    
    @Column(precision = 19, scale = 2)
    private BigDecimal maximumDiscountAmount;
    
    @Column(nullable = false)
    private LocalDateTime validFrom;
    
    @Column(nullable = false)
    private LocalDateTime validUntil;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @Column(nullable = false)
    private Boolean requiresCustomer = false;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    public enum DiscountType {
        PERCENTAGE, FIXED_AMOUNT
    }
    
    public enum ApplicableOn {
        TOTAL, PRODUCT_CATEGORY, SPECIFIC_PRODUCT
    }
    
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return active && now.isAfter(validFrom) && now.isBefore(validUntil);
    }
    
    public BigDecimal calculateDiscount(BigDecimal amount) {
        if (!isValid()) {
            return BigDecimal.ZERO;
        }
        
        if (minimumPurchaseAmount != null && amount.compareTo(minimumPurchaseAmount) < 0) {
            return BigDecimal.ZERO;
        }
        
        BigDecimal discount;
        if (discountType == DiscountType.PERCENTAGE) {
            discount = amount.multiply(discountValue).divide(BigDecimal.valueOf(100), 2, java.math.RoundingMode.HALF_UP);
        } else {
            discount = discountValue;
        }
        
        if (maximumDiscountAmount != null && discount.compareTo(maximumDiscountAmount) > 0) {
            discount = maximumDiscountAmount;
        }
        
        return discount;
    }
}
