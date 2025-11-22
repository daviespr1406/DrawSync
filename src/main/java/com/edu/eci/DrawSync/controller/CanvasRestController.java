package com.edu.eci.DrawSync.controller;

import com.edu.eci.DrawSync.model.Stroke;
import com.edu.eci.DrawSync.repository.StrokeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/canvas")
public class CanvasRestController {

    @Autowired
    private StrokeRepository strokeRepository;

    @GetMapping("/{sessionId}/history")
    public List<Stroke> getHistory(@PathVariable String sessionId) {
        return strokeRepository.findBySessionIdOrderByTimestampAsc(sessionId);
    }
}