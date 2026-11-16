package com.rsm.walletflow.controller;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.rsm.walletflow.ai.rag.ingestion.DataReader;
import com.rsm.walletflow.ai.rag.ingestion.DataTransformer;
import com.rsm.walletflow.ai.rag.ingestion.DataWriter;
import com.rsm.walletflow.ai.rag.ingestion.VectorStoreWriter;
import com.rsm.walletflow.service.FinnyChatServiceImpl;
import lombok.RequiredArgsConstructor;

import java.io.IOException;
import java.util.List;

import org.springframework.ai.document.Document;
@RestController
@RequestMapping("/api/v1/finny-chat")
@RequiredArgsConstructor 
public class FinnyChatController {

    // private static final Logger log = LoggerFactory.getLogger(FinnyChatController.class);
    private final FinnyChatServiceImpl finnyChatService;
    private final DataReader dataReader;
    private final DataTransformer dataTransformer;
    private final DataWriter dataWriter;
    private final VectorStoreWriter vectorStoreWriter;


    @GetMapping("/chat")
    public ResponseEntity<String> chatWithFinny(@RequestParam("message") String userMessage, @RequestParam Long id) {
        return ResponseEntity.ok(finnyChatService.chatWithFinny(userMessage, id));
    }

    @GetMapping("/read/polices")
    public List<Document> readWalletFlowPolices() {
        try {
            return dataReader.readDocuments();
        } catch (IOException e) {
            throw new RuntimeException("Error reading wallet flow policies", e);
        }
    }
    
    @GetMapping("/transform/data")
    public List<Document> getTransformedDocuments() {
        try {
            return dataTransformer.transformDocuments(dataReader.readDocuments());
        } catch (IOException e) {
            throw new RuntimeException("Error reading wallet flow policies", e);
        }
    }

    @GetMapping("/write/data-to-local")
    public void getProcessedDocuments(@RequestParam String param) {
        try {
            dataWriter.writeDocuments(dataTransformer.transformDocuments(dataReader.readDocuments()));
        } catch (IOException e) {
            throw new RuntimeException("Error reading wallet flow policies", e);
        }
    }

    @GetMapping("/write/data-to-vector-store")
    public void getProcessedDocumentsToVectorStore(@RequestParam String param) {
        try {
            vectorStoreWriter.writeDocuments(dataTransformer.transformDocuments(dataReader.readDocuments()));
        } catch (IOException e) {
            throw new RuntimeException("Error reading wallet flow policies", e);
        }
    }
    
    
    
}
