package com.smartpos.domain.repositories;

import com.smartpos.domain.entities.Supplier;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.List;

public interface SupplierRepository extends JpaRepository<Supplier, Long> {
    Optional<Supplier> findByCode(String code);
    Optional<Supplier> findByEmail(String email);
    Optional<Supplier> findByTaxId(String taxId);
    List<Supplier> findByActiveTrue();
}
