package com.smartpos.presentation.controllers;

import com.smartpos.application.dtos.ProductDTO;
import com.smartpos.application.dtos.CreateProductRequest;
import com.smartpos.application.services.ProductService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/products")
@RequiredArgsConstructor
@Tag(name = "Products", description = "Product management endpoints")
public class ProductController {
    
    private final ProductService productService;
    
    @PostMapping
    @Operation(summary = "Create a new product")
    public ResponseEntity<ProductDTO> createProduct(@RequestBody CreateProductRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(productService.createProduct(request));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get product by ID")
    public ResponseEntity<ProductDTO> getProductById(@PathVariable Long id) {
        return ResponseEntity.ok(productService.getProductById(id));
    }
    
    @GetMapping("/code/{code}")
    @Operation(summary = "Get product by code")
    public ResponseEntity<ProductDTO> getProductByCode(@PathVariable String code) {
        return ResponseEntity.ok(productService.getProductByCode(code));
    }
    
    @GetMapping
    @Operation(summary = "Get all active products")
    public ResponseEntity<List<ProductDTO>> getAllActiveProducts() {
        return ResponseEntity.ok(productService.getAllActiveProducts());
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search products by name")
    public ResponseEntity<List<ProductDTO>> searchProducts(@RequestParam String searchTerm) {
        return ResponseEntity.ok(productService.searchProducts(searchTerm));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update product")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable Long id, @RequestBody CreateProductRequest request) {
        return ResponseEntity.ok(productService.updateProduct(id, request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete (deactivate) product")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
}
