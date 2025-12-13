package com.smartpos.presentation.controllers;

import com.smartpos.application.dtos.StockLevelDTO;
import com.smartpos.application.services.StockLevelService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/stock-levels")
@RequiredArgsConstructor
@Tag(name = "Stock Levels", description = "Stock level management endpoints")
public class StockLevelController {
    
    private final StockLevelService stockLevelService;
    
    @PostMapping
    @Operation(summary = "Create a stock level for a product")
    public ResponseEntity<StockLevelDTO> createStockLevel(
            @RequestParam Long productId,
            @RequestParam Integer minimumLevel,
            @RequestParam Integer maximumLevel) {
        return ResponseEntity.status(HttpStatus.CREATED)
                .body(stockLevelService.createStockLevel(productId, minimumLevel, maximumLevel));
    }
    
    @GetMapping("/product/{productId}")
    @Operation(summary = "Get all stock levels for a product")
    public ResponseEntity<List<StockLevelDTO>> getStockLevelsByProductId(@PathVariable Long productId) {
        return ResponseEntity.ok(stockLevelService.getStockLevelsByProductId(productId));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get stock level by ID")
    public ResponseEntity<StockLevelDTO> getStockLevelById(@PathVariable Long id) {
        return ResponseEntity.ok(stockLevelService.getStockLevelById(id));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update stock level")
    public ResponseEntity<StockLevelDTO> updateStockLevel(
            @PathVariable Long id,
            @RequestParam Integer minimumLevel,
            @RequestParam Integer maximumLevel) {
        return ResponseEntity.ok(stockLevelService.updateStockLevel(id, minimumLevel, maximumLevel));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete stock level")
    public ResponseEntity<Void> deleteStockLevel(@PathVariable Long id) {
        stockLevelService.deleteStockLevel(id);
        return ResponseEntity.noContent().build();
    }
}
