package com.rsm.walletflow.exception;

/** Thrown when a business rule conflict occurs (e.g. duplicate wallet for a user). Mapped to HTTP 409. */
public class BusinessConflictException extends RuntimeException {
    public BusinessConflictException(String message) {
        super(message);
    }
}
