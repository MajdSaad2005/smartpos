package com.smartpos.application.services;

import com.smartpos.application.dtos.CustomerPurchaseSummaryDTO;
import com.smartpos.application.dtos.ProductSalesStatsDTO;
import com.smartpos.application.dtos.SalesReportDTO;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * Service for complex reporting queries demonstrating:
 * - Multi-table joins
 * - Nested queries
 * - Aggregate functions with GROUP BY
 * - Set operations (UNION)
 * 
 * This service uses native SQL queries to demonstrate advanced database operations
 * for academic purposes and performance optimization.
 */
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ReportingService {
    
    private final JdbcTemplate jdbcTemplate;
    
    /**
     * COMPLEX QUERY #1: Multi-table JOIN
     * Sales Report with Customer and Cash Session Information
     * 
     * Joins: tickets -> customers -> close_cash
     * Demonstrates: INNER JOIN, LEFT JOIN, date filtering
     */
    public List<SalesReportDTO> getSalesReport(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = """
            SELECT 
                t.id as ticketId,
                t.number as ticketNumber,
                t.created_at as createdAt,
                CONCAT(COALESCE(c.first_name, 'Walk-in'), ' ', COALESCE(c.last_name, 'Customer')) as customerName,
                t.total,
                cc.cashier_name as cashierName,
                COUNT(tl.id) as itemCount
            FROM tickets t
            LEFT JOIN customers c ON t.customer_id = c.id
            LEFT JOIN close_cash cc ON t.close_cash_id = cc.id
            INNER JOIN ticket_lines tl ON t.id = tl.ticket_id
            WHERE t.created_at BETWEEN ? AND ?
                AND t.status = 'COMPLETED'
                AND t.type = 'SALE'
            GROUP BY t.id, t.number, t.created_at, c.first_name, c.last_name, t.total, cc.cashier_name
            ORDER BY t.created_at DESC
        """;
        
        return jdbcTemplate.query(sql, 
            (rs, rowNum) -> SalesReportDTO.builder()
                .ticketId(rs.getLong("ticketId"))
                .ticketNumber(rs.getString("ticketNumber"))
                .createdAt(rs.getTimestamp("createdAt").toLocalDateTime())
                .customerName(rs.getString("customerName"))
                .total(rs.getBigDecimal("total"))
                .cashierName(rs.getString("cashierName"))
                .itemCount(rs.getInt("itemCount"))
                .build(),
            startDate, endDate
        );
    }
    
    /**
     * COMPLEX QUERY #2: Aggregate with GROUP BY
     * Product Sales Statistics with Multiple Aggregations
     * 
     * Demonstrates: SUM, COUNT, AVG, GROUP BY, HAVING
     * Joins: ticket_lines -> tickets -> products
     */
    public List<ProductSalesStatsDTO> getProductSalesStatistics(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = """
            SELECT 
                p.id as productId,
                p.code as productCode,
                p.name as productName,
                SUM(tl.quantity) as totalQuantitySold,
                SUM(tl.line_total) as totalRevenue,
                AVG(tl.unit_price) as averagePrice,
                COUNT(DISTINCT t.id) as transactionCount
            FROM products p
            INNER JOIN ticket_lines tl ON p.id = tl.product_id
            INNER JOIN tickets t ON tl.ticket_id = t.id
            WHERE t.created_at BETWEEN ? AND ?
                AND t.status = 'COMPLETED'
                AND t.type = 'SALE'
            GROUP BY p.id, p.code, p.name
            HAVING SUM(tl.quantity) > 0
            ORDER BY totalRevenue DESC
        """;
        
        return jdbcTemplate.query(sql,
            (rs, rowNum) -> ProductSalesStatsDTO.builder()
                .productId(rs.getLong("productId"))
                .productCode(rs.getString("productCode"))
                .productName(rs.getString("productName"))
                .totalQuantitySold(rs.getLong("totalQuantitySold"))
                .totalRevenue(rs.getBigDecimal("totalRevenue"))
                .averagePrice(rs.getBigDecimal("averagePrice"))
                .transactionCount(rs.getLong("transactionCount"))
                .build(),
            startDate, endDate
        );
    }
    
    /**
     * COMPLEX QUERY #3: Nested Query (Subquery)
     * Get Products with Below Average Sales
     * 
     * Demonstrates: Subquery in WHERE clause, comparison with aggregate result
     */
    public List<ProductSalesStatsDTO> getProductsBelowAverageSales(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = """
            SELECT 
                p.id as productId,
                p.code as productCode,
                p.name as productName,
                SUM(tl.quantity) as totalQuantitySold,
                SUM(tl.line_total) as totalRevenue,
                AVG(tl.unit_price) as averagePrice,
                COUNT(DISTINCT t.id) as transactionCount
            FROM products p
            INNER JOIN ticket_lines tl ON p.id = tl.product_id
            INNER JOIN tickets t ON tl.ticket_id = t.id
            WHERE t.created_at BETWEEN ? AND ?
                AND t.status = 'COMPLETED'
                AND t.type = 'SALE'
            GROUP BY p.id, p.code, p.name
            HAVING SUM(tl.line_total) < (
                SELECT AVG(product_revenue)
                FROM (
                    SELECT SUM(tl2.line_total) as product_revenue
                    FROM ticket_lines tl2
                    INNER JOIN tickets t2 ON tl2.ticket_id = t2.id
                    WHERE t2.created_at BETWEEN ? AND ?
                        AND t2.status = 'COMPLETED'
                        AND t2.type = 'SALE'
                    GROUP BY tl2.product_id
                ) as revenue_per_product
            )
            ORDER BY totalRevenue ASC
        """;
        
        return jdbcTemplate.query(sql,
            (rs, rowNum) -> ProductSalesStatsDTO.builder()
                .productId(rs.getLong("productId"))
                .productCode(rs.getString("productCode"))
                .productName(rs.getString("productName"))
                .totalQuantitySold(rs.getLong("totalQuantitySold"))
                .totalRevenue(rs.getBigDecimal("totalRevenue"))
                .averagePrice(rs.getBigDecimal("averagePrice"))
                .transactionCount(rs.getLong("transactionCount"))
                .build(),
            startDate, endDate, startDate, endDate
        );
    }
    
    /**
     * COMPLEX QUERY #4: Set Operation - UNION
     * Get All Active Entities (Customers, Suppliers, Products)
     * 
     * Demonstrates: UNION to combine results from different tables
     */
    public List<String> getAllActiveEntities() {
        String sql = """
            SELECT CONCAT('CUSTOMER-', code, ': ', first_name, ' ', last_name) as entity_name
            FROM customers
            WHERE active = true
            UNION
            SELECT CONCAT('SUPPLIER-', code, ': ', name) as entity_name
            FROM suppliers
            WHERE active = true
            UNION
            SELECT CONCAT('PRODUCT-', code, ': ', name) as entity_name
            FROM products
            WHERE active = true
            ORDER BY entity_name
        """;
        
        return jdbcTemplate.queryForList(sql, String.class);
    }
    
    /**
     * COMPLEX QUERY #5: Multi-table JOIN with Aggregation
     * Customer Purchase Summary with Total Spent
     * 
     * Demonstrates: Multiple JOINs, GROUP BY with multiple aggregates, COALESCE
     */
    public List<CustomerPurchaseSummaryDTO> getCustomerPurchaseSummary(LocalDateTime startDate, LocalDateTime endDate) {
        String sql = """
            SELECT 
                c.id as customerId,
                c.code as customerCode,
                CONCAT(c.first_name, ' ', c.last_name) as customerName,
                COUNT(DISTINCT t.id) as totalPurchases,
                COALESCE(SUM(t.total), 0) as totalSpent,
                COALESCE(AVG(t.total), 0) as averageTransactionValue
            FROM customers c
            LEFT JOIN tickets t ON c.id = t.customer_id 
                AND t.created_at BETWEEN ? AND ?
                AND t.status = 'COMPLETED'
                AND t.type = 'SALE'
            WHERE c.active = true
            GROUP BY c.id, c.code, c.first_name, c.last_name
            HAVING COUNT(DISTINCT t.id) > 0
            ORDER BY totalSpent DESC
        """;
        
        return jdbcTemplate.query(sql,
            (rs, rowNum) -> CustomerPurchaseSummaryDTO.builder()
                .customerId(rs.getLong("customerId"))
                .customerCode(rs.getString("customerCode"))
                .customerName(rs.getString("customerName"))
                .totalPurchases(rs.getLong("totalPurchases"))
                .totalSpent(rs.getBigDecimal("totalSpent"))
                .averageTransactionValue(rs.getBigDecimal("averageTransactionValue"))
                .build(),
            startDate, endDate
        );
    }
    
    /**
     * COMPLEX QUERY #6: Nested Query with EXISTS
     * Get Customers Who Made Purchases Above a Threshold
     * 
     * Demonstrates: Correlated subquery with EXISTS
     */
    public List<CustomerPurchaseSummaryDTO> getHighValueCustomers(BigDecimal threshold) {
        String sql = """
            SELECT 
                c.id as customerId,
                c.code as customerCode,
                CONCAT(c.first_name, ' ', c.last_name) as customerName,
                COUNT(DISTINCT t.id) as totalPurchases,
                SUM(t.total) as totalSpent,
                AVG(t.total) as averageTransactionValue
            FROM customers c
            INNER JOIN tickets t ON c.id = t.customer_id
            WHERE c.active = true
                AND t.status = 'COMPLETED'
                AND t.type = 'SALE'
                AND EXISTS (
                    SELECT 1
                    FROM tickets t2
                    WHERE t2.customer_id = c.id
                        AND t2.total >= ?
                        AND t2.status = 'COMPLETED'
                )
            GROUP BY c.id, c.code, c.first_name, c.last_name
            ORDER BY totalSpent DESC
        """;
        
        return jdbcTemplate.query(sql,
            (rs, rowNum) -> CustomerPurchaseSummaryDTO.builder()
                .customerId(rs.getLong("customerId"))
                .customerCode(rs.getString("customerCode"))
                .customerName(rs.getString("customerName"))
                .totalPurchases(rs.getLong("totalPurchases"))
                .totalSpent(rs.getBigDecimal("totalSpent"))
                .averageTransactionValue(rs.getBigDecimal("averageTransactionValue"))
                .build(),
            threshold
        );
    }
    
    /**
     * Get Low Stock Products (products below minimum level)
     * Demonstrates: Simple JOIN with comparison
     */
    public List<String> getLowStockProducts() {
        String sql = """
            SELECT CONCAT(p.code, ' - ', p.name, ' (Stock: ', sc.quantity, ', Min: ', sl.minimum_level, ')')
            FROM products p
            INNER JOIN stock_current sc ON p.id = sc.product_id
            INNER JOIN stock_levels sl ON p.id = sl.product_id
            WHERE sc.quantity < sl.minimum_level
                AND p.active = true
            ORDER BY sc.quantity ASC
        """;
        
        return jdbcTemplate.queryForList(sql, String.class);
    }
    
    /**
     * Demonstrates explicit SQL transaction control with BEGIN, COMMIT, and ROLLBACK
     * This method shows raw SQL transaction management for academic purposes.
     * 
     * In production, Spring's @Transactional is preferred, but this demonstrates
     * the underlying SQL transaction commands that Spring uses internally.
     * 
     * @param ticketId The ticket to recalculate totals for
     * @return true if successful, false if rolled back
     */
    public boolean recalculateTicketTotalsWithExplicitTransaction(Long ticketId) {
        try {
            // START TRANSACTION (BEGIN)
            jdbcTemplate.execute("START TRANSACTION");
            
            // Step 1: Calculate new subtotal from ticket lines
            String calculateSubtotalSql = """
                SELECT COALESCE(SUM(line_total), 0) as subtotal,
                       COALESCE(SUM(tax_amount), 0) as tax_total
                FROM ticket_lines
                WHERE ticket_id = ?
            """;
            
            var totals = jdbcTemplate.queryForMap(calculateSubtotalSql, ticketId);
            BigDecimal subtotal = (BigDecimal) totals.get("subtotal");
            BigDecimal taxTotal = (BigDecimal) totals.get("tax_total");
            BigDecimal total = subtotal.add(taxTotal);
            
            // Step 2: Update ticket with calculated totals
            String updateTicketSql = """
                UPDATE tickets 
                SET subtotal = ?,
                    tax_amount = ?,
                    total = ?
                WHERE id = ?
            """;
            
            int rowsUpdated = jdbcTemplate.update(updateTicketSql, subtotal, taxTotal, total, ticketId);
            
            if (rowsUpdated == 0) {
                // Ticket not found - ROLLBACK
                jdbcTemplate.execute("ROLLBACK");
                return false;
            }
            
            // COMMIT the transaction
            jdbcTemplate.execute("COMMIT");
            return true;
            
        } catch (Exception e) {
            // On any error - ROLLBACK
            try {
                jdbcTemplate.execute("ROLLBACK");
            } catch (Exception rollbackEx) {
                // Log rollback failure (in production would use logger)
                System.err.println("Rollback failed: " + rollbackEx.getMessage());
            }
            throw new RuntimeException("Transaction failed: " + e.getMessage(), e);
        }
    }
}
