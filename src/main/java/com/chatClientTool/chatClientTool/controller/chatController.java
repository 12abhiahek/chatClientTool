package com.chatClientTool.chatClientTool.controller;

import com.chatClientTool.chatClientTool.service.chatService;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/ai")
public class chatController {

    private final chatService chatService;

    public chatController(chatService chatService) {
        this.chatService = chatService;
    }

    @PostMapping("/ask")
    public String chat(@RequestParam String message) {
        return chatService.chat(message);
    }
}
