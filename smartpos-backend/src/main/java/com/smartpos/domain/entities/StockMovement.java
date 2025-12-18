package com.smartpos.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "stock_movements")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockMovement {
    
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
    
    // Positive for purchase/return, negative for sale
    @Column(nullable = false)
    private Integer quantityChange;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private MovementType type; // SALE, RETURN, ADJUSTMENT
    
    @Column(nullable = false)
    private Boolean hasQuantityChanged;
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false)
    private Boolean isDefective = false;
    
    public enum MovementType {
        SALE, RETURN, ADJUSTMENT
    }
}
