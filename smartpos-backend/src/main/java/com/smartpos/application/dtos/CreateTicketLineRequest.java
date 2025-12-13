package com.smartpos.application.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTicketLineRequest {
    private Long productId;
    private Integer quantity;
}
