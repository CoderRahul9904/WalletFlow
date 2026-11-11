package com.rsm.walletflow.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;



@Configuration 
public class FinnyConfig {
    
    @Bean(name = "finnyOpenAIChatClient")
    public ChatClient getOpenAIFinnyChatClient(OpenAiChatModel openAiChatModel) {
        return ChatClient
            .builder(openAiChatModel)
            .build();
    }

}
