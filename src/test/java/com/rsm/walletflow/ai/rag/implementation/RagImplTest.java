package com.rsm.walletflow.ai.rag.implementation;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.Test;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.vectorstore.VectorStore;

class RagImplTest {

    @Test
    void shouldInstantiateRagImplWithoutTranslationTransformer() {
        ChatClient chatClient = mock(ChatClient.class);
        ChatClient.Builder chatClientBuilder = mock(ChatClient.Builder.class);
        VectorStore vectorStore = mock(VectorStore.class);

        when(chatClient.mutate()).thenReturn(chatClientBuilder);
        when(chatClientBuilder.clone()).thenReturn(chatClientBuilder);

        assertDoesNotThrow(() -> new RagImpl(chatClient, vectorStore));
    }
}
