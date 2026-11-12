package com.rsm.walletflow.config;

import org.springframework.ai.chat.client.ChatClient;
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
    
    @Bean(name = "finnyOpenAIChatClient")
    public ChatClient getOpenAIFinnyChatClient(OpenAiChatModel openAiChatModel) {
        return ChatClient
            .builder(openAiChatModel)
            .defaultAdvisors(new SimpleLoggerAdvisor())
            .defaultOptions(OpenAiChatOptions.builder()
                .model("openai.gpt-5-mini")
                .temperature(0.7)
                .maxTokens(1000))
            .build();
    }

    @Bean
    public ChatMemory finnyChatMemory(){
        return MessageWindowChatMemory.builder().chatMemoryRepository(chatMemoryRepository).build();
    }

}
