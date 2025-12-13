package com.smartpos.application.dtos;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateProductRequest {
    private String code;
    private String name;
    private String description;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;
    private BigDecimal taxPercentage;
    private Long supplierId;
}
