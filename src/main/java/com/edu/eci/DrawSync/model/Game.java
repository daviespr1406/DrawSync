package com.edu.eci.DrawSync.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

public class Game {
    private String gameCode;
    private List<String> players;
    private GameStatus status;
    private int timeRemaining; // in seconds
    private String currentWord;
    private java.util.Map<String, Integer> scores;
    private java.util.Map<String, String> drawings; // Player -> Base64 Image

    public Game() {
        this.gameCode = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        this.players = new ArrayList<>();
        this.status = GameStatus.LOBBY;
        this.timeRemaining = 60; // Default 60 seconds
        this.scores = new ConcurrentHashMap<>();
        this.drawings = new ConcurrentHashMap<>();
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
}
