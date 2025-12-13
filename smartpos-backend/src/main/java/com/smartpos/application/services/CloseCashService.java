package com.smartpos.application.services;

import com.smartpos.application.dtos.CloseCashDTO;
import com.smartpos.domain.entities.CloseCash;
import com.smartpos.domain.entities.Ticket;
import com.smartpos.domain.repositories.CloseCashRepository;
import com.smartpos.domain.repositories.TicketRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Transactional
public class CloseCashService {
    
    private final CloseCashRepository closeCashRepository;
    private final TicketRepository ticketRepository;
    
    public CloseCashDTO openCloseCash() {
        CloseCash closeCash = CloseCash.builder()
                .openedAt(LocalDateTime.now())
                .closedAt(null)
                .totalSales(BigDecimal.ZERO)
                .totalReturns(BigDecimal.ZERO)
                .netAmount(BigDecimal.ZERO)
                .reconciled(false)
                .build();
        
        closeCash = closeCashRepository.save(closeCash);
        return toDTO(closeCash);
    }
    
    public CloseCashDTO closeCloseCash(Long id) {
        CloseCash closeCash = closeCashRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Close cash session not found"));
        
        if (closeCash.getClosedAt() != null) {
            throw new RuntimeException("Close cash session is already closed");
        }
        
        // Calculate totals from tickets
        List<Ticket> tickets = ticketRepository.findByCloseCashId(id);
        
        BigDecimal totalSales = BigDecimal.ZERO;
        BigDecimal totalReturns = BigDecimal.ZERO;
        
        for (Ticket ticket : tickets) {
            if (ticket.getType() == Ticket.TicketType.SALE) {
                totalSales = totalSales.add(ticket.getTotal());
            } else {
                totalReturns = totalReturns.add(ticket.getTotal());
            }
        }
        
        closeCash.setClosedAt(LocalDateTime.now());
        closeCash.setTotalSales(totalSales);
        closeCash.setTotalReturns(totalReturns);
        closeCash.setNetAmount(totalSales.subtract(totalReturns));
        
        closeCash = closeCashRepository.save(closeCash);
        return toDTO(closeCash);
    }
    
    @Transactional(readOnly = true)
    public CloseCashDTO getCloseCashById(Long id) {
        CloseCash closeCash = closeCashRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Close cash session not found"));
        return toDTO(closeCash);
    }
    
    @Transactional(readOnly = true)
    public List<CloseCashDTO> getPendingCloseCash() {
        return closeCashRepository.findByReconciledFalse().stream()
                .map(this::toDTO)
                .collect(Collectors.toList());
    }
    
    public void reconcileCloseCash(Long id) {
        CloseCash closeCash = closeCashRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("Close cash session not found"));
        
        if (closeCash.getClosedAt() == null) {
            throw new RuntimeException("Close cash session is not closed yet");
        }
        
        closeCash.setReconciled(true);
        closeCashRepository.save(closeCash);
    }
    
    private CloseCashDTO toDTO(CloseCash closeCash) {
        return CloseCashDTO.builder()
                .id(closeCash.getId())
                .openedAt(closeCash.getOpenedAt())
                .closedAt(closeCash.getClosedAt())
                .totalSales(closeCash.getTotalSales())
                .totalReturns(closeCash.getTotalReturns())
                .netAmount(closeCash.getNetAmount())
                .reconciled(closeCash.getReconciled())
                .build();
    }
}
