package com.smartpos.application.dtos;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * DTO for Sales Report with multi-table join data
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SalesReportDTO {
    private Long ticketId;
    private String ticketNumber;
    private LocalDateTime createdAt;
    private String customerName;
    private BigDecimal total;
    private String cashierName;
    private Integer itemCount;
}
