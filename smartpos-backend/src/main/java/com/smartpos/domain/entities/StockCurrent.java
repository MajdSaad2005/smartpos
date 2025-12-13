package com.smartpos.domain.entities;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "stock_current", uniqueConstraints = @UniqueConstraint(columnNames = "product_id"))
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockCurrent {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false, unique = true)
    private Product product;
    
    @Column(nullable = false)
    private Integer quantity;
}
