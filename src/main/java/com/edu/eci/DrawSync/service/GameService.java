package com.edu.eci.DrawSync.service;

import com.edu.eci.DrawSync.model.Game;
import com.edu.eci.DrawSync.model.GameStatus;
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

    @Autowired
    private OpenAIService openAIService;

    public Game createGame(String creator) {
        Game game = new Game();
        game.addPlayer(creator);
        games.put(game.getGameCode(), game);
        return game;
    }

    public Game joinGame(String gameCode, String player) {
        Game game = games.get(gameCode);
        if (game != null && game.getStatus() != GameStatus.FINISHED) {
            game.addPlayer(player);
            System.out.println(
                    "Player " + player + " joined game " + gameCode + ". Total players: " + game.getPlayers().size());
            return game;
        }
        System.out.println("Failed to join game " + gameCode + ". Game not found or finished.");
        return null;
    }

    public void startGame(String gameCode) {
        Game game = games.get(gameCode);
        if (game != null && game.getStatus() == GameStatus.LOBBY) {
            game.setStatus(GameStatus.PLAYING);

            // Generate random word
            String word = openAIService.getRandomWord();
            game.setCurrentWord(word);
            System.out.println("Generated word for game " + gameCode + ": " + word);
            messagingTemplate.convertAndSend("/topic/" + gameCode + "/word", word);

            System.out.println("=== STARTING GAME " + gameCode + " ===");
            startTimer(game);
        }
    }

    public void submitDrawing(String gameCode, String player, String base64Image) {
        Game game = games.get(gameCode);
        if (game != null) {
            game.addDrawing(player, base64Image);
            System.out.println(
                    "Received drawing from " + player + " for game " + gameCode + ". Size: " + base64Image.length());
        } else {
            System.err.println("Received drawing for unknown game: " + gameCode);
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
                game.setStatus(GameStatus.FINISHED);
                System.out.println("Game " + gameCode + " FINISHED");
                messagingTemplate.convertAndSend("/topic/" + gameCode + "/timer", 0);

                // Cancel this timer task
                ScheduledFuture<?> task = timerTasks.remove(gameCode);
                if (task != null) {
                    task.cancel(false);
                }

                // Schedule evaluation after 10 seconds to allow for submissions
                System.out.println("Scheduling evaluation in 10 seconds...");
                scheduler.schedule(() -> evaluateAndBroadcastResults(game), 10, TimeUnit.SECONDS);
            }
        }, 0, 1, TimeUnit.SECONDS);

        timerTasks.put(gameCode, timerTask);
    }

    private void evaluateAndBroadcastResults(Game game) {
        System.out.println("Evaluating results for game " + game.getGameCode());
        String word = game.getCurrentWord();
        Map<String, Integer> scores = game.getScores();

        System.out.println("Drawings to evaluate: " + game.getDrawings().size());

        game.getDrawings().forEach((player, drawing) -> {
            System.out.println("Evaluating drawing for " + player + "...");
            int score = openAIService.evaluateDrawing(drawing, word);
            scores.put(player, score);
            System.out.println("Score for " + player + ": " + score);
        });

        // Broadcast scores
        messagingTemplate.convertAndSend("/topic/" + game.getGameCode() + "/scores", scores);
    }

    public Game getGame(String gameCode) {
        return games.get(gameCode);
    }
}
