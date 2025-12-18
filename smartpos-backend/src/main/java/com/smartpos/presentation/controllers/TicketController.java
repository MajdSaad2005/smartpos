package com.smartpos.presentation.controllers;

import com.smartpos.application.dtos.TicketDTO;
import com.smartpos.application.dtos.CreateTicketRequest;
import com.smartpos.application.services.TicketService;
import com.smartpos.application.services.ReportingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/v1/tickets")
@RequiredArgsConstructor
@Tag(name = "Tickets", description = "Sales ticket management endpoints")
public class TicketController {
    
    private final TicketService ticketService;
    private final ReportingService reportingService;
    
    @PostMapping
    @Operation(summary = "Create a new ticket (sale or return)")
    public ResponseEntity<TicketDTO> createTicket(@RequestBody CreateTicketRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(ticketService.createTicket(request));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get ticket by ID")
    public ResponseEntity<TicketDTO> getTicketById(@PathVariable Long id) {
        return ResponseEntity.ok(ticketService.getTicketById(id));
    }
    
    @GetMapping("/number/{number}")
    @Operation(summary = "Get ticket by ticket number")
    public ResponseEntity<TicketDTO> getTicketByNumber(@PathVariable String number) {
        return ResponseEntity.ok(ticketService.getTicketByNumber(number));
    }
    
    @GetMapping("/customer/{customerId}")
    @Operation(summary = "Get all tickets for a customer")
    public ResponseEntity<List<TicketDTO>> getTicketsByCustomerId(@PathVariable Long customerId) {
        return ResponseEntity.ok(ticketService.getTicketsByCustomerId(customerId));
    }
    
    @GetMapping("/date-range")
    @Operation(summary = "Get tickets within a date range")
    public ResponseEntity<List<TicketDTO>> getTicketsByDateRange(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime startDate,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) LocalDateTime endDate) {
        return ResponseEntity.ok(ticketService.getTicketsByDateRange(startDate, endDate));
    }
    
    @GetMapping("/recent")
    @Operation(summary = "Get recent tickets")
    public ResponseEntity<List<TicketDTO>> getRecentTickets(@RequestParam(defaultValue = "10") int limit) {
        return ResponseEntity.ok(ticketService.getRecentTickets(limit));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Cancel a ticket")
    public ResponseEntity<Void> cancelTicket(@PathVariable Long id) {
        ticketService.cancelTicket(id);
        return ResponseEntity.noContent().build();
    }
    
    /**
     * Recalculate Ticket Totals
     * Demonstrates EXPLICIT SQL TRANSACTION CONTROL (BEGIN, COMMIT, ROLLBACK)
     * 
     * Use case: Fix data inconsistencies or recalculate after manual database updates
     * This endpoint uses raw SQL transaction commands instead of Spring's @Transactional
     */
    @PostMapping("/{id}/recalculate")
    @Operation(summary = "Recalculate ticket totals using explicit SQL transactions (BEGIN/COMMIT/ROLLBACK)")
    public ResponseEntity<String> recalculateTicket(@PathVariable Long id) {
        boolean success = reportingService.recalculateTicketTotalsWithExplicitTransaction(id);
        if (success) {
            return ResponseEntity.ok("Ticket totals recalculated successfully using explicit transaction control");
        } else {
            return ResponseEntity.status(HttpStatus.NOT_FOUND)
                    .body("Ticket not found - transaction was rolled back");
        }
    }
    
    /**
     * Bulk Inventory Adjustment Endpoint
     * Demonstrates TRANSACTIONAL operation with ACID properties
     * 
     * Request body example:
     * {
     *   "adjustments": {
     *     "1": 10,   // Add 10 units to product ID 1
     *     "2": -5    // Remove 5 units from product ID 2
     *   },
     *   "reason": "Physical inventory count correction"
     * }
     */
    @PostMapping("/bulk-adjustment")
    @Operation(summary = "Perform bulk inventory adjustment (Transactional)")
    public ResponseEntity<TicketDTO> bulkAdjustment(@RequestBody java.util.Map<String, Object> request) {
        @SuppressWarnings("unchecked")
        java.util.Map<String, Integer> adjustmentsRaw = (java.util.Map<String, Integer>) request.get("adjustments");
        String reason = (String) request.get("reason");
        
        // Convert String keys to Long
        java.util.Map<Long, Integer> adjustments = new java.util.HashMap<>();
        adjustmentsRaw.forEach((k, v) -> adjustments.put(Long.parseLong(k), v));
        
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(ticketService.performBulkInventoryAdjustment(adjustments, reason));
    }
}

