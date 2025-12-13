package com.smartpos.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

// Option A stabilization: prevent duplicate entity/table mapping with domain entity.
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketStockJPA {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "ticket_id", nullable = false)
    private Long ticketId;
    
    @Column(name = "product_id", nullable = false)
    private Long productId;
    
    @Column(nullable = false)
    private Integer quantity;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private StockMovementType type;
    
    public enum StockMovementType {
        INCREASE, DECREASE
    }
}
