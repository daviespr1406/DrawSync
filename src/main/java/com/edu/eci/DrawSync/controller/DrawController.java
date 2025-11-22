package com.edu.eci.DrawSync.controller;

import com.edu.eci.DrawSync.model.Message;
import com.edu.eci.DrawSync.model.Stroke;
import com.edu.eci.DrawSync.repository.StrokeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class DrawController {
    @Autowired
    private StrokeRepository strokeRepository;

    @MessageMapping("/draw/{gameCode}")
    @SendTo("/topic/{gameCode}/draw")
    public Message handleDraw(@DestinationVariable String gameCode, Message message) {
        System.out.println("Received in " + gameCode + ": " + message.getContent());
        return message;
    }
    @MessageMapping("/stroke/{gameCode}")
    @SendTo("/topic/{gameCode}/strokes")
    public Stroke handleStroke(@DestinationVariable String gameCode, Stroke stroke) {
        if (stroke.getTimestamp() == 0) stroke.setTimestamp(System.currentTimeMillis());
        stroke.setSessionId(gameCode); // Use gameCode as sessionId
        strokeRepository.save(stroke);
        return stroke;
    }
}


