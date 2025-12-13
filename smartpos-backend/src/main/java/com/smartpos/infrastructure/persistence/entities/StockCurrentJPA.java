package com.smartpos.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockCurrentJPA {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "product_id", nullable = false, unique = true)
    private Long productId;
    
    @Column(nullable = false)
    private Integer quantity;
}
