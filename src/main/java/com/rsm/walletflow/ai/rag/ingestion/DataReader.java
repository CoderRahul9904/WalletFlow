package com.rsm.walletflow.ai.rag.ingestion;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.ai.document.Document;
import org.springframework.ai.reader.markdown.MarkdownDocumentReader;
import org.springframework.ai.reader.markdown.config.MarkdownDocumentReaderConfig;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.core.io.support.ResourcePatternResolver;
import org.springframework.stereotype.Service;

@Service
public class DataReader {
 
    public List<Document> readDocuments() throws IOException {
 
        ResourcePatternResolver resolver =
                new PathMatchingResourcePatternResolver();
 
        Resource[] resources = resolver.getResources(
            "file:./docs/knowledge-base/internal-policies/*.md"
        );
 
        List<Document> documents = new ArrayList<>();
 
        MarkdownDocumentReaderConfig config =
                MarkdownDocumentReaderConfig.builder()
                        .withIncludeCodeBlock(true)
                        .withIncludeBlockquote(true)
                        .build();
 
        for (Resource resource : resources) {
 
            MarkdownDocumentReader reader =
                    new MarkdownDocumentReader(resource, config);
 
            List<Document> extracted = reader.read();
 
            documents.addAll(extracted);
        }
        System.out.println("Documents read: " + documents.size());
        return documents;
    }
}
