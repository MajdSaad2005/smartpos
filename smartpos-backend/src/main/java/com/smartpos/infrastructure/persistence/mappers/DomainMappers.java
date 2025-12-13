package com.smartpos.infrastructure.persistence.mappers;

import com.smartpos.domain.models.*;
import com.smartpos.infrastructure.persistence.entities.*;

/**
 * Mappers to convert between Domain Models and JPA Entities
 */
public class DomainMappers {
    
    // ===== Product Mappers =====
    public static ProductDomain toDomain(ProductJPA jpa) {
        if (jpa == null) return null;
        return new ProductDomain(jpa.getId(), jpa.getCode(), jpa.getName(), 
            jpa.getDescription(), jpa.getPurchasePrice(), jpa.getSalePrice(), 
            jpa.getTaxPercentage(), jpa.getActive(), jpa.getSupplierId());
    }
    
    public static ProductJPA toProductJPA(ProductDomain domain) {
        if (domain == null) return null;
        return ProductJPA.builder()
            .id(domain.getId())
            .code(domain.getCode())
            .name(domain.getName())
            .description(domain.getDescription())
            .purchasePrice(domain.getPurchasePrice())
            .salePrice(domain.getSalePrice())
            .taxPercentage(domain.getTaxPercentage())
            .active(domain.getActive())
            .supplierId(domain.getSupplierId())
            .build();
    }
    
    // ===== Supplier Mappers =====
    public static SupplierDomain toDomain(SupplierJPA jpa) {
        if (jpa == null) return null;
        return new SupplierDomain(jpa.getId(), jpa.getCode(), jpa.getName(), 
            jpa.getEmail(), jpa.getPhone(), jpa.getTaxId(), jpa.getAddress(), jpa.getActive());
    }
    
    public static SupplierJPA toSupplierJPA(SupplierDomain domain) {
        if (domain == null) return null;
        return SupplierJPA.builder()
            .id(domain.getId())
            .code(domain.getCode())
            .name(domain.getName())
            .email(domain.getEmail())
            .phone(domain.getPhone())
            .taxId(domain.getTaxId())
            .address(domain.getAddress())
            .active(domain.getActive())
            .build();
    }
    
    // ===== Customer Mappers =====
    public static CustomerDomain toDomain(CustomerJPA jpa) {
        if (jpa == null) return null;
        return new CustomerDomain(jpa.getId(), jpa.getCode(), jpa.getFirstName(), 
            jpa.getLastName(), jpa.getEmail(), jpa.getPhone(), jpa.getTaxId(), 
            jpa.getAddress(), jpa.getActive());
    }
    
    public static CustomerJPA toCustomerJPA(CustomerDomain domain) {
        if (domain == null) return null;
        return CustomerJPA.builder()
            .id(domain.getId())
            .code(domain.getCode())
            .firstName(domain.getFirstName())
            .lastName(domain.getLastName())
            .email(domain.getEmail())
            .phone(domain.getPhone())
            .taxId(domain.getTaxId())
            .address(domain.getAddress())
            .active(domain.getActive())
            .build();
    }
    
    // ===== StockCurrent Mappers =====
    public static StockCurrentDomain toDomain(StockCurrentJPA jpa) {
        if (jpa == null) return null;
        return new StockCurrentDomain(jpa.getId(), jpa.getProductId(), jpa.getQuantity());
    }
    
    public static StockCurrentJPA toStockCurrentJPA(StockCurrentDomain domain) {
        if (domain == null) return null;
        return StockCurrentJPA.builder()
            .id(domain.getId())
            .productId(domain.getProductId())
            .quantity(domain.getQuantity())
            .build();
    }
    
    // ===== Ticket Mappers =====
    public static TicketDomain toDomain(TicketJPA jpa) {
        if (jpa == null) return null;
        TicketDomain domain = new TicketDomain(jpa.getId(), jpa.getNumber(), 
            TicketDomain.TicketType.valueOf(jpa.getType().name()), 
            jpa.getCreatedAt(), jpa.getCustomerId(), jpa.getCloseCashId());
        // Note: Status is initialized in constructor; if needed, extend TicketDomain with a setter.
        return domain;
    }
    
    public static TicketJPA toTicketJPA(TicketDomain domain) {
        if (domain == null) return null;
        return TicketJPA.builder()
            .id(domain.getId())
            .number(domain.getNumber())
            .type(TicketJPA.TicketType.valueOf(domain.getType().name()))
            .createdAt(domain.getCreatedAt())
            .subtotal(domain.getSubtotal())
            .taxAmount(domain.getTaxAmount())
            .total(domain.getTotal())
            .status(TicketJPA.TicketStatus.valueOf(domain.getStatus().name()))
            .customerId(domain.getCustomerId())
            .closeCashId(domain.getCloseCashId())
            .build();
    }
    
    // ===== TicketLine Mappers =====
    public static TicketLineDomain toDomain(TicketLineJPA jpa) {
        if (jpa == null) return null;
        return new TicketLineDomain(jpa.getId(), jpa.getTicketId(), jpa.getProductId(), 
            jpa.getQuantity(), jpa.getUnitPrice(), jpa.getTaxPercentage());
    }
    
    public static TicketLineJPA toTicketLineJPA(TicketLineDomain domain) {
        if (domain == null) return null;
        return TicketLineJPA.builder()
            .id(domain.getId())
            .ticketId(domain.getTicketId())
            .productId(domain.getProductId())
            .quantity(domain.getQuantity())
            .unitPrice(domain.getUnitPrice())
            .taxPercentage(domain.getTaxPercentage())
            .lineTotal(domain.getLineTotal())
            .taxAmount(domain.getTaxAmount())
            .build();
    }
    
    // ===== TicketStock Mappers =====
    public static TicketStockDomain toDomain(TicketStockJPA jpa) {
        if (jpa == null) return null;
        return new TicketStockDomain(jpa.getId(), jpa.getTicketId(), jpa.getProductId(), 
            jpa.getQuantity(), TicketStockDomain.StockMovementType.valueOf(jpa.getType().name()));
    }
    
    public static TicketStockJPA toTicketStockJPA(TicketStockDomain domain) {
        if (domain == null) return null;
        return TicketStockJPA.builder()
            .id(domain.getId())
            .ticketId(domain.getTicketId())
            .productId(domain.getProductId())
            .quantity(domain.getQuantity())
            .type(TicketStockJPA.StockMovementType.valueOf(domain.getType().name()))
            .build();
    }
    
    // ===== CloseCash Mappers =====
    public static CloseCashDomain toDomain(CloseCashJPA jpa) {
        if (jpa == null) return null;
        return new CloseCashDomain(jpa.getId(), jpa.getOpenedAt(), jpa.getClosedAt(), 
            jpa.getTotalSales(), jpa.getTotalReturns(), jpa.getNetAmount(), jpa.getReconciled());
    }
    
    public static CloseCashJPA toCloseCashJPA(CloseCashDomain domain) {
        if (domain == null) return null;
        return CloseCashJPA.builder()
            .id(domain.getId())
            .openedAt(domain.getOpenedAt())
            .closedAt(domain.getClosedAt())
            .totalSales(domain.getTotalSales())
            .totalReturns(domain.getTotalReturns())
            .netAmount(domain.getNetAmount())
            .reconciled(domain.getReconciled())
            .build();
    }
}
