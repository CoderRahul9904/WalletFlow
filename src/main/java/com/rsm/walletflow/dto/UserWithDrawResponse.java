package com.rsm.walletflow.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.math.BigDecimal;

@Data
@Getter
@Setter
public class UserWithDrawResponse {
    private final String name;
    private final BigDecimal totalAmount;
    private final BigDecimal withDrawAmount;
    private final String message;

    public UserWithDrawResponse(
            String name,
            BigDecimal totalAmount,
            BigDecimal withDrawAmount) {

        this.name = name;
        this.totalAmount = totalAmount;
        this.withDrawAmount = withDrawAmount;

        this.message = "Dear " + name
                + ", your deposit of ₹" + withDrawAmount
                + " was successfully deducted from your account."
                + " Your current available balance is ₹"
                + totalAmount + ".";
    }
}
