package com.smartpos.domain.repositories;

import com.smartpos.domain.entities.Discount;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface DiscountRepository extends JpaRepository<Discount, Long> {
    List<Discount> findByActiveTrue();
    List<Discount> findByActiveTrueAndValidFromBeforeAndValidUntilAfter(LocalDateTime validFrom, LocalDateTime validUntil);
    List<Discount> findByActiveTrueAndApplicableOn(Discount.ApplicableOn applicableOn);
    List<Discount> findByActiveTrueAndApplicableProductId(Long productId);
}
