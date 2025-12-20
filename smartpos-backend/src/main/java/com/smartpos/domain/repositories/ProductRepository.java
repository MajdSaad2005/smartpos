package com.smartpos.domain.repositories;

import com.smartpos.domain.entities.Product;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.List;

public interface ProductRepository extends JpaRepository<Product, Long> {
    Optional<Product> findByCode(String code);
    Optional<Product> findByBarcode(String barcode);
    List<Product> findByActiveTrue();
    List<Product> findBySupplierId(Long supplierId);
    
    @Query("SELECT p FROM Product p WHERE p.active = true AND (p.name LIKE %:searchTerm% OR p.code LIKE %:searchTerm% OR p.barcode LIKE %:searchTerm%)")
    List<Product> searchActiveProducts(@Param("searchTerm") String searchTerm);
}
