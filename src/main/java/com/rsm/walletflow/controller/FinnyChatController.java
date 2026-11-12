package com.rsm.walletflow.controller;

// import org.slf4j.Logger;
// import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.rsm.walletflow.service.FinnyChatServiceImpl;
import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/v1/finny-chat")
@RequiredArgsConstructor 
public class FinnyChatController {

    // private static final Logger log = LoggerFactory.getLogger(FinnyChatController.class);
    private final FinnyChatServiceImpl finnyChatService;



    @GetMapping("/chat")
    public ResponseEntity<String> chatWithFinny(@RequestParam("message") String userMessage, @RequestParam Long id) {
        return ResponseEntity.ok(finnyChatService.chatWithFinny(userMessage, id));
    }
}
