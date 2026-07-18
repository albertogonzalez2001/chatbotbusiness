package com.Alberto.chatbotbusiness.controller;

import com.Alberto.chatbotbusiness.service.ChatService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public Map<String, String> chat(@RequestBody Map<String, String> request){
        String sessionId = request.get("sessionId");
        String userMessage = request.get("message");

        // Delegamos al servicio de la clase ChatService, el controller lo desconoce.
        String response = chatService.chat(sessionId, userMessage);

        return Map.of("response", response);
    }

    @GetMapping("/session")
    public Map<String,String> newSession(){
        String sessionId = java.util.UUID.randomUUID().toString();
        return Map.of("sessionId", sessionId);
    }
}

