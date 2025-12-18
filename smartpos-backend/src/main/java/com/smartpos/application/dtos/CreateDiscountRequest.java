package com.smartpos.application.dtos;

import com.smartpos.domain.entities.Discount;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateDiscountRequest {
    private String name;
    private String description;
    private Discount.DiscountType discountType;
    private BigDecimal discountValue;
    private Discount.ApplicableOn applicableOn;
    private Long applicableProductId;
    private BigDecimal minimumPurchaseAmount;
    private BigDecimal maximumDiscountAmount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private Boolean active;
    private Boolean requiresCustomer;
}
