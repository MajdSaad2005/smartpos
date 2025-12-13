package com.smartpos.application.services;

import com.smartpos.application.dtos.CreateSupplierRequest;
import com.smartpos.application.dtos.SupplierDTO;
import com.smartpos.domain.entities.Supplier;
import com.smartpos.domain.repositories.SupplierRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class SupplierService {
    
    private final SupplierRepository supplierRepository;
    
    public SupplierDTO createSupplier(CreateSupplierRequest request) {
        if (supplierRepository.findByCode(request.getCode()).isPresent()) {
            throw new RuntimeException("Supplier code already exists");
        }
        
        Supplier supplier = Supplier.builder()
                .code(request.getCode())
                .name(request.getName())
                .address(request.getAddress())
                .email(request.getEmail())
                .phone(request.getPhone())
                .taxId(request.getTaxId())
                .active(true)
                .build();
        
        supplier = supplierRepository.save(supplier);
        return toDTO(supplier);
    }
    
    @Transactional(readOnly = true)
    public SupplierDTO getSupplierById(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        return toDTO(supplier);
    }
    
    @Transactional(readOnly = true)
    public List<SupplierDTO> getAllActiveSuppliers() {
        return supplierRepository.findByActiveTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<SupplierDTO> getAllSuppliers() {
        return supplierRepository.findAll().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public SupplierDTO updateSupplier(Long id, CreateSupplierRequest request) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        
        if (!request.getCode().equals(supplier.getCode())) {
            if (supplierRepository.findByCode(request.getCode()).isPresent()) {
                throw new RuntimeException("Supplier code already exists");
            }
            supplier.setCode(request.getCode());
        }
        
        supplier.setName(request.getName());
        supplier.setAddress(request.getAddress());
        supplier.setEmail(request.getEmail());
        supplier.setPhone(request.getPhone());
        supplier.setTaxId(request.getTaxId());
        
        supplier = supplierRepository.save(supplier);
        return toDTO(supplier);
    }
    
    public void deleteSupplier(Long id) {
        Supplier supplier = supplierRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Supplier not found"));
        supplier.setActive(false);
        supplierRepository.save(supplier);
    }
    
    private SupplierDTO toDTO(Supplier supplier) {
        return SupplierDTO.builder()
                .id(supplier.getId())
                .code(supplier.getCode())
                .name(supplier.getName())
                .address(supplier.getAddress())
                .email(supplier.getEmail())
                .phone(supplier.getPhone())
                .taxId(supplier.getTaxId())
                .active(supplier.getActive())
                .build();
    }
}
