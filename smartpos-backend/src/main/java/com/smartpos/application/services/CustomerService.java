package com.smartpos.application.services;

import com.smartpos.application.dtos.CreateCustomerRequest;
import com.smartpos.application.dtos.CustomerDTO;
import com.smartpos.domain.entities.Customer;
import com.smartpos.domain.repositories.CustomerRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CustomerService {
    
    private final CustomerRepository customerRepository;
    
    public CustomerDTO createCustomer(CreateCustomerRequest request) {
        if (customerRepository.findByCode(request.getCode()).isPresent()) {
            throw new RuntimeException("Customer code already exists");
        }
        
        Customer customer = Customer.builder()
                .code(request.getCode())
                .firstName(request.getFirstName())
                .lastName(request.getLastName())
                .email(request.getEmail())
                .phone(request.getPhone())
                .address(request.getAddress())
                .taxId(request.getTaxId())
                .active(true)
                .build();
        
        customer = customerRepository.save(customer);
        return toDTO(customer);
    }
    
    @Transactional(readOnly = true)
    public CustomerDTO getCustomerById(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        return toDTO(customer);
    }
    
    @Transactional(readOnly = true)
    public List<CustomerDTO> getAllActiveCustomers() {
        return customerRepository.findByActiveTrue().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    @Transactional(readOnly = true)
    public List<CustomerDTO> searchCustomers(String searchTerm) {
        return customerRepository.searchActiveCustomers(searchTerm).stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public CustomerDTO updateCustomer(Long id, CreateCustomerRequest request) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        
        if (!request.getCode().equals(customer.getCode())) {
            if (customerRepository.findByCode(request.getCode()).isPresent()) {
                throw new RuntimeException("Customer code already exists");
            }
            customer.setCode(request.getCode());
        }
        
        customer.setFirstName(request.getFirstName());
        customer.setLastName(request.getLastName());
        customer.setEmail(request.getEmail());
        customer.setPhone(request.getPhone());
        customer.setAddress(request.getAddress());
        customer.setTaxId(request.getTaxId());
        
        customer = customerRepository.save(customer);
        return toDTO(customer);
    }
    
    public void deleteCustomer(Long id) {
        Customer customer = customerRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Customer not found"));
        customer.setActive(false);
        customerRepository.save(customer);
    }
    
    private CustomerDTO toDTO(Customer customer) {
        return CustomerDTO.builder()
                .id(customer.getId())
                .code(customer.getCode())
                .firstName(customer.getFirstName())
                .lastName(customer.getLastName())
                .email(customer.getEmail())
                .phone(customer.getPhone())
                .address(customer.getAddress())
                .taxId(customer.getTaxId())
                .active(customer.getActive())
                .build();
    }
}
