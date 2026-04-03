package com.chatClientTool.chatClientTool.controller;

import com.chatClientTool.chatClientTool.service.chatService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class chatController {

    private static final Logger logger = LoggerFactory.getLogger(chatController.class);

    private final chatService chatService;

    public chatController(chatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/ask")
    public String chat(@RequestParam String message) {
        logger.info("Received chat request with message: {}", message);
        String response = chatService.chat(message);
        logger.info("Responding with: {}", response);
        return response;
    }
}
