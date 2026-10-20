package com.rsm.walletflow.dto;

import lombok.Data;

import java.math.BigDecimal;

@Data
public class UserTransferMoneyResponse {

    private final String senderName;
    private final String receiverName;
    private final BigDecimal transferAmount;
    private final String message;

    public UserTransferMoneyResponse(String senderName, String receiverName, BigDecimal transferAmount){
        this.senderName=senderName;
        this.receiverName=receiverName;
        this.transferAmount=transferAmount;
        this.message="Dear "+ senderName + " your transfer amount "+ transferAmount + " has been successfully transferred to "+ receiverName + " wallet. Thank you for using the service.";
    }
}
