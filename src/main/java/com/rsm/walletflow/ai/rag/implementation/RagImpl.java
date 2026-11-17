package com.rsm.walletflow.ai.rag.implementation;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.api.Advisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.rag.advisor.RetrievalAugmentationAdvisor;
import org.springframework.ai.rag.generation.augmentation.ContextualQueryAugmenter;
import org.springframework.ai.rag.preretrieval.query.expansion.MultiQueryExpander;
import org.springframework.ai.rag.preretrieval.query.transformation.CompressionQueryTransformer;
import org.springframework.ai.rag.preretrieval.query.transformation.RewriteQueryTransformer;
import org.springframework.ai.rag.retrieval.join.ConcatenationDocumentJoiner;

import org.springframework.ai.rag.retrieval.search.VectorStoreDocumentRetriever;
import org.springframework.ai.vectorstore.VectorStore;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;


@Service
public class RagImpl implements RagService {

    private final VectorStore vectorStore;
    private final ChatClient chatClient;
    private final Advisor ragAdv;

    public RagImpl(@Qualifier("ragInternalChatClient") ChatClient chatClient, VectorStore vectorStore) {
        this.chatClient = chatClient;
        this.vectorStore = vectorStore;
        this.ragAdv = buildAdvisor();
    }

    private Advisor buildAdvisor() {
        return RetrievalAugmentationAdvisor
                .builder()
                .queryTransformers(
                        RewriteQueryTransformer
                                .builder()
                                .chatClientBuilder(chatClient.mutate().clone())
                                .build(),
                        CompressionQueryTransformer
                                .builder()
                                .chatClientBuilder(chatClient.mutate().clone())
                                .build()
                )
                .queryExpander(MultiQueryExpander
                        .builder()
                        .chatClientBuilder(chatClient.mutate().clone())
                        .build())
                .documentRetriever(VectorStoreDocumentRetriever
                        .builder()
                        .vectorStore(vectorStore)
                        .topK(4)
                        .similarityThreshold(0.73)
                        .build())
                .documentJoiner(new ConcatenationDocumentJoiner())
                .queryAugmenter(ContextualQueryAugmenter.builder().build())
                .build();
    }

    @Override
    public String generateResponse(String userMessage) {
        try {

            var request = chatClient
                    .prompt()
                    .advisors(ragAdv);

            return request
                    .user(userMessage)
                    .call()
                    .content();
        } catch (Exception e) {
            return e.getMessage();
        }
    }

}
