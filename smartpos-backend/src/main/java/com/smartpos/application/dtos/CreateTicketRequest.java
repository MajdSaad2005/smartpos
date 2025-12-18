package com.smartpos.application.dtos;

import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTicketRequest {
    private String type; // SALE or RETURN
    private Long customerId;
    private List<CreateTicketLineRequest> lines;
    private String couponCode; // Optional coupon code to apply
    private Long discountId; // Optional discount ID to apply
}
