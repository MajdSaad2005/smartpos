package com.smartpos.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "coupons")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Coupon {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true, length = 50)
    private String code;
    
    @Column(nullable = false, length = 255)
    private String description;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private DiscountType discountType; // PERCENTAGE, FIXED_AMOUNT
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal discountValue;
    
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
    
    @Column
    private Integer maxUsageCount;
    
    @Column(nullable = false)
    private Integer currentUsageCount = 0;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    public enum DiscountType {
        PERCENTAGE, FIXED_AMOUNT
    }
    
    public boolean isValid() {
        LocalDateTime now = LocalDateTime.now();
        return active 
            && now.isAfter(validFrom) 
            && now.isBefore(validUntil)
            && (maxUsageCount == null || currentUsageCount < maxUsageCount);
    }
    
    public BigDecimal calculateDiscount(BigDecimal amount) {
        if (!isValid()) {
            throw new IllegalStateException("Coupon is not valid");
        }
        
        if (minimumPurchaseAmount != null && amount.compareTo(minimumPurchaseAmount) < 0) {
            throw new IllegalStateException("Purchase amount does not meet minimum requirement");
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
