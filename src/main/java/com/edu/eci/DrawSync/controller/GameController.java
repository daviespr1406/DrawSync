package com.edu.eci.DrawSync.controller;

import com.edu.eci.DrawSync.model.Game;
import com.edu.eci.DrawSync.service.GameService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/games")
@CrossOrigin(origins = "*")
public class GameController {

    @Autowired
    private GameService gameService;

    @PostMapping("/create")
    public ResponseEntity<Game> createGame(@RequestBody Map<String, Object> payload) {
        String creator = (String) payload.get("creator");
        Integer maxPlayers = payload.get("maxPlayers") != null ? (Integer) payload.get("maxPlayers") : 4;
        Integer roundTime = payload.get("roundTime") != null ? (Integer) payload.get("roundTime") : 60;
        Boolean isPrivate = payload.get("isPrivate") != null ? (Boolean) payload.get("isPrivate") : false;

        System.out.println("Creating game - isPrivate: " + isPrivate);

        Game game = gameService.createGame(creator);
        game.setMaxPlayers(maxPlayers);
        game.setGameDuration(roundTime);
        game.setPrivate(isPrivate);

        System.out.println("Game created with code: " + game.getGameCode() + ", isPrivate: " + game.isPrivate());

        return ResponseEntity.ok(game);
    }

    @GetMapping("/available")
    public ResponseEntity<List<Game>> getAvailableGames() {
        List<Game> availableGames = gameService.getAvailableGames();
        return ResponseEntity.ok(availableGames);
    }

    @PostMapping("/join")
    public ResponseEntity<?> joinGame(@RequestBody Map<String, String> payload) {
        String gameCode = payload.get("gameCode");
        String player = payload.get("player");

        // Check if game exists
        Game existingGame = gameService.getGame(gameCode);
        if (existingGame == null) {
            return ResponseEntity.status(404).body("Game not found");
        }

        // Check if game is full
        if (existingGame.getPlayers().size() >= existingGame.getMaxPlayers()) {
            return ResponseEntity.status(409).body("Game is full");
        }

        Game game = gameService.joinGame(gameCode, player);
        if (game != null) {
            return ResponseEntity.ok(game);
        }
        return ResponseEntity.badRequest().body("Unable to join game");
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

    @PostMapping("/{gameCode}/submit")
    public ResponseEntity<?> submitDrawing(@PathVariable String gameCode, @RequestBody Map<String, String> payload) {
        String player = payload.get("player");
        String drawing = payload.get("drawing"); // Base64

        if (player == null || drawing == null) {
            return ResponseEntity.badRequest().body("Missing player or drawing");
        }

        gameService.submitDrawing(gameCode, player, drawing);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/recent/{player}")
    public ResponseEntity<List<Game>> getRecentGames(@PathVariable String player) {
        List<Game> recentGames = gameService.getRecentGames(player);
        return ResponseEntity.ok(recentGames);
    }

    @PostMapping("/{gameCode}/leave")
    public ResponseEntity<?> leaveGame(@PathVariable String gameCode, @RequestBody Map<String, String> payload) {
        String player = payload.get("player");
        if (player == null) {
            return ResponseEntity.badRequest().body("Missing player");
        }

        Game game = gameService.leaveGame(gameCode, player);
        if (game != null) {
            return ResponseEntity.ok(game);
        }
        return ResponseEntity.ok().body("Game ended (no players left)");
    }

    @DeleteMapping("/{gameCode}/abort")
    public ResponseEntity<?> abortGame(@PathVariable String gameCode) {
        boolean aborted = gameService.abortGame(gameCode);
        if (aborted) {
            return ResponseEntity.ok().body("Game aborted successfully");
        }
        return ResponseEntity.notFound().build();
    }
}
