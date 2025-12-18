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
public class DiscountResponse {
    private Long id;
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
    private LocalDateTime createdAt;
    
    public static DiscountResponse fromEntity(Discount discount) {
        return DiscountResponse.builder()
                .id(discount.getId())
                .name(discount.getName())
                .description(discount.getDescription())
                .discountType(discount.getDiscountType())
                .discountValue(discount.getDiscountValue())
                .applicableOn(discount.getApplicableOn())
                .applicableProductId(discount.getApplicableProductId())
                .minimumPurchaseAmount(discount.getMinimumPurchaseAmount())
                .maximumDiscountAmount(discount.getMaximumDiscountAmount())
                .validFrom(discount.getValidFrom())
                .validUntil(discount.getValidUntil())
                .active(discount.getActive())
                .requiresCustomer(discount.getRequiresCustomer())
                .createdAt(discount.getCreatedAt())
                .build();
    }
}
