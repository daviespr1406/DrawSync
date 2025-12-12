package com.edu.eci.DrawSync.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Document(collection = "games")
public class Game {
    @Id
    private String gameCode;
    private List<String> players;
    private GameStatus status;
    private int timeRemaining; // in seconds
    private int gameDuration; // Total duration configured for the game
    private int maxPlayers; // Maximum number of players allowed

    @JsonProperty("isPrivate")
    private boolean isPrivate; // Whether the game requires a code to join
    private String currentWord;
    private java.util.Map<String, Integer> scores;
    private java.util.Map<String, String> drawings; // Player -> Base64 Image
    private String createdAt; // Timestamp when game was created
    private String winner; // Winner of the game

    public Game() {
        this.gameCode = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        this.players = new ArrayList<>();
        this.status = GameStatus.LOBBY;
        this.timeRemaining = 60; // Default 60 seconds
        this.gameDuration = 60; // Default duration
        this.maxPlayers = 4; // Default max 4 players
        this.isPrivate = false; // Default to public
        this.scores = new ConcurrentHashMap<>();
        this.drawings = new ConcurrentHashMap<>();
        this.createdAt = java.time.Instant.now().toString();
    }

    public String getGameCode() {
        return gameCode;
    }

    public void setGameCode(String gameCode) {
        this.gameCode = gameCode;
    }

    public List<String> getPlayers() {
        return players;
    }

    public void addPlayer(String player) {
        this.players.add(player);
        this.scores.putIfAbsent(player, 0);
    }

    public void removePlayer(String player) {
        this.players.remove(player);
        this.scores.remove(player);
        this.drawings.remove(player);
    }

    public GameStatus getStatus() {
        return status;
    }

    public void setStatus(GameStatus status) {
        this.status = status;
    }

    public int getTimeRemaining() {
        return timeRemaining;
    }

    public void setTimeRemaining(int timeRemaining) {
        this.timeRemaining = timeRemaining;
    }

    public String getCurrentWord() {
        return currentWord;
    }

    public void setCurrentWord(String currentWord) {
        this.currentWord = currentWord;
    }

    public java.util.Map<String, Integer> getScores() {
        return scores;
    }

    public void setScores(java.util.Map<String, Integer> scores) {
        this.scores = scores;
    }

    public java.util.Map<String, String> getDrawings() {
        return drawings;
    }

    public void setDrawings(java.util.Map<String, String> drawings) {
        this.drawings = drawings;
    }

    public void addDrawing(String player, String drawing) {
        this.drawings.put(player, drawing);
    }

    public int getGameDuration() {
        return gameDuration;
    }

    public void setGameDuration(int gameDuration) {
        this.gameDuration = gameDuration;
        this.timeRemaining = gameDuration; // Also set the initial time remaining
    }

    public int getMaxPlayers() {
        return maxPlayers;
    }

    public void setMaxPlayers(int maxPlayers) {
        this.maxPlayers = maxPlayers;
    }

    public String getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(String createdAt) {
        this.createdAt = createdAt;
    }

    public String getWinner() {
        return winner;
    }

    public void setWinner(String winner) {
        this.winner = winner;
    }

    public boolean isPrivate() {
        return isPrivate;
    }

    public void setPrivate(boolean isPrivate) {
        this.isPrivate = isPrivate;
    }
}
