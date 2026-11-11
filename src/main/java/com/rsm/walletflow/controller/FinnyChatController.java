package com.rsm.walletflow.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/finny-chat")
public class FinnyChatController {

    private static final Logger log = LoggerFactory.getLogger(FinnyChatController.class);

    private final ChatClient finnyOpenAIChatClient;

    public FinnyChatController(@Qualifier("finnyOpenAIChatClient") ChatClient finnyOpenAIChatClient) {
        this.finnyOpenAIChatClient = finnyOpenAIChatClient;
    }

    @GetMapping("/chat")
    public ResponseEntity<String> chatWithFinny(@RequestParam("message") String userMessage) {
        try {
            String finnyResponse = finnyOpenAIChatClient.prompt(userMessage).call().content();
            return ResponseEntity.ok(finnyResponse);
        } catch (Exception ex) {
            log.error("Finny chat request failed for message: {}", userMessage, ex);
            return ResponseEntity.status(HttpStatus.BAD_GATEWAY)
                    .body("Finny AI is unavailable right now. Please verify the OpenAI API key and model settings.");
        }
    }
}
