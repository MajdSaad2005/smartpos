package com.smartpos.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stock_levels")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockLevel {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    private Product product;
    
    @Column(nullable = false)
    private Integer minimumLevel;
    
    @Column(nullable = false)
    private Integer maximumLevel;
}
