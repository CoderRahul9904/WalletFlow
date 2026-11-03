package com.rsm.walletflow.exception;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

/** Consistent error response body returned by {@link GlobalExceptionHandler}. */
@Data
@AllArgsConstructor
public class ErrorResponse {
    private LocalDateTime timestamp;
    private int status;
    private String error;
    private String message;
    private String path;
}
