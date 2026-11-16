package com.rsm.walletflow.controller;

import java.util.List;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rsm.walletflow.dto.IngestionResponse;
import com.rsm.walletflow.service.FinnyChatService;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/admin/knlowledge-base")
@RequiredArgsConstructor 
public class FinnyAdminController {

    private final FinnyChatService finnyChatService;
    
    @GetMapping("/ingest/data")
    public ResponseEntity<IngestionResponse> ingestData(@RequestParam String adminMessage) {
        try {
            // Call the service method to ingest data and get the response
            IngestionResponse ingestionResponse = finnyChatService.ingestData(adminMessage);
            return ResponseEntity.ok(ingestionResponse);
        } catch (Exception e) {
            // Handle exceptions and return an appropriate response
            throw new RuntimeException("Error ingesting data", e);
        }
    }

    @GetMapping("/ingestion-responses")
    public ResponseEntity<List<IngestionResponse>> getAllIngestionResponses() {
        // Implementation for fetching all ingestion responses
        return ResponseEntity.ok(finnyChatService.getAllIngestionResponses());
    }
}
