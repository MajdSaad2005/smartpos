package com.smartpos.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "close_cash")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CloseCash {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false)
    private LocalDateTime openedAt;
    
    @Column(nullable = false)
    private LocalDateTime closedAt;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalSales;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal totalReturns;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal netAmount;
    
    @Column(nullable = false)
    private Boolean reconciled = false;
    
    @OneToMany(mappedBy = "closeCash")
    private Set<Ticket> tickets = new HashSet<>();
}
