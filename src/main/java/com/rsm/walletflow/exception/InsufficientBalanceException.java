package com.rsm.walletflow.exception;

/** Thrown when a withdrawal or transfer exceeds the available balance. Mapped to HTTP 400. */
public class InsufficientBalanceException extends RuntimeException {
    public InsufficientBalanceException(String message) {
        super(message);
    }
}
