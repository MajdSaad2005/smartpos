package com.smartpos.domain.repositories;

import com.smartpos.domain.entities.Coupon;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface CouponRepository extends JpaRepository<Coupon, Long> {
    Optional<Coupon> findByCode(String code);
    List<Coupon> findByActiveTrue();
    List<Coupon> findByValidFromBeforeAndValidUntilAfter(LocalDateTime validFrom, LocalDateTime validUntil);
}
