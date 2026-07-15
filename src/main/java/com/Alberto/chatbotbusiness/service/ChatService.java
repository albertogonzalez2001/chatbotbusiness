package com.Alberto.chatbotbusiness.service;


import com.Alberto.chatbotbusiness.model.Message;
import com.Alberto.chatbotbusiness.repository.MessageRepository;

import lombok.RequiredArgsConstructor;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.messages.AssistantMessage;
import org.springframework.ai.chat.messages.SystemMessage;
import org.springframework.ai.chat.messages.UserMessage;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class ChatService {

    // Atributos de la clase (dependencias) que no varian, FINAL.
    private final ChatClient chatClient;
    private final MessageRepository messageRepository;

    private static final String SYSTEM_PROMPT = """
             Eres el asistente virtual de "Café González", una cafetería ubicada en el centro de Madrid.
                    Responde únicamente preguntas relacionadas con el negocio.
            
                    Información del negocio:
                    - Horario: Lunes a Viernes de 8:00 a 21:00, Sábados de 9:00 a 22:00, Domingos cerrado.
                    - Dirección: Calle Gran Vía 42, Madrid.
                    - Teléfono: 910 123 456
                    - Especialidades: café de especialidad, repostería artesanal, brunch los fines de semana.
                    - Reservas: solo para grupos de más de 6 personas, llamando al teléfono.
                    - WiFi: disponible para clientes.
                    - Parking: no disponible, pero hay parking público a 200 metros en la calle paralela.
            
                    Si te preguntan algo fuera del ámbito del negocio, responde amablemente que solo puedes
                    ayudar con información sobre Café González.
            
                    Responde siempre en el mismo idioma en que te hablen.
                    Sé breve, amable y directo.
            """;

    public String chat(String sessionId, String userInput){

        // Recuperacion del historial de esta sesion desde PostgreSql
        // Permitimos así que el LLM tenga contexto de mensajes anteriores
        List<Message> history = messageRepository.findBySessionIdOrderByCreatedAtAsc(sessionId);

        //Construccion de la lista de mensajes que se enviara al Modelo de IA
        List<org.springframework.ai.chat.messages.Message> aiMessages = new ArrayList<>();

        // El SystemMessage siempre va primero, definiendo así el comportamiento del bot
        aiMessages.add(new SystemMessage(SYSTEM_PROMPT));

        // Añadimos el historial previo para que el LLM recuerde el contexto
        for (Message msg : history){
            if (msg.getRole() == Message.Role.USER){
                aiMessages.add(new UserMessage(msg.getContent()));
            } else {
                aiMessages.add(new AssistantMessage(msg.getContent()));
            }
        }

        // Añadimos el mensaje actual del usuario
        aiMessages.add(new UserMessage(userInput));


        // Guardamos el mensaje del usuario en PostgreSql antes de llamar al LLM
        saveMessage(sessionId, Message.Role.USER, userInput);

        // Llamamos al LLM a traves de Spring AI
        String response = chatClient.prompt(new Prompt(aiMessages))
                .call()
                .content();

        // Guardar la respuesta del asistente en la BD
        saveMessage(sessionId, Message.Role.ASSISTANT, response);

        return response;
    }

    // Metodo para evitar repetir codigo de persistencia
    private void saveMessage(String sessionId, Message.Role role, String content){
        Message message = new Message();
        message.setSessionId(sessionId);
        message.setRole(role);
        message.setContent(content);
        messageRepository.save(message);
    }
}

//ENTIENDE ESTE CODIGO ANTES DE PASAR A LA SIGUIENTE CLASE
