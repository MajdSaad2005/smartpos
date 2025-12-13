package com.smartpos.domain.repositories;

import com.smartpos.domain.entities.TicketStock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketStockRepository extends JpaRepository<TicketStock, Long> {
    List<TicketStock> findByTicketId(Long ticketId);
    List<TicketStock> findByProductId(Long productId);
}
