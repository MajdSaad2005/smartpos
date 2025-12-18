package com.smartpos.application.dtos;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreatePurchaseOrderRequest {
    private Long supplierId;
    private LocalDateTime expectedDeliveryDate;
    private String notes;
    private List<CreatePurchaseOrderLineRequest> lines;
}
