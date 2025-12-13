package com.smartpos.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

/**
 * JPA Persistence Entity - Stays in Infrastructure Layer
 * Maps to database table, separate from domain logic
 */
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockLevelJPA {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "product_id", nullable = false)
    private Long productId;
    
    @Column(nullable = false)
    private Integer minimumLevel;
    
    @Column(nullable = false)
    private Integer maximumLevel;
}
