package com.smartpos.application.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class StockLevelDTO {
    private Long id;
    private Long productId;
    private String productName;
    private Integer minimumLevel;
    private Integer maximumLevel;
}
