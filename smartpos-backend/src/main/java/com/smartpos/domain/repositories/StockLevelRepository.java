package com.smartpos.domain.repositories;

import com.smartpos.domain.entities.StockLevel;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockLevelRepository extends JpaRepository<StockLevel, Long> {
    List<StockLevel> findByProductId(Long productId);
}
