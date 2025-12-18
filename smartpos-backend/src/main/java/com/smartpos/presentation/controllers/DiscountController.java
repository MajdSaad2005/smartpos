package com.smartpos.presentation.controllers;

import com.smartpos.application.dtos.CreateDiscountRequest;
import com.smartpos.application.dtos.DiscountResponse;
import com.smartpos.application.services.DiscountService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/discounts")
@RequiredArgsConstructor
@Tag(name = "Discounts", description = "Discount management endpoints")
public class DiscountController {
    
    private final DiscountService discountService;
    
    @PostMapping
    @Operation(summary = "Create a new discount")
    public ResponseEntity<DiscountResponse> createDiscount(@RequestBody CreateDiscountRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(discountService.createDiscount(request));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a discount")
    public ResponseEntity<DiscountResponse> updateDiscount(@PathVariable Long id, @RequestBody CreateDiscountRequest request) {
        return ResponseEntity.ok(discountService.updateDiscount(id, request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a discount")
    public ResponseEntity<Void> deleteDiscount(@PathVariable Long id) {
        discountService.deleteDiscount(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get discount by ID")
    public ResponseEntity<DiscountResponse> getDiscount(@PathVariable Long id) {
        return ResponseEntity.ok(discountService.getDiscount(id));
    }
    
    @GetMapping
    @Operation(summary = "Get all discounts")
    public ResponseEntity<List<DiscountResponse>> getAllDiscounts() {
        return ResponseEntity.ok(discountService.getAllDiscounts());
    }
    
    @GetMapping("/active")
    @Operation(summary = "Get all active and valid discounts")
    public ResponseEntity<List<DiscountResponse>> getActiveDiscounts() {
        return ResponseEntity.ok(discountService.getActiveDiscounts());
    }
    
    @GetMapping("/product/{productId}")
    @Operation(summary = "Get discounts for a specific product")
    public ResponseEntity<List<DiscountResponse>> getDiscountsForProduct(@PathVariable Long productId) {
        return ResponseEntity.ok(discountService.getDiscountsForProduct(productId));
    }
    
    @GetMapping("/total")
    @Operation(summary = "Get discounts applicable to total")
    public ResponseEntity<List<DiscountResponse>> getTotalDiscounts() {
        return ResponseEntity.ok(discountService.getTotalDiscounts());
    }
}
