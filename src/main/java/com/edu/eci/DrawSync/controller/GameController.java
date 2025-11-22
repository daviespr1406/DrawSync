package com.edu.eci.DrawSync.controller;

import com.edu.eci.DrawSync.model.Game;
import com.edu.eci.DrawSync.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = "*")
public class GameController {

    @Autowired
    private GameService gameService;

    @PostMapping("/create")
    public ResponseEntity<Game> createGame(@RequestBody Map<String, String> payload) {
        String creator = payload.get("player");
        Game game = gameService.createGame(creator);
        return ResponseEntity.ok(game);
    }

    @PostMapping("/join")
    public ResponseEntity<Game> joinGame(@RequestBody Map<String, String> payload) {
        String gameCode = payload.get("gameCode");
        String player = payload.get("player");
        Game game = gameService.joinGame(gameCode, player);
        if (game != null) {
            return ResponseEntity.ok(game);
        }
        return ResponseEntity.badRequest().build();
    }

    @PostMapping("/{gameCode}/start")
    public ResponseEntity<Void> startGame(@PathVariable String gameCode) {
        gameService.startGame(gameCode);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/{gameCode}")
    public ResponseEntity<Game> getGame(@PathVariable String gameCode) {
        Game game = gameService.getGame(gameCode);
        if (game != null) {
            return ResponseEntity.ok(game);
        }
        return ResponseEntity.notFound().build();
    }
}
