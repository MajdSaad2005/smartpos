package com.smartpos.application.dtos;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDTO {
    private Long id;
    private String code;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private String address;
    private String taxId;
    private Boolean active;
}
