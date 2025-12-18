package com.smartpos.application.dtos;

import lombok.*;

import java.math.BigDecimal;

/**
 * DTO for Product Sales Statistics with aggregation
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductSalesStatsDTO {
    private Long productId;
    private String productCode;
    private String productName;
    private Long totalQuantitySold;
    private BigDecimal totalRevenue;
    private BigDecimal averagePrice;
    private Long transactionCount;
}
