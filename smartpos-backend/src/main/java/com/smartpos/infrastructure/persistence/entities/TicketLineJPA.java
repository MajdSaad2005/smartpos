package com.smartpos.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;

// Option A stabilization: prevent duplicate entity/table mapping with domain entity.
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketLineJPA {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;
    
    @Column(name = "product_id", nullable = false)
    private Long productId;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(name = "unit_price", nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;
    
    @Column(name = "tax_percentage", precision = 5, scale = 2)
    private BigDecimal taxPercentage;
    
    @Column(name = "line_total", nullable = false, precision = 19, scale = 2)
    private BigDecimal lineTotal;
    
    @Column(name = "tax_amount", precision = 19, scale = 2)
    private BigDecimal taxAmount;
}
