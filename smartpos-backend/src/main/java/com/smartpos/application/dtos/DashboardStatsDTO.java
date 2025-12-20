package com.smartpos.application.dtos;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * DTO representing daily statistics from the v_dashboard_stats view
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DashboardStatsDTO {
    private LocalDate saleDate;
    private Long sessionId;
    private BigDecimal totalSales;
    private BigDecimal totalReturns;
    private BigDecimal netProfit;
    private Integer transactionCount;
    
    /**
     * Calculate net amount (sales - returns)
     */
    public BigDecimal getNetAmount() {
        BigDecimal sales = totalSales != null ? totalSales : BigDecimal.ZERO;
        BigDecimal returns = totalReturns != null ? totalReturns : BigDecimal.ZERO;
        return sales.subtract(returns);
    }
}
