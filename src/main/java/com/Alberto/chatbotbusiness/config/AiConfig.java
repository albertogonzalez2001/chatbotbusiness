package com.Alberto.chatbotbusiness.config;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.ollama.OllamaChatModel;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class AiConfig {

    @Bean
    public ChatClient chatClient(OllamaChatModel ollamaChatModel){
        // OllamaChatModel es inyectado automaticamente por Spring AI
        return ChatClient.builder(ollamaChatModel).build();
    }
}
