package com.smartpos.application.dtos;

import lombok.*;

import java.math.BigDecimal;

/**
 * DTO for Customer Purchase Summary
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerPurchaseSummaryDTO {
    private Long customerId;
    private String customerCode;
    private String customerName;
    private Long totalPurchases;
    private BigDecimal totalSpent;
    private BigDecimal averageTransactionValue;
}
