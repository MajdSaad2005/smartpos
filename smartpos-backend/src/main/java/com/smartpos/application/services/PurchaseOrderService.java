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
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class PurchaseOrderService {
    
    private final PurchaseOrderRepository purchaseOrderRepository;
    private final PurchaseOrderLineRepository purchaseOrderLineRepository;
    private final SupplierRepository supplierRepository;
    private final ProductRepository productRepository;
    private final StockCurrentRepository stockCurrentRepository;
    
    public PurchaseOrderDTO createPurchaseOrder(CreatePurchaseOrderRequest request) {
        // Validate supplier exists
        Supplier supplier = supplierRepository.findById(request.getSupplierId())
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        
        // Generate order number
        String orderNumber = generateOrderNumber();
        
        // Create purchase order
        PurchaseOrder po = PurchaseOrder.builder()
                .orderNumber(orderNumber)
                .supplier(supplier)
                .status(PurchaseOrder.OrderStatus.PENDING)
                .orderDate(LocalDateTime.now())
                .expectedDeliveryDate(request.getExpectedDeliveryDate())
                .notes(request.getNotes())
                .subtotal(BigDecimal.ZERO)
                .taxAmount(BigDecimal.ZERO)
                .total(BigDecimal.ZERO)
                .build();
        
        po = purchaseOrderRepository.save(po);
        
        // Add lines and calculate totals
        BigDecimal subtotal = BigDecimal.ZERO;
        BigDecimal totalTax = BigDecimal.ZERO;
        
        for (CreatePurchaseOrderLineRequest lineRequest : request.getLines()) {
            Product product = productRepository.findById(lineRequest.getProductId())
                    .orElseThrow(() -> new RuntimeException("Product not found: " + lineRequest.getProductId()));
            
            BigDecimal unitCost = product.getPurchasePrice() != null ? product.getPurchasePrice() : BigDecimal.ZERO;
            BigDecimal lineSubtotal = unitCost.multiply(BigDecimal.valueOf(lineRequest.getQuantity()));
            
            BigDecimal taxPercent = product.getTaxPercentage() != null ? product.getTaxPercentage() : BigDecimal.ZERO;
            BigDecimal lineTaxAmount = lineSubtotal.multiply(taxPercent).divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
            
            PurchaseOrderLine line = PurchaseOrderLine.builder()
                    .purchaseOrder(po)
                    .product(product)
                    .quantityOrdered(lineRequest.getQuantity())
                    .quantityReceived(0)
                    .unitCost(unitCost)
                    .lineTotal(lineSubtotal)
                    .taxPercentage(taxPercent)
                    .taxAmount(lineTaxAmount)
                    .build();
            
            purchaseOrderLineRepository.save(line);
            
            subtotal = subtotal.add(lineSubtotal);
            totalTax = totalTax.add(lineTaxAmount);
        }
        
        // Update totals
        po.setSubtotal(subtotal);
        po.setTaxAmount(totalTax);
        po.setTotal(subtotal.add(totalTax));
        
        po = purchaseOrderRepository.save(po);
        return toDTO(po);
    }
    
    public PurchaseOrderDTO receivePurchaseOrder(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase order not found"));
        
        if (po.getStatus() == PurchaseOrder.OrderStatus.RECEIVED) {
            throw new RuntimeException("Purchase order already received");
        }
        
        if (po.getStatus() == PurchaseOrder.OrderStatus.CANCELLED) {
            throw new RuntimeException("Cannot receive cancelled purchase order");
        }
        
        // Mark all lines as fully received and update stock
        List<PurchaseOrderLine> lines = purchaseOrderLineRepository.findByPurchaseOrderId(id);
        
        for (PurchaseOrderLine line : lines) {
            line.setQuantityReceived(line.getQuantityOrdered());
            purchaseOrderLineRepository.save(line);
            
            // Update stock - add received quantity
            StockCurrent stockCurrent = stockCurrentRepository.findByProductId(line.getProduct().getId())
                    .orElseThrow(() -> new RuntimeException("Stock not found for product: " + line.getProduct().getId()));
            
            stockCurrent.setQuantity(stockCurrent.getQuantity() + line.getQuantityOrdered());
            stockCurrentRepository.save(stockCurrent);
        }
        
        // Update purchase order status
        po.setStatus(PurchaseOrder.OrderStatus.RECEIVED);
        po.setReceivedDate(LocalDateTime.now());
        po = purchaseOrderRepository.save(po);
        
        return toDTO(po);
    }
    
    public void cancelPurchaseOrder(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase order not found"));
        
        if (po.getStatus() == PurchaseOrder.OrderStatus.RECEIVED) {
            throw new RuntimeException("Cannot cancel received purchase order");
        }
        
        po.setStatus(PurchaseOrder.OrderStatus.CANCELLED);
        purchaseOrderRepository.save(po);
    }
    
    @Transactional(readOnly = true)
    public PurchaseOrderDTO getPurchaseOrderById(Long id) {
        PurchaseOrder po = purchaseOrderRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Purchase order not found"));
        return toDTO(po);
    }
    
    @Transactional(readOnly = true)
    public List<PurchaseOrderDTO> getAllPurchaseOrders() {
        return purchaseOrderRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<PurchaseOrderDTO> getPurchaseOrdersBySupplier(Long supplierId) {
        return purchaseOrderRepository.findBySupplierId(supplierId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<PurchaseOrderDTO> getPurchaseOrdersByStatus(String status) {
        PurchaseOrder.OrderStatus orderStatus = PurchaseOrder.OrderStatus.valueOf(status);
        return purchaseOrderRepository.findByStatus(orderStatus).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    private String generateOrderNumber() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        return "PO-" + timestamp;
    }
    
    private PurchaseOrderDTO toDTO(PurchaseOrder po) {
        List<PurchaseOrderLine> lines = purchaseOrderLineRepository.findByPurchaseOrderId(po.getId());
        
        return PurchaseOrderDTO.builder()
                .id(po.getId())
                .orderNumber(po.getOrderNumber())
                .supplierId(po.getSupplier().getId())
                .supplierName(po.getSupplier().getName())
                .status(po.getStatus().name())
                .orderDate(po.getOrderDate())
                .expectedDeliveryDate(po.getExpectedDeliveryDate())
                .receivedDate(po.getReceivedDate())
                .subtotal(po.getSubtotal())
                .taxAmount(po.getTaxAmount())
                .total(po.getTotal())
                .notes(po.getNotes())
                .lines(lines.stream().map(this::lineToDTO).collect(Collectors.toList()))
                .createdAt(po.getCreatedAt())
                .updatedAt(po.getUpdatedAt())
                .build();
    }
    
    private PurchaseOrderLineDTO lineToDTO(PurchaseOrderLine line) {
        return PurchaseOrderLineDTO.builder()
                .id(line.getId())
                .productId(line.getProduct().getId())
                .productName(line.getProduct().getName())
                .productCode(line.getProduct().getCode())
                .quantityOrdered(line.getQuantityOrdered())
                .quantityReceived(line.getQuantityReceived())
                .unitCost(line.getUnitCost())
                .lineTotal(line.getLineTotal())
                .taxPercentage(line.getTaxPercentage())
                .taxAmount(line.getTaxAmount())
                .build();
    }
}
