package com.Alberto.chatbotbusiness.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
public class ControladorDeSalud {

    @GetMapping("/saludo")
    public String obtenerSaludo() {
        return "Hola desde mi primer endpoint en Spring Boot";
    }
}
