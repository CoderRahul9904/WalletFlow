package com.rsm.walletflow.exception;

/** Thrown when a required entity (user, wallet) cannot be located. Mapped to HTTP 404. */
public class ResourceNotFoundException extends RuntimeException {
    public ResourceNotFoundException(String message) {
        super(message);
    }
}
