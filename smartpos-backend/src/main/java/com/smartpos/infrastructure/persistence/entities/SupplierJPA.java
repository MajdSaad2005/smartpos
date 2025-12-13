package com.smartpos.infrastructure.persistence.entities;

import jakarta.persistence.*;
import lombok.*;

// Option A stabilization: prevent duplicate entity/table mapping with domain entity.
@MappedSuperclass
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierJPA {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @Column(nullable = false, unique = true)
    private String code;
    
    @Column(nullable = false)
    private String name;
    
    @Column(unique = true)
    private String email;
    
    @Column(unique = true)
    private String phone;
    
    @Column(name = "tax_id", unique = true)
    private String taxId;
    
    @Column(columnDefinition = "TEXT")
    private String address;
    
    @Column(nullable = false)
    private Boolean active;
}
