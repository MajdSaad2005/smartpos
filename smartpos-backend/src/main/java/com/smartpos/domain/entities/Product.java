package com.smartpos.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "products")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Product {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String code;
    
    @Column(nullable = false)
    private String name;
    
    @Column(columnDefinition = "TEXT")
    private String description;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal purchasePrice;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal salePrice;
    
    @Column(nullable = false)
    private Boolean active = true;
    
    @Column(name = "tax_percentage", precision = 5, scale = 2)
    private BigDecimal taxPercentage;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "supplier_id", nullable = false)
    private Supplier supplier;
    
    @OneToOne(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private StockCurrent stockCurrent;
    
    @OneToMany(mappedBy = "product", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<StockLevel> stockLevels = new HashSet<>();
    
    @OneToMany(mappedBy = "product")
    private Set<TicketLine> ticketLines = new HashSet<>();
    
    @OneToMany(mappedBy = "product")
    private Set<TicketStock> ticketStocks = new HashSet<>();
}
