package com.smartpos.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "purchase_order_lines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PurchaseOrderLine {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne
    @JoinColumn(name = "purchase_order_id", nullable = false)
    private PurchaseOrder purchaseOrder;
    
    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(nullable = false)
    private Integer quantityOrdered;
    
    @Column(nullable = false)
    private Integer quantityReceived;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal unitCost;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal lineTotal;
    
    @Column(precision = 10, scale = 2)
    private BigDecimal taxPercentage;
    
    @Column(precision = 19, scale = 2)
    private BigDecimal taxAmount;
}
