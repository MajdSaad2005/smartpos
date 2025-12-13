package com.smartpos.domain.entities;

import jakarta.persistence.*;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "tickets")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Ticket {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String number;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketType type; // SALE, RETURN
    
    @Column(nullable = false)
    private LocalDateTime createdAt;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal subtotal;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal taxAmount;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private BigDecimal total;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketStatus status; // PENDING, COMPLETED, CANCELLED
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "customer_id")
    private Customer customer;
    
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "close_cash_id")
    private CloseCash closeCash;
    
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TicketLine> lines = new HashSet<>();
    
    @OneToMany(mappedBy = "ticket", cascade = CascadeType.ALL, orphanRemoval = true)
    private Set<TicketStock> stocks = new HashSet<>();
    
    public enum TicketType {
        SALE, RETURN
    }
    
    public enum TicketStatus {
        PENDING, COMPLETED, CANCELLED
    }
}
