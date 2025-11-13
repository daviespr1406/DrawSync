package com.edu.eci.DrawSync.controller;
import com.edu.eci.DrawSync.model.Game;
import com.edu.eci.DrawSync.repository.GameRepository;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


import java.util.List;
@RestController
@RequestMapping("/api")
public class ApiController {


    private final GameRepository gameRepository;


    public ApiController(GameRepository gameRepository) {
        this.gameRepository = gameRepository;
    }


    @GetMapping("/games")
    public List<Game> listGames() {
        return gameRepository.findAll();
    }


    @PostMapping("/games")
    public ResponseEntity<Game> createGame(@RequestBody Game game) {
        Game saved = gameRepository.save(game);
        return ResponseEntity.ok(saved);
    }
}