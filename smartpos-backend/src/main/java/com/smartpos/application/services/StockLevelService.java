package com.smartpos.application.services;

import com.smartpos.application.dtos.StockLevelDTO;
import com.smartpos.domain.entities.Product;
import com.smartpos.domain.entities.StockLevel;
import com.smartpos.domain.repositories.ProductRepository;
import com.smartpos.domain.repositories.StockLevelRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class StockLevelService {
    
    private final StockLevelRepository stockLevelRepository;
    private final ProductRepository productRepository;
    
    public StockLevelDTO createStockLevel(Long productId, Integer minimumLevel, Integer maximumLevel) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new RuntimeException("Product not found"));
        
        StockLevel stockLevel = StockLevel.builder()
                .product(product)
                .minimumLevel(minimumLevel)
                .maximumLevel(maximumLevel)
                .build();
        
        stockLevel = stockLevelRepository.save(stockLevel);
        return toDTO(stockLevel);
    }
    
    @Transactional(readOnly = true)
    public List<StockLevelDTO> getStockLevelsByProductId(Long productId) {
        return stockLevelRepository.findByProductId(productId).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public StockLevelDTO getStockLevelById(Long id) {
        StockLevel stockLevel = stockLevelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock level not found"));
        return toDTO(stockLevel);
    }
    
    public StockLevelDTO updateStockLevel(Long id, Integer minimumLevel, Integer maximumLevel) {
        StockLevel stockLevel = stockLevelRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Stock level not found"));
        
        stockLevel.setMinimumLevel(minimumLevel);
        stockLevel.setMaximumLevel(maximumLevel);
        
        stockLevel = stockLevelRepository.save(stockLevel);
        return toDTO(stockLevel);
    }
    
    public void deleteStockLevel(Long id) {
        stockLevelRepository.deleteById(id);
    }
    
    private StockLevelDTO toDTO(StockLevel stockLevel) {
        return StockLevelDTO.builder()
                .id(stockLevel.getId())
                .productId(stockLevel.getProduct().getId())
                .productName(stockLevel.getProduct().getName())
                .minimumLevel(stockLevel.getMinimumLevel())
                .maximumLevel(stockLevel.getMaximumLevel())
                .build();
    }
}
