package com.rsm.walletflow.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class UserDepositResponse {
    private final String name;
    private final BigDecimal totalAmount;
    private final BigDecimal depositAmount;
    private final String message;

    public UserDepositResponse(
            String name,
            BigDecimal totalAmount,
            BigDecimal depositAmount) {

        this.name = name;
        this.totalAmount = totalAmount;
        this.depositAmount = depositAmount;

        this.message = "Dear " + name
                + ", your deposit of ₹" + depositAmount
                + " was successfully added to your account."
                + " Your current available balance is ₹"
                + totalAmount + ".";
    }
}
