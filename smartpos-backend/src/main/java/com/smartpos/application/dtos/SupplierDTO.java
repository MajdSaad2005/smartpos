package com.smartpos.application.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SupplierDTO {
    private Long id;
    private String code;
    private String name;
    private String address;
    private String email;
    private String phone;
    private String taxId;
    private Boolean active;
}
