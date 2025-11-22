package com.edu.eci.DrawSync.model;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class Game {
    private String gameCode;
    private List<String> players;
    private GameStatus status;
    private int timeRemaining; // in seconds

    public enum GameStatus {
        LOBBY,
        PLAYING,
        FINISHED
    }

    public Game() {
        this.gameCode = UUID.randomUUID().toString().substring(0, 4).toUpperCase();
        this.players = new ArrayList<>();
        this.status = GameStatus.LOBBY;
        this.timeRemaining = 60; // Default 60 seconds
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
    }

    public void removePlayer(String player) {
        this.players.remove(player);
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
}