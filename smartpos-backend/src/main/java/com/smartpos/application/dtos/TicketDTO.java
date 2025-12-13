package com.smartpos.application.dtos;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class TicketDTO {
    private Long id;
    private String number;
    private String type;
    private LocalDateTime createdAt;
    private BigDecimal subtotal;
    private BigDecimal taxAmount;
    private BigDecimal total;
    private String status;
    private Long customerId;
    private String customerName;
    private Long closeCashId;
    private List<TicketLineDTO> lines;
}
