package com.chatClientTool.chatClientTool.service;

import com.chatClientTool.chatClientTool.tool.Weathertool;
import com.chatClientTool.chatClientTool.tool.simpleDatetimetool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class chatService {

    private static final Logger logger = LoggerFactory.getLogger(chatService.class);

    private final ChatClient chatClient;

    private final Weathertool weathertool;

    public chatService(ChatClient chatClient, Weathertool weathertool) {
        this.chatClient = chatClient;
        this.weathertool=weathertool;
    }

    public String chat(String message) {

        logger.info("Processing message: {}", message);

        if (message == null || message.trim().isEmpty()) {
            logger.warn("Message is empty or null");
            return "Message cannot be empty";
        }

        try {
            String content = chatClient.prompt()
                    .tools(new simpleDatetimetool(),weathertool)
                    .user(message)
                    .call()
                    .content();
            logger.info("AI response: {}", content);
            return content;
        } catch (Exception e) {
            logger.error("Error processing message: {}", e.getMessage(), e);
            return "Error: " + e.getMessage();
        }
    }
}
