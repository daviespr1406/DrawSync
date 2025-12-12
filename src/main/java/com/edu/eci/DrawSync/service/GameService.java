package com.edu.eci.DrawSync.service;

import com.edu.eci.DrawSync.model.Game;
import com.edu.eci.DrawSync.model.GameStatus;
import com.edu.eci.DrawSync.repository.GameRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@Service
public class GameService {

    private final Map<String, Game> games = new ConcurrentHashMap<>();
    private final Map<String, ScheduledFuture<?>> timerTasks = new ConcurrentHashMap<>();
    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(5);

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @Autowired
    private OpenAIService openAIService;

    @Autowired
    private GameRepository gameRepository;

    public Game createGame(String creator) {
        Game game = new Game();
        game.addPlayer(creator);
        games.put(game.getGameCode(), game);
        return game;
    }

    public Game joinGame(String gameCode, String player) {
        Game game = games.get(gameCode);
        if (game != null && game.getStatus() != GameStatus.FINISHED) {
            // Check if game is full
            if (game.getPlayers().size() >= game.getMaxPlayers()) {
                System.out.println(
                        "Cannot join game " + gameCode + ". Game is full (" + game.getMaxPlayers() + " players max)");
                return null;
            }

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

        try {
            game.getDrawings().forEach((player, drawing) -> {
                try {
                    System.out.println("Evaluating drawing for " + player + "...");
                    int score = openAIService.evaluateDrawing(drawing, word);
                    scores.put(player, score);
                    System.out.println("Score for " + player + ": " + score);
                } catch (Exception e) {
                    System.err.println("Error evaluating drawing for " + player + ": " + e.getMessage());
                    scores.put(player, 0); // Default score on error
                }
            });
        } catch (Exception e) {
            System.err.println("Error in evaluation loop: " + e.getMessage());
        }

        // Determine winner (player with highest score)
        String winner = scores.entrySet().stream()
                .max(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
        game.setWinner(winner);
        System.out.println("Winner: " + winner);

        // Save finished game to MongoDB
        try {
            gameRepository.save(game);
            System.out.println("Game " + game.getGameCode() + " saved to database");
        } catch (Exception e) {
            System.err.println("Error saving game to database: " + e.getMessage());
        }

        // Broadcast scores
        messagingTemplate.convertAndSend("/topic/" + game.getGameCode() + "/scores", scores);
    }

    public Game getGame(String gameCode) {
        return games.get(gameCode);
    }

    /**
     * Get the last 3 games played by a specific player
     */
    public List<Game> getRecentGames(String player) {
        System.out.println("🔍 Fetching recent FINISHED games for player: " + player);
        PageRequest pageRequest = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Game> games = gameRepository.findByPlayersContainingAndStatus(player, "FINISHED", pageRequest);
        System.out.println("📄 Found " + games.size() + " games for " + player);
        return games;

    }

    /**
     * Get all available games (in lobby status)
     */
    public List<Game> getAvailableGames() {
        return games.values().stream()
                .filter(game -> game.getStatus() == GameStatus.LOBBY)
                .collect(Collectors.toList());
    }

    /**
     * Remove a player from a game (before it starts)
     */
    public Game leaveGame(String gameCode, String player) {
        Game game = games.get(gameCode);
        if (game != null && game.getStatus() == GameStatus.LOBBY) {

            System.out.println("⚠️ DEBUG LEAVE - Game: " + gameCode + ", Players: " + game.getPlayers()
                    + ", Requesting: " + player);
            boolean isLast = game.getPlayers().size() == 1;
            boolean contains = game.getPlayers().contains(player);
            System.out.println("⚠️ DEBUG LEAVE - isLast: " + isLast + ", contains: " + contains);

            // Check if this is the last player
            if (isLast && contains) {
                // Determine it as aborted but DON'T remove the player so it shows in history
                game.setStatus(GameStatus.ABORTED);
                game.setWinner("Abandoned");

                try {
                    gameRepository.save(game);
                    System.out.println("Game " + gameCode + " saved to DB (abandoned by last player " + player + ")");
                } catch (Exception e) {
                    System.err.println("Error saving abandoned game: " + e.getMessage());
                }

                games.remove(gameCode);
                System.out.println("Game " + gameCode + " removed (no players left)");
                return null;
            }

            game.removePlayer(player);
            System.out.println("Player " + player + " left game " + gameCode);

            // Should not happen due to check above, but for safety
            if (game.getPlayers().isEmpty()) {
                games.remove(gameCode);
                return null;
            }

            // Broadcast updated player list
            messagingTemplate.convertAndSend("/topic/" + gameCode + "/players", game.getPlayers());
            return game;
        }
        return null;
    }

    /**
     * Abort a game (creator only) - removes game and kicks all players
     */
    public boolean abortGame(String gameCode) {
        Game game = games.remove(gameCode);
        if (game != null) {
            // Cancel any running timer
            ScheduledFuture<?> task = timerTasks.remove(gameCode);
            if (task != null) {
                task.cancel(false);
            }

            // Broadcast abort message to all players
            messagingTemplate.convertAndSend("/topic/" + gameCode + "/abort", "Game aborted by creator");

            // Save aborted game
            game.setStatus(GameStatus.ABORTED);
            game.setWinner("Aborted");
            try {
                gameRepository.save(game);
                System.out.println("Game " + gameCode + " aborted and saved to database");
            } catch (Exception e) {
                System.err.println("Error saving aborted game: " + e.getMessage());
            }

            System.out.println("Game " + gameCode + " aborted");
            return true;
        }
        return false;
    }
}
