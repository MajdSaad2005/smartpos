package com.smartpos.domain.models;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * Pure Domain Model - CloseCash (Cash Register Session)
 * NO JPA annotations - independent of persistence framework
 */
public class CloseCashDomain {
    
    private Long id;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
    private BigDecimal totalSales;
    private BigDecimal totalReturns;
    private BigDecimal netAmount;
    private Boolean reconciled;
    
    public CloseCashDomain(Long id, LocalDateTime openedAt, LocalDateTime closedAt, 
                          BigDecimal totalSales, BigDecimal totalReturns, 
                          BigDecimal netAmount, Boolean reconciled) {
        this.id = id;
        this.openedAt = openedAt != null ? openedAt : LocalDateTime.now();
        this.closedAt = closedAt;
        this.totalSales = totalSales != null ? totalSales : BigDecimal.ZERO;
        this.totalReturns = totalReturns != null ? totalReturns : BigDecimal.ZERO;
        this.netAmount = netAmount != null ? netAmount : BigDecimal.ZERO;
        this.reconciled = reconciled != null ? reconciled : false;
    }
    
    /**
     * Domain logic: Open new cash session
     */
    public static CloseCashDomain openNewSession() {
        return new CloseCashDomain(null, LocalDateTime.now(), null, 
            BigDecimal.ZERO, BigDecimal.ZERO, BigDecimal.ZERO, false);
    }
    
    /**
     * Domain logic: Close the session
     */
    public void close() {
        if (this.closedAt != null) {
            throw new IllegalStateException("Session is already closed");
        }
        this.closedAt = LocalDateTime.now();
        recalculateNet();
    }
    
    /**
     * Domain logic: Reconcile the session
     */
    public void reconcile() {
        if (this.closedAt == null) {
            throw new IllegalStateException("Cannot reconcile an open session");
        }
        this.reconciled = true;
    }
    
    /**
     * Domain logic: Check if session is open
     */
    public boolean isOpen() {
        return this.closedAt == null;
    }
    
    /**
     * Domain logic: Check if session is closed
     */
    public boolean isClosed() {
        return this.closedAt != null;
    }
    
    /**
     * Domain logic: Check if session is reconciled
     */
    public boolean isReconciled() {
        return this.reconciled;
    }
    
    /**
     * Domain logic: Add sales amount
     */
    public void addSales(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Sales amount cannot be negative");
        }
        this.totalSales = this.totalSales.add(amount);
        recalculateNet();
    }
    
    /**
     * Domain logic: Add return amount
     */
    public void addReturn(BigDecimal amount) {
        if (amount == null || amount.compareTo(BigDecimal.ZERO) < 0) {
            throw new IllegalArgumentException("Return amount cannot be negative");
        }
        this.totalReturns = this.totalReturns.add(amount);
        recalculateNet();
    }
    
    /**
     * Domain logic: Recalculate net amount (sales - returns)
     */
    private void recalculateNet() {
        this.netAmount = this.totalSales.subtract(this.totalReturns)
            .setScale(2, java.math.RoundingMode.HALF_UP);
    }
    
    /**
     * Domain logic: Get session duration in minutes
     */
    public long getSessionDurationMinutes() {
        LocalDateTime endTime = this.closedAt != null ? this.closedAt : LocalDateTime.now();
        return java.time.temporal.ChronoUnit.MINUTES.between(this.openedAt, endTime);
    }
    
    // Getters
    public Long getId() {
        return id;
    }
    
    public LocalDateTime getOpenedAt() {
        return openedAt;
    }
    
    public LocalDateTime getClosedAt() {
        return closedAt;
    }
    
    public BigDecimal getTotalSales() {
        return totalSales;
    }
    
    public BigDecimal getTotalReturns() {
        return totalReturns;
    }
    
    public BigDecimal getNetAmount() {
        return netAmount;
    }
    
    public Boolean getReconciled() {
        return reconciled;
    }
}
