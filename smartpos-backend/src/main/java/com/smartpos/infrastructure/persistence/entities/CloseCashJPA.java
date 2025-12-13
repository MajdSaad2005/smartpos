package com.smartpos.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

// Option A stabilization: do not register as an entity to avoid
// duplicate table/column mappings with domain JPA entities.
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CloseCashJPA {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(name = "opened_at", nullable = false)
    private LocalDateTime openedAt;
    
    @Column(name = "closed_at")
    private LocalDateTime closedAt;
    
    @Column(name = "total_sales", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalSales;
    
    @Column(name = "total_returns", nullable = false, precision = 19, scale = 2)
    private BigDecimal totalReturns;
    
    @Column(name = "net_amount", nullable = false, precision = 19, scale = 2)
    private BigDecimal netAmount;
    
    @Column(nullable = false)
    private Boolean reconciled;
}
