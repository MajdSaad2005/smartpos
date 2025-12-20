package com.smartpos.domain.repositories;

import com.smartpos.application.dtos.DashboardStatsDTO;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.math.BigDecimal;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

/**
 * Repository for querying the v_dashboard_stats view
 * Demonstrates reading from SQL views
 */
@Repository
public class DashboardStatsRepository {
    
    private final JdbcTemplate jdbcTemplate;
    
    public DashboardStatsRepository(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }
    
    /**
     * Get aggregated stats for recent days
     */
    public List<DashboardStatsDTO> getRecentStats(int days) {
        String sql = """
            SELECT 
                sale_date,
                session_id,
                total_sales,
                total_returns,
                net_profit,
                transaction_count
            FROM v_dashboard_stats
            WHERE sale_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY)
            ORDER BY sale_date DESC
        """;
        
        return jdbcTemplate.query(sql,
            (rs, rowNum) -> DashboardStatsDTO.builder()
                .saleDate(rs.getDate("sale_date").toLocalDate())
                .sessionId(rs.getObject("session_id", Long.class))
                .totalSales(rs.getBigDecimal("total_sales"))
                .totalReturns(rs.getBigDecimal("total_returns"))
                .netProfit(rs.getBigDecimal("net_profit"))
                .transactionCount(rs.getInt("transaction_count"))
                .build(),
            days
        );
    }
    
    /**
     * Get stats for a specific cash session
     */
    public List<DashboardStatsDTO> getStatsBySession(Long sessionId) {
        String sql = """
            SELECT 
                sale_date,
                session_id,
                total_sales,
                total_returns,
                net_profit,
                transaction_count
            FROM v_dashboard_stats
            WHERE session_id = ?
            ORDER BY sale_date DESC
        """;
        
        return jdbcTemplate.query(sql,
            (rs, rowNum) -> DashboardStatsDTO.builder()
                .saleDate(rs.getDate("sale_date").toLocalDate())
                .sessionId(rs.getLong("session_id"))
                .totalSales(rs.getBigDecimal("total_sales"))
                .totalReturns(rs.getBigDecimal("total_returns"))
                .netProfit(rs.getBigDecimal("net_profit"))
                .transactionCount(rs.getInt("transaction_count"))
                .build(),
            sessionId
        );
    }
    
    /**
     * Get aggregated totals (sum across all days)
     */
    public DashboardStatsDTO getAggregatedStats(int days) {
        String sql = """
            SELECT 
                SUM(total_sales) as total_sales,
                SUM(total_returns) as total_returns,
                SUM(net_profit) as net_profit,
                SUM(transaction_count) as transaction_count
            FROM v_dashboard_stats
            WHERE sale_date >= DATE_SUB(CURDATE(), INTERVAL ? DAY)
        """;
        
        return jdbcTemplate.queryForObject(sql,
            (rs, rowNum) -> DashboardStatsDTO.builder()
                .saleDate(LocalDate.now())
                .sessionId(null)
                .totalSales(rs.getBigDecimal("total_sales"))
                .totalReturns(rs.getBigDecimal("total_returns"))
                .netProfit(rs.getBigDecimal("net_profit"))
                .transactionCount(rs.getInt("transaction_count"))
                .build(),
            days
        );
    }
}
