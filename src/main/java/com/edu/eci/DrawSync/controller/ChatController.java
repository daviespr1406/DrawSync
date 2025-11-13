package com.edu.eci.DrawSync.controller;

import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class ChatController {

    @MessageMapping("/chat")
    @SendTo("/topic/chat")
    public Message handleChat(Message message) {
        System.out.println("Chat message from " + message.getUser() + ": " + message.getContent());
        return message; // Se envía a todos los clientes conectados
    }
}
