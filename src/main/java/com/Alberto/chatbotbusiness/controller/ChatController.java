package com.Alberto.chatbotbusiness.controller;


import com.Alberto.chatbotbusiness.model.Message;
import com.Alberto.chatbotbusiness.repository.MessageRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import java.util.Map;

@RestController
@RequestMapping("/api/chat")
@RequiredArgsConstructor
public class ChatController {

    private final MessageRepository messageRepository;

    @PostMapping
    public Map<String, String> chat(@RequestBody Map<String, String> request) {
        String sessionId = request.get("sessionId");
        String userMessage = request.get("message");

        //Guardar el mensaje del usuario
        Message userMsg = new Message();
        userMsg.setSessionId(sessionId);
        userMsg.setRole(Message.Role.USER);
        userMsg.setContent(userMessage);
        messageRepository.save(userMsg);

        //Respuesta hardcodeada provisional
        String response = "Hola, soy el asistente virtual. Aun me estoy configurando";

        //Guradar la respuesta del asistente (IA)
        Message assistantMsg = new Message();
        assistantMsg.setSessionId(sessionId);
        assistantMsg.setRole(Message.Role.ASSISTANT);
        assistantMsg.setContent(response);
        messageRepository.save(assistantMsg);

        return Map.of("response", response);

    }

}
