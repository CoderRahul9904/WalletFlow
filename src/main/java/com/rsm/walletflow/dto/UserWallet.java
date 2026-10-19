package com.rsm.walletflow.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserWallet {
    private String userName;
    private String email;
    private Long walletId;
    private BigDecimal balance;
    private String currency;
}
