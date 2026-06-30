package com.Alberto.chatbotbusiness.model;


import jakarta.persistence.*;
import lombok.Data;
import java.time.LocalDateTime;

@Entity
@Data
public class Message {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String sessionId;

    @Enumerated(EnumType.STRING)
    private Role role;

    @Column(columnDefinition = "TEXT") //Eliminamos el límite de caracteres del mensaje
    private String content;

    private LocalDateTime createdAt = LocalDateTime.now(); //Capturamos el momento fechado de la consulta

    public enum Role { USER, ASSISTANT }
}
