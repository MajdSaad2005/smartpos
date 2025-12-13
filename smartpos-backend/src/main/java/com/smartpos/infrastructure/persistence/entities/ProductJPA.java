package com.smartpos.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ProductJPA {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String code;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal purchasePrice;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal salePrice;
    
    @Column(name = "tax_percentage", precision = 5, scale = 2)
    private BigDecimal taxPercentage;
    
    @Column(nullable = false)
    private Boolean active;
    
    @Column(name = "supplier_id", nullable = false)
    private Long supplierId;
}
