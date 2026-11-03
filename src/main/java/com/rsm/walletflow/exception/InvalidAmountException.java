package com.rsm.walletflow.exception;

/** Thrown for null / zero / negative monetary amounts. Mapped to HTTP 400. */
public class InvalidAmountException extends RuntimeException {
    public InvalidAmountException(String message) {
        super(message);
    }
}
