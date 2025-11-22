package com.edu.eci.DrawSync.service;

import com.edu.eci.DrawSync.model.Game;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.Map;
import java.util.concurrent.*;

@Service
public class GameService {

    private final Map<String, Game> games = new ConcurrentHashMap<>();
    private final Map<String, ScheduledFuture<?>> timerTasks = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public Game createGame(String creator) {
        Game game = new Game();
        game.addPlayer(creator);
        games.put(game.getGameCode(), game);
        return game;
    }

    public Game joinGame(String gameCode, String player) {
        Game game = games.get(gameCode);
        if (game != null && game.getStatus() != Game.GameStatus.FINISHED) {
            game.addPlayer(player);
            System.out.println("Player " + player + " joined game " + gameCode + ". Total players: " + game.getPlayers().size());
            return game;
        }
        System.out.println("Failed to join game " + gameCode + ". Game not found or finished.");
        return null;
    }

    public void startGame(String gameCode) {
        Game game = games.get(gameCode);
        if (game != null && game.getStatus() == Game.GameStatus.LOBBY) {
            game.setStatus(Game.GameStatus.PLAYING);
            System.out.println("=== STARTING GAME " + gameCode + " ===");
            startTimer(game);
        }
    }

    private void startTimer(Game game) {
        String gameCode = game.getGameCode();
        
        // Cancel any existing timer for this game
        ScheduledFuture<?> existingTask = timerTasks.get(gameCode);
        if (existingTask != null) {
            existingTask.cancel(false);
            System.out.println("Cancelled existing timer for game " + gameCode);
        }
        
        System.out.println("Starting timer for game " + gameCode + " with " + game.getTimeRemaining() + " seconds");
        
        ScheduledFuture<?> timerTask = scheduler.scheduleAtFixedRate(() -> {
            if (game.getTimeRemaining() > 0) {
                game.setTimeRemaining(game.getTimeRemaining() - 1);
                System.out.println("Game " + gameCode + " timer: " + game.getTimeRemaining() + "s");
                messagingTemplate.convertAndSend("/topic/" + gameCode + "/timer", game.getTimeRemaining());
            } else {
                game.setStatus(Game.GameStatus.FINISHED);
                System.out.println("Game " + gameCode + " FINISHED");
                messagingTemplate.convertAndSend("/topic/" + gameCode + "/timer", 0);
                
                // Cancel this timer task
                ScheduledFuture<?> task = timerTasks.remove(gameCode);
                if (task != null) {
                    task.cancel(false);
                }
            }
        }, 0, 1, TimeUnit.SECONDS);
        
        timerTasks.put(gameCode, timerTask);
    }

    public Game getGame(String gameCode) {
        return games.get(gameCode);
    }
}
