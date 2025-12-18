package com.smartpos.domain.repositories;

import com.smartpos.domain.entities.PurchaseOrderLine;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PurchaseOrderLineRepository extends JpaRepository<PurchaseOrderLine, Long> {
    
    List<PurchaseOrderLine> findByPurchaseOrderId(Long purchaseOrderId);
    
    List<PurchaseOrderLine> findByProductId(Long productId);
}
