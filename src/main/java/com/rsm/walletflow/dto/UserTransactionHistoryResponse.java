package com.rsm.walletflow.dto;

import com.rsm.walletflow.config.TransactionStatus;
import com.rsm.walletflow.config.TransactionType;
import com.rsm.walletflow.config.WalletMoneyFlowType;
import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
public class UserTransactionHistoryResponse {

    private String ownerName;
    private String counterPartyName;
    private BigDecimal amount;
    private TransactionType transactionType;
    private TransactionStatus transactionStatus;
    private WalletMoneyFlowType walletMoneyFlowType;
    private LocalDateTime transactionTime;
}
