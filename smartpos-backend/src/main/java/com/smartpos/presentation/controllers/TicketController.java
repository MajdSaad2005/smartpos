package com.smartpos.presentation.controllers;

import com.smartpos.application.dtos.TicketDTO;
import com.smartpos.application.dtos.CreateTicketRequest;
import com.smartpos.application.services.TicketService;
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
}
