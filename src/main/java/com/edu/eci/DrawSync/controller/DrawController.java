package com.edu.eci.DrawSync.controller;

import com.edu.eci.DrawSync.model.Stroke;
import com.edu.eci.DrawSync.repository.StrokeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;

@Controller
public class DrawController {
    @Autowired
    private StrokeRepository strokeRepository;

    @MessageMapping("/draw")
    @SendTo("/topic/draw")
    public Message handleDraw(Message message) {
        System.out.println("Received: " + message.getContent());
        return message;
    }
    @MessageMapping("/stroke")
    @SendTo("/topic/strokes")
    public Stroke handleStroke(Stroke stroke) {
        if (stroke.getTimestamp() == 0) stroke.setTimestamp(System.currentTimeMillis());
        strokeRepository.save(stroke);
        return stroke;
    }
}

