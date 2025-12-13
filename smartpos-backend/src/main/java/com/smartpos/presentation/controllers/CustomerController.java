package com.smartpos.presentation.controllers;

import com.smartpos.application.dtos.CustomerDTO;
import com.smartpos.application.dtos.CreateCustomerRequest;
import com.smartpos.application.services.CustomerService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/v1/customers")
@RequiredArgsConstructor
@Tag(name = "Customers", description = "Customer management endpoints")
public class CustomerController {
    
    private final CustomerService customerService;
    
    @PostMapping
    @Operation(summary = "Create a new customer")
    public ResponseEntity<CustomerDTO> createCustomer(@RequestBody CreateCustomerRequest request) {
        return ResponseEntity.status(HttpStatus.CREATED).body(customerService.createCustomer(request));
    }
    
    @GetMapping("/{id}")
    @Operation(summary = "Get customer by ID")
    public ResponseEntity<CustomerDTO> getCustomerById(@PathVariable Long id) {
        return ResponseEntity.ok(customerService.getCustomerById(id));
    }
    
    @GetMapping
    @Operation(summary = "Get all active customers")
    public ResponseEntity<List<CustomerDTO>> getAllActiveCustomers() {
        return ResponseEntity.ok(customerService.getAllActiveCustomers());
    }
    
    @GetMapping("/search")
    @Operation(summary = "Search customers by name")
    public ResponseEntity<List<CustomerDTO>> searchCustomers(@RequestParam String searchTerm) {
        return ResponseEntity.ok(customerService.searchCustomers(searchTerm));
    }
    
    @PutMapping("/{id}")
    @Operation(summary = "Update customer")
    public ResponseEntity<CustomerDTO> updateCustomer(@PathVariable Long id, @RequestBody CreateCustomerRequest request) {
        return ResponseEntity.ok(customerService.updateCustomer(id, request));
    }
    
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete (deactivate) customer")
    public ResponseEntity<Void> deleteCustomer(@PathVariable Long id) {
        customerService.deleteCustomer(id);
        return ResponseEntity.noContent().build();
    }
}
