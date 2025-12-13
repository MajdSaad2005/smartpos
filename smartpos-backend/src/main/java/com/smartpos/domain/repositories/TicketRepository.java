package com.smartpos.domain.repositories;

import com.smartpos.domain.entities.Ticket;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.List;

public interface TicketRepository extends JpaRepository<Ticket, Long> {
    Optional<Ticket> findByNumber(String number);
    List<Ticket> findByCustomerId(Long customerId);
    List<Ticket> findByCloseCashId(Long closeCashId);
    
    @Query("SELECT t FROM Ticket t WHERE t.createdAt BETWEEN :startDate AND :endDate")
    List<Ticket> findByDateRange(@Param("startDate") LocalDateTime startDate, @Param("endDate") LocalDateTime endDate);
    
    List<Ticket> findByType(Ticket.TicketType type);
}
