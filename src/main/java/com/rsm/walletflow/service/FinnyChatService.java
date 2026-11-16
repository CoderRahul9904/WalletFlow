package com.rsm.walletflow.service;

import java.util.List;

import com.rsm.walletflow.dto.IngestionResponse;

public interface FinnyChatService {
    String chatWithFinny(String userMessage, Long id);
    IngestionResponse ingestData(String param);
    List<IngestionResponse> getAllIngestionResponses();
}
