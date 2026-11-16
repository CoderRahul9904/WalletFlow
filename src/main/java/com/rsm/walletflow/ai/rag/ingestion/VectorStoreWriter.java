package com.rsm.walletflow.ai.rag.ingestion;
 
import java.util.List;
 
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.document.Document;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.stereotype.Service;
 
@Service
public class VectorStoreWriter {
 
    private static final Logger log =
            LoggerFactory.getLogger(VectorStoreWriter.class);
 
    private final VectorStore vectorStore;
 
    public VectorStoreWriter(VectorStore vectorStore) {
        this.vectorStore = vectorStore;
    }
 
    public void writeDocuments(List<Document> documents) {
 
        if (documents == null || documents.isEmpty()) {
            log.warn("No documents available for vector storage.");
            return;
        }
 
        log.info("Storing {} documents in vector store...",
                documents.size());
 
        vectorStore.add(documents);
 
        log.info("Successfully stored {} documents.",
                documents.size());
    }
}