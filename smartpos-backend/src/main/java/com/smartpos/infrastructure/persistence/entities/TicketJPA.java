package com.smartpos.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketJPA {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String number;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketType type;
    
    @Column(nullable = false)
    private java.time.LocalDateTime createdAt;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private java.math.BigDecimal subtotal;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private java.math.BigDecimal taxAmount;
    
    @Column(nullable = false, precision = 19, scale = 2)
    private java.math.BigDecimal total;
    
    @Column(nullable = false)
    @Enumerated(EnumType.STRING)
    private TicketStatus status;
    
    @Column(name = "customer_id")
    private Long customerId;
    
    @Column(name = "close_cash_id")
    private Long closeCashId;
    
    public enum TicketType {
        SALE, RETURN
    }
    
    public enum TicketStatus {
        PENDING, COMPLETED, CANCELLED
    }
}
