package com.rsm.walletflow.dto;

import java.util.UUID;

import jakarta.persistence.Id;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;

@Data
@Getter
@Builder
public class IngestionResponse {
    private UUID ingestionId;
    private String status;
    private String message;
}
