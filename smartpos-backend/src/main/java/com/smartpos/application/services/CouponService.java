package com.smartpos.application.services;

import com.smartpos.application.dtos.CouponResponse;
import com.smartpos.application.dtos.CreateCouponRequest;
import com.smartpos.domain.entities.Coupon;
import com.smartpos.domain.repositories.CouponRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CouponService {
    
    private final CouponRepository couponRepository;
    
    public CouponResponse createCoupon(CreateCouponRequest request) {
        // Check if code already exists
        if (couponRepository.findByCode(request.getCode()).isPresent()) {
            throw new RuntimeException("Coupon code already exists");
        }
        
        Coupon coupon = Coupon.builder()
                .code(request.getCode())
                .description(request.getDescription())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .minimumPurchaseAmount(request.getMinimumPurchaseAmount())
                .maximumDiscountAmount(request.getMaximumDiscountAmount())
                .validFrom(request.getValidFrom())
                .validUntil(request.getValidUntil())
                .active(request.getActive() != null ? request.getActive() : true)
                .maxUsageCount(request.getMaxUsageCount())
                .currentUsageCount(0)
                .createdAt(LocalDateTime.now())
                .build();
        
        coupon = couponRepository.save(coupon);
        return CouponResponse.fromEntity(coupon);
    }
    
    public CouponResponse updateCoupon(Long id, CreateCouponRequest request) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
        
        // Check if code is being changed and if new code already exists
        if (!coupon.getCode().equals(request.getCode())) {
            if (couponRepository.findByCode(request.getCode()).isPresent()) {
                throw new RuntimeException("Coupon code already exists");
            }
            coupon.setCode(request.getCode());
        }
        
        coupon.setDescription(request.getDescription());
        coupon.setDiscountType(request.getDiscountType());
        coupon.setDiscountValue(request.getDiscountValue());
        coupon.setMinimumPurchaseAmount(request.getMinimumPurchaseAmount());
        coupon.setMaximumDiscountAmount(request.getMaximumDiscountAmount());
        coupon.setValidFrom(request.getValidFrom());
        coupon.setValidUntil(request.getValidUntil());
        coupon.setActive(request.getActive());
        coupon.setMaxUsageCount(request.getMaxUsageCount());
        
        coupon = couponRepository.save(coupon);
        return CouponResponse.fromEntity(coupon);
    }
    
    public void deleteCoupon(Long id) {
        if (!couponRepository.existsById(id)) {
            throw new RuntimeException("Coupon not found");
        }
        couponRepository.deleteById(id);
    }
    
    public CouponResponse getCoupon(Long id) {
        Coupon coupon = couponRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
        return CouponResponse.fromEntity(coupon);
    }
    
    public CouponResponse getCouponByCode(String code) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
        return CouponResponse.fromEntity(coupon);
    }
    
    public List<CouponResponse> getAllCoupons() {
        return couponRepository.findAll().stream()
                .map(CouponResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<CouponResponse> getActiveCoupons() {
        return couponRepository.findByActiveTrue().stream()
                .map(CouponResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    public BigDecimal validateAndCalculateDiscount(String code, BigDecimal amount) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
        
        if (!coupon.isValid()) {
            throw new RuntimeException("Coupon is not valid or has expired");
        }
        
        return coupon.calculateDiscount(amount);
    }
    
    public void incrementUsageCount(String code) {
        Coupon coupon = couponRepository.findByCode(code)
                .orElseThrow(() -> new RuntimeException("Coupon not found"));
        
        coupon.setCurrentUsageCount(coupon.getCurrentUsageCount() + 1);
        couponRepository.save(coupon);
    }
}
