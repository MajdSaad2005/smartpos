package com.smartpos.domain.repositories;

import com.smartpos.domain.entities.PurchaseOrder;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface PurchaseOrderRepository extends JpaRepository<PurchaseOrder, Long> {
    
    Optional<PurchaseOrder> findByOrderNumber(String orderNumber);
    
    List<PurchaseOrder> findBySupplierId(Long supplierId);
    
    List<PurchaseOrder> findByStatus(PurchaseOrder.OrderStatus status);
    
    @Query("SELECT po FROM PurchaseOrder po WHERE po.orderDate BETWEEN :startDate AND :endDate")
    List<PurchaseOrder> findByDateRange(LocalDateTime startDate, LocalDateTime endDate);
    
    @Query("SELECT po FROM PurchaseOrder po WHERE po.supplier.id = :supplierId AND po.orderDate BETWEEN :startDate AND :endDate")
    List<PurchaseOrder> findBySupplierAndDateRange(Long supplierId, LocalDateTime startDate, LocalDateTime endDate);
}
