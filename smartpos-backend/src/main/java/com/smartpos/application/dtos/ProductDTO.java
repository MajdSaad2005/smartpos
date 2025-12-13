package com.smartpos.application.dtos;

import lombok.*;

import java.math.BigDecimal;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductDTO {
    private Long id;
    private String code;
    private String name;
    private String description;
    private BigDecimal purchasePrice;
    private BigDecimal salePrice;
    private Boolean active;
    private BigDecimal taxPercentage;
    private Long supplierId;
    private String supplierName;
    private Integer currentStock;
}
