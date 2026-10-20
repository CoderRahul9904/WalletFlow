package com.rsm.walletflow.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserTransferMoneyRequest {
    private Long receiverWalletId;
    private BigDecimal amount;
}
