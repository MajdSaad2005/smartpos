package com.smartpos.presentation.controllers;

import com.smartpos.application.dtos.CouponResponse;
import com.smartpos.application.dtos.CreateCouponRequest;
import com.smartpos.application.services.CouponService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/v1/coupons")
@RequiredArgsConstructor
@Tag(name = "Coupons", description = "Coupon management endpoints")
public class CouponController {
    
    private final CouponService couponService;
    
    @PostMapping
    @Operation(summary = "Create a new coupon")
    public ResponseEntity<CouponResponse> createCoupon(@RequestBody CreateCouponRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(couponService.createCoupon(request));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update a coupon")
    public ResponseEntity<CouponResponse> updateCoupon(@PathVariable Long id, @RequestBody CreateCouponRequest request) {
        return ResponseEntity.ok(couponService.updateCoupon(id, request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a coupon")
    public ResponseEntity<Void> deleteCoupon(@PathVariable Long id) {
        couponService.deleteCoupon(id);
        return ResponseEntity.noContent().build();
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get coupon by ID")
    public ResponseEntity<CouponResponse> getCoupon(@PathVariable Long id) {
        return ResponseEntity.ok(couponService.getCoupon(id));
    }
    
    @GetMapping("/code/{code}")
    @Operation(summary = "Get coupon by code")
    public ResponseEntity<CouponResponse> getCouponByCode(@PathVariable String code) {
        return ResponseEntity.ok(couponService.getCouponByCode(code));
    }
    
    @GetMapping
    @Operation(summary = "Get all coupons")
    public ResponseEntity<List<CouponResponse>> getAllCoupons() {
        return ResponseEntity.ok(couponService.getAllCoupons());
    }
    
    @GetMapping("/active")
    @Operation(summary = "Get all active coupons")
    public ResponseEntity<List<CouponResponse>> getActiveCoupons() {
        return ResponseEntity.ok(couponService.getActiveCoupons());
    }
    
    @PostMapping("/validate")
    @Operation(summary = "Validate a coupon and calculate discount")
    public ResponseEntity<Map<String, BigDecimal>> validateCoupon(@RequestBody Map<String, Object> request) {
        String code = (String) request.get("code");
        BigDecimal amount = new BigDecimal(request.get("amount").toString());
        
        try {
            BigDecimal discount = couponService.validateAndCalculateDiscount(code, amount);
            return ResponseEntity.ok(Map.of("discount", discount));
        } catch (RuntimeException e) {
            return ResponseEntity.badRequest().body(Map.of("error", BigDecimal.ZERO));
        }
    }
}
