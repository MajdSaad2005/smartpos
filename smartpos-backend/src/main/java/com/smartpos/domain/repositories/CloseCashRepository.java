package com.smartpos.domain.repositories;

import com.smartpos.domain.entities.CloseCash;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.LocalDateTime;
import java.util.List;

public interface CloseCashRepository extends JpaRepository<CloseCash, Long> {
    List<CloseCash> findByReconciledFalse();
    List<CloseCash> findByClosedAtBetween(LocalDateTime startDate, LocalDateTime endDate);
}
