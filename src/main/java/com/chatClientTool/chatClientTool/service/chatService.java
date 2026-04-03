package com.chatClientTool.chatClientTool.service;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.stereotype.Service;

@Service
public class chatService {

    private final ChatClient chatClient;

    public chatService(ChatClient chatClient) {
        this.chatClient = chatClient;
    }

    public String chat(String message) {

        if (message == null || message.trim().isEmpty()) {
            return "Message cannot be empty";
        }

        try {
            return chatClient.prompt()
                    .user(message)
                    .call()
                    .content();
        } catch (Exception e) {
            e.printStackTrace();
            return "Error: " + e.getMessage();
        }
    }
}
