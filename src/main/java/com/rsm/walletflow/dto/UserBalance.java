package com.rsm.walletflow.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserBalance {
    private Long id;
    private BigDecimal balance;
    private String currency;
}
