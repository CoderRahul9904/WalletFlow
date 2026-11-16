package com.rsm.walletflow.ai.rag.ingestion;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.writer.FileDocumentWriter;
import org.springframework.stereotype.Service;

@Service 
public class DataWriter {
    
    public void writeDocuments(List<Document> documents) {
        FileDocumentWriter writer = new FileDocumentWriter("file:./docs/knowledge-base/internal-policies/processed");
        writer.accept(documents);
    }
}

