package com.smartpos.application.dtos;

import com.smartpos.domain.entities.Coupon;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CouponResponse {
    private Long id;
    private String code;
    private String description;
    private Coupon.DiscountType discountType;
    private BigDecimal discountValue;
    private BigDecimal minimumPurchaseAmount;
    private BigDecimal maximumDiscountAmount;
    private LocalDateTime validFrom;
    private LocalDateTime validUntil;
    private Boolean active;
    private Integer maxUsageCount;
    private Integer currentUsageCount;
    private LocalDateTime createdAt;
    
    public static CouponResponse fromEntity(Coupon coupon) {
        return CouponResponse.builder()
                .id(coupon.getId())
                .code(coupon.getCode())
                .description(coupon.getDescription())
                .discountType(coupon.getDiscountType())
                .discountValue(coupon.getDiscountValue())
                .minimumPurchaseAmount(coupon.getMinimumPurchaseAmount())
                .maximumDiscountAmount(coupon.getMaximumDiscountAmount())
                .validFrom(coupon.getValidFrom())
                .validUntil(coupon.getValidUntil())
                .active(coupon.getActive())
                .maxUsageCount(coupon.getMaxUsageCount())
                .currentUsageCount(coupon.getCurrentUsageCount())
                .createdAt(coupon.getCreatedAt())
                .build();
    }
}
