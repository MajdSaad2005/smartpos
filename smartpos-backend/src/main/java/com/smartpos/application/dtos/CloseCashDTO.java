package com.smartpos.application.dtos;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CloseCashDTO {
    private Long id;
    private LocalDateTime openedAt;
    private LocalDateTime closedAt;
    private BigDecimal totalSales;
    private BigDecimal totalReturns;
    private BigDecimal netAmount;
    private Boolean reconciled;
}
