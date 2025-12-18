package com.smartpos.application.services;

import com.smartpos.application.dtos.CreateDiscountRequest;
import com.smartpos.application.dtos.DiscountResponse;
import com.smartpos.domain.entities.Discount;
import com.smartpos.domain.repositories.DiscountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class DiscountService {
    
    private final DiscountRepository discountRepository;
    
    public DiscountResponse createDiscount(CreateDiscountRequest request) {
        Discount discount = Discount.builder()
                .name(request.getName())
                .description(request.getDescription())
                .discountType(request.getDiscountType())
                .discountValue(request.getDiscountValue())
                .applicableOn(request.getApplicableOn())
                .applicableProductId(request.getApplicableProductId())
                .minimumPurchaseAmount(request.getMinimumPurchaseAmount())
                .maximumDiscountAmount(request.getMaximumDiscountAmount())
                .validFrom(request.getValidFrom())
                .validUntil(request.getValidUntil())
                .active(request.getActive() != null ? request.getActive() : true)
                .requiresCustomer(request.getRequiresCustomer() != null ? request.getRequiresCustomer() : false)
                .createdAt(LocalDateTime.now())
                .build();
        
        discount = discountRepository.save(discount);
        return DiscountResponse.fromEntity(discount);
    }
    
    public DiscountResponse updateDiscount(Long id, CreateDiscountRequest request) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount not found"));
        
        discount.setName(request.getName());
        discount.setDescription(request.getDescription());
        discount.setDiscountType(request.getDiscountType());
        discount.setDiscountValue(request.getDiscountValue());
        discount.setApplicableOn(request.getApplicableOn());
        discount.setApplicableProductId(request.getApplicableProductId());
        discount.setMinimumPurchaseAmount(request.getMinimumPurchaseAmount());
        discount.setMaximumDiscountAmount(request.getMaximumDiscountAmount());
        discount.setValidFrom(request.getValidFrom());
        discount.setValidUntil(request.getValidUntil());
        discount.setActive(request.getActive());
        discount.setRequiresCustomer(request.getRequiresCustomer());
        
        discount = discountRepository.save(discount);
        return DiscountResponse.fromEntity(discount);
    }
    
    public void deleteDiscount(Long id) {
        if (!discountRepository.existsById(id)) {
            throw new RuntimeException("Discount not found");
        }
        discountRepository.deleteById(id);
    }
    
    public DiscountResponse getDiscount(Long id) {
        Discount discount = discountRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Discount not found"));
        return DiscountResponse.fromEntity(discount);
    }
    
    public List<DiscountResponse> getAllDiscounts() {
        return discountRepository.findAll().stream()
                .map(DiscountResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<DiscountResponse> getActiveDiscounts() {
        LocalDateTime now = LocalDateTime.now();
        return discountRepository.findByActiveTrueAndValidFromBeforeAndValidUntilAfter(now, now).stream()
                .map(DiscountResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<DiscountResponse> getDiscountsForProduct(Long productId) {
        return discountRepository.findByActiveTrueAndApplicableProductId(productId).stream()
                .map(DiscountResponse::fromEntity)
                .collect(Collectors.toList());
    }
    
    public List<DiscountResponse> getTotalDiscounts() {
        return discountRepository.findByActiveTrueAndApplicableOn(Discount.ApplicableOn.TOTAL).stream()
                .map(DiscountResponse::fromEntity)
                .collect(Collectors.toList());
    }
}
