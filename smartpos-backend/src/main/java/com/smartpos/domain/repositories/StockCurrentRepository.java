package com.smartpos.domain.repositories;

import com.smartpos.domain.entities.StockCurrent;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface StockCurrentRepository extends JpaRepository<StockCurrent, Long> {
    Optional<StockCurrent> findByProductId(Long productId);
}
