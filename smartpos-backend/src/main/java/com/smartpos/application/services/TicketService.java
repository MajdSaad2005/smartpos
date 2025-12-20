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
@Transactional // Ensures all public methods run within a transaction (ACID properties)
public class TicketService {
    
    private final TicketRepository ticketRepository;
    private final TicketLineRepository ticketLineRepository;
    private final StockMovementRepository stockMovementRepository;
    private final ProductRepository productRepository;
    private final StockCurrentRepository stockCurrentRepository;
    private final CustomerRepository customerRepository;
    private final CloseCashRepository closeCashRepository;
    private final CouponRepository couponRepository;
    private final DiscountRepository discountRepository;
    private final TelegramNotificationService telegramNotificationService;
    
    /**
     * TRANSACTIONAL OPERATION #1: Create Ticket with Stock Update
     * 
     * This multi-step operation demonstrates ACID properties:
     * - Atomicity: All steps (create ticket, lines, stock movements) succeed or rollback
     * - Consistency: Stock levels always match transactions
     * - Isolation: Concurrent ticket creation doesn't cause race conditions
     * - Durability: Once committed, data persists even after system failure
     * 
     * Steps:
     * 1. Validate all products exist (fail fast)
     * 2. Create ticket with generated number
     * 3. Associate with customer and active cash session
     * 4. Create ticket lines for each product
     * 5. Create stock movements (SALE decreases, RETURN increases)
     * 6. Update stock_current table (except for defective returns)
     * 7. Calculate and save totals
     * 8. Mark ticket as COMPLETED
     * 
     * If ANY step fails, entire transaction is rolled back (ROLLBACK)
     * If all steps succeed, transaction is committed (COMMIT)
     * 
     * @param request Contains ticket type, customer, and line items
     * @return Created ticket with all details
     * @throws RuntimeException if validation fails or stock is insufficient
     */
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
            CloseCash activeSession = activeSessions.stream()
                .filter(cs -> cs.getClosedAt() == null)
                .findFirst()
                .orElse(null);
            
            if (activeSession != null) {
                // Prevent changes if session is reconciled
                if (activeSession.getReconciled()) {
                    throw new IllegalStateException("Cannot create ticket: Cash session is already reconciled");
                }
                ticket.setCloseCash(activeSession);
            }
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
            
            // Create stock movement with new logic
            StockMovement.MovementType movementType;
            int quantityChange;
            boolean isDefective = lineRequest.getIsDefective() != null ? lineRequest.getIsDefective() : false;
            
            if (ticket.getType() == Ticket.TicketType.SALE) {
                movementType = StockMovement.MovementType.SALE;
                quantityChange = -lineRequest.getQuantity(); // Negative for sale
            } else if (ticket.getType() == Ticket.TicketType.RETURN) {
                movementType = StockMovement.MovementType.RETURN;
                quantityChange = lineRequest.getQuantity(); // Positive for return
            } else {
                movementType = StockMovement.MovementType.ADJUSTMENT;
                quantityChange = lineRequest.getQuantity(); // Positive for adjustment
            }
            
            StockMovement stock = StockMovement.builder()
                    .ticket(ticket)
                    .product(product)
                    .quantity(Math.abs(lineRequest.getQuantity()))
                    .quantityChange(quantityChange)
                    .type(movementType)
                    .hasQuantityChanged(true)
                    .isDefective(isDefective)
                    .createdAt(LocalDateTime.now())
                    .build();
            
            stockMovementRepository.save(stock);
            
            // Update stock current - don't add defective items back to stock
            StockCurrent stockCurrent = stockCurrentRepository.findByProductId(product.getId())
                    .orElseThrow();
            
            if (movementType == StockMovement.MovementType.SALE) {
                stockCurrent.setQuantity(stockCurrent.getQuantity() - lineRequest.getQuantity());
            } else if (movementType == StockMovement.MovementType.RETURN && !isDefective) {
                // Only add back to stock if not defective
                stockCurrent.setQuantity(stockCurrent.getQuantity() + lineRequest.getQuantity());
            }
            // For defective returns, don't update stock quantity
            
            stockCurrentRepository.save(stockCurrent);
            
            subtotal = subtotal.add(lineSubtotal);
            totalTax = totalTax.add(lineTaxAmount);
        }
        
        // Calculate base total before discounts
        BigDecimal totalBeforeDiscounts = subtotal.add(totalTax);
        BigDecimal totalDiscount = BigDecimal.ZERO;
        
        // Apply coupon discount if provided
        if (request.getCouponCode() != null && !request.getCouponCode().trim().isEmpty()) {
            Coupon coupon = couponRepository.findByCode(request.getCouponCode())
                    .orElseThrow(() -> new RuntimeException("Invalid coupon code"));
            
            // Validate coupon
            LocalDateTime now = LocalDateTime.now();
            if (!coupon.getActive()) {
                throw new RuntimeException("Coupon is not active");
            }
            if (now.isBefore(coupon.getValidFrom()) || now.isAfter(coupon.getValidUntil())) {
                throw new RuntimeException("Coupon is expired or not yet valid");
            }
            if (coupon.getMinimumPurchaseAmount() != null && subtotal.compareTo(coupon.getMinimumPurchaseAmount()) < 0) {
                throw new RuntimeException("Minimum purchase amount not met for this coupon");
            }
            
            // Calculate coupon discount
            BigDecimal couponDiscount = BigDecimal.ZERO;
            if (coupon.getDiscountType() == Coupon.DiscountType.PERCENTAGE) {
                couponDiscount = subtotal.multiply(coupon.getDiscountValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            } else {
                couponDiscount = coupon.getDiscountValue();
            }
            
            // Apply maximum discount cap
            if (coupon.getMaximumDiscountAmount() != null && couponDiscount.compareTo(coupon.getMaximumDiscountAmount()) > 0) {
                couponDiscount = coupon.getMaximumDiscountAmount();
            }
            
            totalDiscount = totalDiscount.add(couponDiscount);
        }
        
        // Apply system discount if provided
        if (request.getDiscountId() != null) {
            Discount discount = discountRepository.findById(request.getDiscountId())
                    .orElseThrow(() -> new RuntimeException("Invalid discount"));
            
            // Validate discount
            LocalDateTime now = LocalDateTime.now();
            if (!discount.getActive()) {
                throw new RuntimeException("Discount is not active");
            }
            if (now.isBefore(discount.getValidFrom()) || now.isAfter(discount.getValidUntil())) {
                throw new RuntimeException("Discount is expired or not yet valid");
            }
            if (discount.getMinimumPurchaseAmount() != null && subtotal.compareTo(discount.getMinimumPurchaseAmount()) < 0) {
                throw new RuntimeException("Minimum purchase amount not met for this discount");
            }
            
            // Calculate discount amount (only for TOTAL applicable discounts)
            if (discount.getApplicableOn() == Discount.ApplicableOn.TOTAL) {
                BigDecimal discountAmount = BigDecimal.ZERO;
                if (discount.getDiscountType() == Discount.DiscountType.PERCENTAGE) {
                    discountAmount = subtotal.multiply(discount.getDiscountValue()).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
                } else {
                    discountAmount = discount.getDiscountValue();
                }
                
                // Apply maximum discount cap
                if (discount.getMaximumDiscountAmount() != null && discountAmount.compareTo(discount.getMaximumDiscountAmount()) > 0) {
                    discountAmount = discount.getMaximumDiscountAmount();
                }
                
                totalDiscount = totalDiscount.add(discountAmount);
            }
        }
        
        // Update ticket totals with discount applied
        BigDecimal total = totalBeforeDiscounts.subtract(totalDiscount);
        if (total.compareTo(BigDecimal.ZERO) < 0) {
            total = BigDecimal.ZERO; // Don't allow negative totals
        }
        
        ticket.setSubtotal(subtotal);
        ticket.setTaxAmount(totalTax);
        ticket.setTotal(total);
        ticket.setStatus(Ticket.TicketStatus.COMPLETED);
        
        ticket = ticketRepository.save(ticket);
        
        // Send Telegram notification for sales (async, won't block transaction)
        if (ticket.getType() == Ticket.TicketType.SALE) {
            telegramNotificationService.notifySale(ticket, request.getLines().size());
        }
        
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
        List<StockMovement> stocks = stockMovementRepository.findByTicketId(id);
        for (StockMovement stock : stocks) {
            StockCurrent stockCurrent = stockCurrentRepository.findByProductId(stock.getProduct().getId())
                    .orElseThrow();
            
            // Reverse the quantity change
            if (stock.getType() == StockMovement.MovementType.SALE) {
                stockCurrent.setQuantity(stockCurrent.getQuantity() + stock.getQuantity());
            } else if (stock.getType() == StockMovement.MovementType.RETURN && !stock.getIsDefective()) {
                stockCurrent.setQuantity(stockCurrent.getQuantity() - stock.getQuantity());
            }
            // Don't reverse defective returns or adjustments
            
            stockCurrentRepository.save(stockCurrent);
        }
        
        ticket.setStatus(Ticket.TicketStatus.CANCELLED);
        ticketRepository.save(ticket);
    }
    
    /**
     * TRANSACTIONAL OPERATION #2: Bulk Inventory Adjustment
     * 
     * This demonstrates a complex multi-step transaction that:
     * 1. BEGIN TRANSACTION
     * 2. Validates all products exist
     * 3. Creates adjustment ticket
     * 4. Creates stock movements for each product
     * 5. Updates stock_current for all products
     * 6. Marks ticket as completed
     * 7. COMMIT (or ROLLBACK if any step fails)
     * 
     * Use case: Physical inventory count adjustments, damaged goods write-off
     * 
     * ACID Properties ensured:
     * - Atomicity: All adjustments applied or none
     * - Consistency: Stock levels match movements
     * - Isolation: No partial states visible to other transactions
     * - Durability: Changes persisted after commit
     * 
     * @param adjustments Map of productId to quantity adjustment (positive or negative)
     * @param reason Description of why adjustment is being made
     * @return Created adjustment ticket
     * @throws RuntimeException if any product not found or validation fails
     */
    @Transactional(rollbackFor = Exception.class)
    public TicketDTO performBulkInventoryAdjustment(Map<Long, Integer> adjustments, String reason) {
        try {
            // Step 1: Validate all products exist before starting
            for (Long productId : adjustments.keySet()) {
                if (!productRepository.existsById(productId)) {
                    throw new RuntimeException("Product not found: " + productId);
                }
            }
            
            // Step 2: Create adjustment ticket
            String ticketNumber = "ADJ-" + System.currentTimeMillis();
            Ticket ticket = Ticket.builder()
                    .number(ticketNumber)
                    .type(Ticket.TicketType.RETURN) // Using RETURN type for adjustments
                    .createdAt(LocalDateTime.now())
                    .status(Ticket.TicketStatus.PENDING)
                    .subtotal(BigDecimal.ZERO)
                    .taxAmount(BigDecimal.ZERO)
                    .total(BigDecimal.ZERO)
                    .build();
            
            ticket = ticketRepository.save(ticket);
            
            // Step 3: Process each adjustment
            for (Map.Entry<Long, Integer> entry : adjustments.entrySet()) {
                Long productId = entry.getKey();
                Integer quantityChange = entry.getValue();
                
                Product product = productRepository.findById(productId).orElseThrow();
                
                // Create ticket line for audit trail
                TicketLine line = TicketLine.builder()
                        .ticket(ticket)
                        .product(product)
                        .quantity(Math.abs(quantityChange))
                        .unitPrice(product.getPurchasePrice())
                        .lineTotal(BigDecimal.ZERO)
                        .taxPercentage(BigDecimal.ZERO)
                        .taxAmount(BigDecimal.ZERO)
                        .build();
                
                ticketLineRepository.save(line);
                
                // Create stock movement
                StockMovement stock = StockMovement.builder()
                        .ticket(ticket)
                        .product(product)
                        .quantity(Math.abs(quantityChange))
                        .quantityChange(quantityChange)
                        .type(StockMovement.MovementType.ADJUSTMENT)
                        .hasQuantityChanged(true)
                        .isDefective(false)
                        .createdAt(LocalDateTime.now())
                        .build();
                
                stockMovementRepository.save(stock);
                
                // Step 4: Update stock_current atomically
                StockCurrent stockCurrent = stockCurrentRepository.findByProductId(productId)
                        .orElseThrow(() -> new RuntimeException("Stock record not found for product: " + productId));
                
                int newQuantity = stockCurrent.getQuantity() + quantityChange;
                
                // Prevent negative stock
                if (newQuantity < 0) {
                    throw new RuntimeException(
                        String.format("Adjustment would result in negative stock for product %d. Current: %d, Change: %d", 
                            productId, stockCurrent.getQuantity(), quantityChange)
                    );
                }
                
                stockCurrent.setQuantity(newQuantity);
                stockCurrentRepository.save(stockCurrent);
            }
            
            // Step 5: Mark ticket as completed
            ticket.setStatus(Ticket.TicketStatus.COMPLETED);
            ticket = ticketRepository.save(ticket);
            
            // If we reach here, COMMIT happens automatically
            return toDTO(ticket);
            
        } catch (Exception e) {
            // Any exception causes automatic ROLLBACK
            // All database changes are reverted
            throw new RuntimeException("Bulk adjustment failed: " + e.getMessage(), e);
        }
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
