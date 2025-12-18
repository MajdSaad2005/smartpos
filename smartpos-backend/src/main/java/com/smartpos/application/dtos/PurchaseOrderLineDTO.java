package com.smartpos.application.dtos;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderLineDTO {
    private Long id;
    private Long productId;
    private String productName;
    private String productCode;
    private Integer quantityOrdered;
    private Integer quantityReceived;
    private BigDecimal unitCost;
    private BigDecimal lineTotal;
    private BigDecimal taxPercentage;
    private BigDecimal taxAmount;
}
