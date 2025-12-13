package com.smartpos.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;

@Entity
@Table(name = "ticket_lines")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketLine {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ticket_id", nullable = false)
    private Ticket ticket;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal unitPrice;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal lineTotal;
    
    @Column(nullable = false, precision = 5, scale = 2)
    private BigDecimal taxPercentage;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal taxAmount;
}
