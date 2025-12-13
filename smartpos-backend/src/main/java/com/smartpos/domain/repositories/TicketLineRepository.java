package com.smartpos.domain.repositories;

import com.smartpos.domain.entities.TicketLine;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface TicketLineRepository extends JpaRepository<TicketLine, Long> {
    List<TicketLine> findByTicketId(Long ticketId);
    List<TicketLine> findByProductId(Long productId);
}
