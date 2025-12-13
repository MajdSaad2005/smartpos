package com.smartpos.domain.repositories;

import com.smartpos.domain.entities.Customer;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface CustomerRepository extends JpaRepository<Customer, Long> {
    Optional<Customer> findByCode(String code);
    Optional<Customer> findByEmail(String email);
    Optional<Customer> findByTaxId(String taxId);
    List<Customer> findByActiveTrue();
    
    @Query("SELECT c FROM Customer c WHERE c.active = true AND (c.firstName LIKE %:searchTerm% OR c.lastName LIKE %:searchTerm%)")
    List<Customer> searchActiveCustomers(@Param("searchTerm") String searchTerm);
}
