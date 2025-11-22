package com.edu.eci.DrawSync.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

import java.util.Map;

@Controller
public class ChatController {

    @MessageMapping("/chat/{gameCode}")
    @SendTo("/topic/{gameCode}/chat")
    public Map<String, Object> handleChat(@DestinationVariable String gameCode, Map<String, Object> message) {
        System.out.println("=== CHAT MESSAGE RECEIVED ===");
        System.out.println("Game Code: " + gameCode);
        System.out.println("From: " + message.get("username"));
        System.out.println("Message: " + message.get("message"));
        System.out.println("Full payload: " + message);
        System.out.println("Broadcasting to: /topic/" + gameCode + "/chat");
        System.out.println("============================");
        return message;
    }
}

