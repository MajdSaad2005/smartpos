package com.smartpos.presentation.controllers;

import com.smartpos.application.dtos.CustomerPurchaseSummaryDTO;
import com.smartpos.application.dtos.ProductSalesStatsDTO;
import com.smartpos.application.dtos.SalesReportDTO;
import com.smartpos.application.services.ReportingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * REST Controller for Complex Reporting Queries
 * Demonstrates advanced database operations and query optimization
 */
@RestController
@RequestMapping("/v1/reports")
@RequiredArgsConstructor
@Tag(name = "Reports", description = "Advanced reporting and analytics endpoints")
public class ReportingController {
    
    private final ReportingService reportingService;
    
    /**
     * Get sales report with multi-table joins
     * Query Type: Multi-table JOIN (tickets, customers, close_cash, ticket_lines)
     */
    @GetMapping("/sales")
    @Operation(summary = "Get sales report with customer and cashier details")
    public ResponseEntity<List<SalesReportDTO>> getSalesReport(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(reportingService.getSalesReport(startDate, endDate));
    }
    
    /**
     * Get product sales statistics with aggregation
     * Query Type: Aggregate with GROUP BY, HAVING
     */
    @GetMapping("/product-stats")
    @Operation(summary = "Get product sales statistics with aggregations")
    public ResponseEntity<List<ProductSalesStatsDTO>> getProductStatistics(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(reportingService.getProductSalesStatistics(startDate, endDate));
    }
    
    /**
     * Get products with below average sales
     * Query Type: Nested Query (Subquery)
     */
    @GetMapping("/products-below-average")
    @Operation(summary = "Get products with below average sales performance")
    public ResponseEntity<List<ProductSalesStatsDTO>> getProductsBelowAverage(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(reportingService.getProductsBelowAverageSales(startDate, endDate));
    }
    
    /**
     * Get all active entities combined
     * Query Type: Set Operation (UNION)
     */
    @GetMapping("/active-entities")
    @Operation(summary = "Get all active entities using UNION")
    public ResponseEntity<List<String>> getAllActiveEntities() {
        return ResponseEntity.ok(reportingService.getAllActiveEntities());
    }
    
    /**
     * Get customer purchase summary
     * Query Type: Multi-table JOIN with GROUP BY
     */
    @GetMapping("/customer-summary")
    @Operation(summary = "Get customer purchase summary with spending details")
    public ResponseEntity<List<CustomerPurchaseSummaryDTO>> getCustomerSummary(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(reportingService.getCustomerPurchaseSummary(startDate, endDate));
    }
    
    /**
     * Get high-value customers
     * Query Type: Nested Query with EXISTS
     */
    @GetMapping("/high-value-customers")
    @Operation(summary = "Get customers with purchases above threshold")
    public ResponseEntity<List<CustomerPurchaseSummaryDTO>> getHighValueCustomers(
            @RequestParam BigDecimal threshold) {
        return ResponseEntity.ok(reportingService.getHighValueCustomers(threshold));
    }
    
    /**
     * Get low stock products
     * Query Type: Multi-table JOIN with comparison
     */
    @GetMapping("/low-stock")
    @Operation(summary = "Get products below minimum stock level")
    public ResponseEntity<List<String>> getLowStockProducts() {
        return ResponseEntity.ok(reportingService.getLowStockProducts());
    }
}
