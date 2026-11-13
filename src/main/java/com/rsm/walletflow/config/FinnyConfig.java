package com.rsm.walletflow.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.client.advisor.SimpleLoggerAdvisor;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.chat.memory.repository.jdbc.JdbcChatMemoryRepository;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class FinnyConfig {

    private final JdbcChatMemoryRepository chatMemoryRepository;
    

    @Bean
    public ChatMemory finnyChatMemory(){
        return MessageWindowChatMemory.builder().chatMemoryRepository(chatMemoryRepository).build();
    }
    
    @Bean(name = "finnyOpenAIChatClient")
    public ChatClient getOpenAIFinnyChatClient(OpenAiChatModel openAiChatModel, ChatMemory finnyChatMemory) {
        return ChatClient
            .builder(openAiChatModel)
            .defaultAdvisors(new SimpleLoggerAdvisor(), MessageChatMemoryAdvisor.builder(finnyChatMemory).build())
            .defaultOptions(OpenAiChatOptions.builder()
                .model("openai.gpt-5-mini")
                .temperature(0.7)
                .maxTokens(1000))
            .build();
    }
}
