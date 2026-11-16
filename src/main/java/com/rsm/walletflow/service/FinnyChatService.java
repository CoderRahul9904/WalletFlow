package com.rsm.walletflow.service;

import com.rsm.walletflow.dto.IngestionResponse;

public interface FinnyChatService {
    String chatWithFinny(String userMessage, Long id);
    IngestionResponse ingestData(String param);
}
