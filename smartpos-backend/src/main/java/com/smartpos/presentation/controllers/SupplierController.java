package com.smartpos.presentation.controllers;

import com.smartpos.application.dtos.SupplierDTO;
import com.smartpos.application.dtos.CreateSupplierRequest;
import com.smartpos.application.services.SupplierService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/suppliers")
@RequiredArgsConstructor
@Tag(name = "Suppliers", description = "Supplier management endpoints")
public class SupplierController {
    
    private final SupplierService supplierService;
    
    @PostMapping
    @Operation(summary = "Create a new supplier")
    public ResponseEntity<SupplierDTO> createSupplier(@RequestBody CreateSupplierRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(supplierService.createSupplier(request));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get supplier by ID")
    public ResponseEntity<SupplierDTO> getSupplierById(@PathVariable Long id) {
        return ResponseEntity.ok(supplierService.getSupplierById(id));
    }
    
    @GetMapping
    @Operation(summary = "Get all active suppliers")
    public ResponseEntity<List<SupplierDTO>> getAllActiveSuppliers() {
        return ResponseEntity.ok(supplierService.getAllActiveSuppliers());
    }
    
    @GetMapping("/all")
    @Operation(summary = "Get all suppliers including inactive")
    public ResponseEntity<List<SupplierDTO>> getAllSuppliers() {
        return ResponseEntity.ok(supplierService.getAllSuppliers());
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update supplier")
    public ResponseEntity<SupplierDTO> updateSupplier(@PathVariable Long id, @RequestBody CreateSupplierRequest request) {
        return ResponseEntity.ok(supplierService.updateSupplier(id, request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete (deactivate) supplier")
    public ResponseEntity<Void> deleteSupplier(@PathVariable Long id) {
        supplierService.deleteSupplier(id);
        return ResponseEntity.noContent().build();
    }
}
