package com.edu.eci.DrawSync.controller;

import org.springframework.messaging.handler.annotation.DestinationVariable;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.stereotype.Controller;
import java.util.Map;

@Controller
public class VoiceController {

    @MessageMapping("/voice/signal/{gameCode}")
    @SendTo("/topic/{gameCode}/voice")
    public Map<String, Object> handleSignal(@DestinationVariable String gameCode, @Payload Map<String, Object> signal) {
        System.out.println("Voice signal in " + gameCode + " from peer: " + signal.get("peerId"));
        return signal;
    }
}
