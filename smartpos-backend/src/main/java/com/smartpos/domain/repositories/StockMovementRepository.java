package com.smartpos.domain.repositories;

import com.smartpos.domain.entities.StockMovement;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface StockMovementRepository extends JpaRepository<StockMovement, Long> {
    List<StockMovement> findByTicketId(Long ticketId);
    List<StockMovement> findByProductId(Long productId);
}
