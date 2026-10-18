package com.rsm.walletflow.dto;

import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
@Getter
@Setter
@Builder
public class CreateUserRequest {
    private String name;
    private String email;
    private String password;
    private String phone;
}
