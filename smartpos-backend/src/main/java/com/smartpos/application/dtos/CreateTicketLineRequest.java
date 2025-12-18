package com.smartpos.application.dtos;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTicketLineRequest {
    private Long productId;
    private Integer quantity;
    private Boolean isDefective; // For returns - mark product as defective
    private BigDecimal discountAmount; // Discount applied to this line
    private String couponCode; // Coupon code if applicable
}
