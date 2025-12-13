package com.smartpos.application.services;

import com.smartpos.application.dtos.*;
import com.smartpos.domain.entities.*;
import com.smartpos.domain.repositories.*;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class TicketService {
    
    private final TicketRepository ticketRepository;
    private final TicketLineRepository ticketLineRepository;
    private final TicketStockRepository ticketStockRepository;
    private final ProductRepository productRepository;
    private final StockCurrentRepository stockCurrentRepository;
    private final CustomerRepository customerRepository;
    private final CloseCashRepository closeCashRepository;
    
    public TicketDTO createTicket(CreateTicketRequest request) {
        // Generate ticket number
        String ticketNumber = generateTicketNumber();
        
        // Validate products exist
        for (CreateTicketLineRequest lineRequest : request.getLines()) {
            productRepository.findById(lineRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + lineRequest.getProductId()));
        }
        
        Ticket ticket = Ticket.builder()
                .number(ticketNumber)
                .type(Ticket.TicketType.valueOf(request.getType()))
                .createdAt(LocalDateTime.now())
                .status(Ticket.TicketStatus.PENDING)
                .subtotal(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .build();
        
        if (request.getCustomerId() != null) {
            Customer customer = customerRepository.findById(request.getCustomerId())
                    .orElseThrow(() -> new RuntimeException("Customer not found"));
            ticket.setCustomer(customer);
        }
        
        // Associate with active cash session
        List<CloseCash> activeSessions = closeCashRepository.findByReconciledFalse();
        if (!activeSessions.isEmpty()) {
            // Find the first open (not closed) session
            activeSessions.stream()
                .filter(cs -> cs.getClosedAt() == null)
                .findFirst()
                .ifPresent(ticket::setCloseCash);
        }
        
        ticket = ticketRepository.save(ticket);
        
        // Add lines and calculate totals
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        
        for (CreateTicketLineRequest lineRequest : request.getLines()) {
                Product product = productRepository.findById(lineRequest.getProductId()).orElseThrow();
            
                // Use sale price for SALES, purchase price for RETURNS (inventory increases)
                BigDecimal unitPrice = ticket.getType() == Ticket.TicketType.SALE
                    ? product.getSalePrice()
                    : product.getPurchasePrice();
            BigDecimal lineSubtotal = unitPrice.multiply(BigDecimal.valueOf(lineRequest.getQuantity()));
            
            BigDecimal taxPercent = product.getTaxPercentage() != null ? product.getTaxPercentage() : BigDecimal.ZERO;
            BigDecimal lineTaxAmount = lineSubtotal.multiply(taxPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            
            TicketLine line = TicketLine.builder()
                    .ticket(ticket)
                    .product(product)
                    .quantity(lineRequest.getQuantity())
                    .unitPrice(unitPrice)
                    .lineTotal(lineSubtotal)
                    .taxPercentage(taxPercent)
                    .taxAmount(lineTaxAmount)
                    .build();
            
            ticketLineRepository.save(line);
            
            // Create stock movement
            TicketStock.StockMovementType movementType = ticket.getType() == Ticket.TicketType.SALE 
                    ? TicketStock.StockMovementType.DECREASE 
                    : TicketStock.StockMovementType.INCREASE;
            
            TicketStock stock = TicketStock.builder()
                    .ticket(ticket)
                    .product(product)
                    .quantity(lineRequest.getQuantity())
                    .type(movementType)
                    .createdAt(LocalDateTime.now())
                    .build();
            
            ticketStockRepository.save(stock);
            
            // Update stock current
            StockCurrent stockCurrent = stockCurrentRepository.findByProductId(product.getId())
                    .orElseThrow();
            
            if (movementType == TicketStock.StockMovementType.DECREASE) {
                stockCurrent.setQuantity(stockCurrent.getQuantity() - lineRequest.getQuantity());
            } else {
                stockCurrent.setQuantity(stockCurrent.getQuantity() + lineRequest.getQuantity());
            }
            
            stockCurrentRepository.save(stockCurrent);
            
            subtotal = subtotal.add(lineSubtotal);
            totalTax = totalTax.add(lineTaxAmount);
        }
        
        // Update ticket totals
        BigDecimal total = subtotal.add(totalTax);
        ticket.setSubtotal(subtotal);
        ticket.setTaxAmount(totalTax);
        ticket.setTotal(total);
        ticket.setStatus(Ticket.TicketStatus.COMPLETED);
        
        ticket = ticketRepository.save(ticket);
        return toDTO(ticket);
    }
    
    @Transactional(readOnly = true)
    public TicketDTO getTicketById(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        return toDTO(ticket);
    }
    
    @Transactional(readOnly = true)
    public TicketDTO getTicketByNumber(String number) {
        Ticket ticket = ticketRepository.findByNumber(number)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        return toDTO(ticket);
    }
    
    @Transactional(readOnly = true)
    public List<TicketDTO> getTicketsByCustomerId(Long customerId) {
        return ticketRepository.findByCustomerId(customerId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TicketDTO> getTicketsByDateRange(LocalDateTime startDate, LocalDateTime endDate) {
        return ticketRepository.findByDateRange(startDate, endDate).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<TicketDTO> getRecentTickets(int limit) {
        return ticketRepository.findAll().stream()
                .sorted(Comparator.comparing(Ticket::getCreatedAt).reversed())
                .limit(limit)
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public void cancelTicket(Long id) {
        Ticket ticket = ticketRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Ticket not found"));
        
        if (ticket.getStatus() == Ticket.TicketStatus.CANCELLED) {
            throw new RuntimeException("Ticket is already cancelled");
        }
        
        // Reverse stock movements
        List<TicketStock> stocks = ticketStockRepository.findByTicketId(id);
        for (TicketStock stock : stocks) {
            StockCurrent stockCurrent = stockCurrentRepository.findByProductId(stock.getProduct().getId())
                    .orElseThrow();
            
            if (stock.getType() == TicketStock.StockMovementType.DECREASE) {
                stockCurrent.setQuantity(stockCurrent.getQuantity() + stock.getQuantity());
            } else {
                stockCurrent.setQuantity(stockCurrent.getQuantity() - stock.getQuantity());
            }
            
            stockCurrentRepository.save(stockCurrent);
        }
        
        ticket.setStatus(Ticket.TicketStatus.CANCELLED);
        ticketRepository.save(ticket);
    }
    
    private String generateTicketNumber() {
        String timestamp = String.valueOf(System.currentTimeMillis()).substring(5);
        return "TICKET-" + timestamp;
    }
    
    private TicketDTO toDTO(Ticket ticket) {
        List<TicketLineDTO> lines = ticketLineRepository.findByTicketId(ticket.getId())
                .stream()
                .map(line -> TicketLineDTO.builder()
                        .id(line.getId())
                        .productId(line.getProduct().getId())
                        .productName(line.getProduct().getName())
                        .quantity(line.getQuantity())
                        .unitPrice(line.getUnitPrice())
                        .lineTotal(line.getLineTotal())
                        .taxPercentage(line.getTaxPercentage())
                        .taxAmount(line.getTaxAmount())
                        .build())
                .collect(Collectors.toList());
        
        return TicketDTO.builder()
                .id(ticket.getId())
                .number(ticket.getNumber())
                .type(ticket.getType().toString())
                .createdAt(ticket.getCreatedAt())
                .subtotal(ticket.getSubtotal())
                .taxAmount(ticket.getTaxAmount())
                .total(ticket.getTotal())
                .status(ticket.getStatus().toString())
                .customerId(ticket.getCustomer() != null ? ticket.getCustomer().getId() : null)
                .customerName(ticket.getCustomer() != null ? ticket.getCustomer().getFirstName() + " " + ticket.getCustomer().getLastName() : null)
                .closeCashId(ticket.getCloseCash() != null ? ticket.getCloseCash().getId() : null)
                .lines(lines)
                .build();
    }
}
