package com.smartpos.domain.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Pure Domain Model - Ticket (Sales/Return Transaction)
 * NO JPA annotations - independent of persistence framework
 */
public class TicketDomain {
    
    private Long id;
    private String number;
    private TicketType type;
    private LocalDateTime createdAt;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal total;
    private TicketStatus status;
    private Long customerId;
    private Long closeCashId;
    private List<TicketLineDomain> lines;
    
    public enum TicketType {
        SALE, RETURN
    }
    
    public enum TicketStatus {
        PENDING, COMPLETED, CANCELLED
    }
    
    public TicketDomain(Long id, String number, TicketType type, LocalDateTime createdAt, 
                       Long customerId, Long closeCashId) {
        validateTicket(number, type);
        this.id = id;
        this.number = number;
        this.type = type;
        this.createdAt = createdAt != null ? createdAt : LocalDateTime.now();
        this.customerId = customerId;
        this.closeCashId = closeCashId;
        this.status = TicketStatus.PENDING;
        this.subtotal = BigDecimal.ZERO;
        this.taxAmount = BigDecimal.ZERO;
        this.total = BigDecimal.ZERO;
        this.lines = new ArrayList<>();
    }
    
    /**
     * Domain logic: Validate ticket data
     */
    private void validateTicket(String number, TicketType type) {
        if (number == null || number.trim().isEmpty()) {
            throw new IllegalArgumentException("Ticket number cannot be empty");
        }
        if (type == null) {
            throw new IllegalArgumentException("Ticket type is required");
        }
    }
    
    /**
     * Domain logic: Add line to ticket and recalculate totals
     */
    public void addLine(TicketLineDomain line) {
        if (line == null) {
            throw new IllegalArgumentException("Line cannot be null");
        }
        if (this.status != TicketStatus.PENDING) {
            throw new IllegalStateException("Cannot add lines to non-pending tickets");
        }
        this.lines.add(line);
        recalculateTotals();
    }
    
    /**
     * Domain logic: Remove line from ticket
     */
    public void removeLine(TicketLineDomain line) {
        this.lines.remove(line);
        recalculateTotals();
    }
    
    /**
     * Domain logic: Recalculate totals based on lines
     */
    private void recalculateTotals() {
        this.subtotal = BigDecimal.ZERO;
        this.taxAmount = BigDecimal.ZERO;
        
        for (TicketLineDomain line : this.lines) {
            this.subtotal = this.subtotal.add(line.getLineTotal());
            this.taxAmount = this.taxAmount.add(line.getTaxAmount());
        }
        
        this.total = this.subtotal.add(this.taxAmount);
    }
    
    /**
     * Domain logic: Complete ticket (mark as completed)
     */
    public void complete() {
        if (this.lines.isEmpty()) {
            throw new IllegalStateException("Cannot complete ticket with no lines");
        }
        this.status = TicketStatus.COMPLETED;
    }
    
    /**
     * Domain logic: Cancel ticket
     */
    public void cancel() {
        if (this.status == TicketStatus.COMPLETED) {
            throw new IllegalStateException("Cannot cancel completed tickets");
        }
        this.status = TicketStatus.CANCELLED;
    }
    
    /**
     * Domain logic: Check if ticket is for return
     */
    public boolean isReturn() {
        return this.type == TicketType.RETURN;
    }
    
    /**
     * Domain logic: Check if ticket is for sale
     */
    public boolean isSale() {
        return this.type == TicketType.SALE;
    }
    
    /**
     * Domain logic: Check if ticket can be modified
     */
    public boolean canModify() {
        return this.status == TicketStatus.PENDING;
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public String getNumber() {
        return number;
    }
    
    public TicketType getType() {
        return type;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public BigDecimal getSubtotal() {
        return subtotal;
    }
    
    public BigDecimal getTaxAmount() {
        return taxAmount;
    }
    
    public BigDecimal getTotal() {
        return total;
    }
    
    public TicketStatus getStatus() {
        return status;
    }
    
    public Long getCustomerId() {
        return customerId;
    }
    
    public Long getCloseCashId() {
        return closeCashId;
    }
    
    public List<TicketLineDomain> getLines() {
        return new ArrayList<>(lines);
    }
    
    public int getLineCount() {
        return lines.size();
    }
}
