package com.rsm.walletflow.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.memory.ChatMemory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;


@Service  
public class FinnyChatServiceImpl implements FinnyChatService {

    private final ChatClient finnyOpenAIChatClient;

    public FinnyChatServiceImpl(@Qualifier("finnyOpenAIChatClient") ChatClient finnyOpenAIChatClient) {
        this.finnyOpenAIChatClient = finnyOpenAIChatClient;
    }
    
    @Override
    public String chatWithFinny(String userMessage, Long id) {
       return finnyOpenAIChatClient.prompt(userMessage).advisors(a -> a.param(ChatMemory.CONVERSATION_ID, id)).call().content();
    }
}
