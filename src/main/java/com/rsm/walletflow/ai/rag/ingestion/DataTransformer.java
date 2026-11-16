package com.rsm.walletflow.ai.rag.ingestion;

import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.transformer.splitter.TokenTextSplitter;
import org.springframework.stereotype.Service;

@Service
public class DataTransformer {

    public List<Document> transformDocuments(List<Document> documents) {
        TokenTextSplitter splitter = TokenTextSplitter.builder()
                .withChunkSize(500)
                .withMinChunkSizeChars(350)
                .withMinChunkLengthToEmbed(10)
                .withMaxNumChunks(4000)
                .withKeepSeparator(true)
                .build();

        return splitter.apply(documents);
    }
}
