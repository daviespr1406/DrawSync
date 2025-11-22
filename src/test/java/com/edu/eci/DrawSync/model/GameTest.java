package com.edu.eci.DrawSync.model;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class GameTest {

    private Game game;

    @BeforeEach
    void setUp() {
        game = new Game();
    }

    @Test
    void testGameCreation() {
        // Verify game is created with default values
        assertNotNull(game.getGameCode());
        assertEquals(4, game.getGameCode().length());
        assertNotNull(game.getPlayers());
        assertEquals(0, game.getPlayers().size());
        assertEquals(Game.GameStatus.LOBBY, game.getStatus());
        assertEquals(60, game.getTimeRemaining());
    }

    @Test
    void testGameCodeIsUpperCase() {
        // Verify game code is uppercase
        String gameCode = game.getGameCode();
        assertEquals(gameCode.toUpperCase(), gameCode);
    }

    @Test
    void testAddPlayer() {
        // Given
        String player1 = "Player1";
        String player2 = "Player2";

        // When
        game.addPlayer(player1);
        game.addPlayer(player2);

        // Then
        assertEquals(2, game.getPlayers().size());
        assertTrue(game.getPlayers().contains(player1));
        assertTrue(game.getPlayers().contains(player2));
    }

    @Test
    void testRemovePlayer() {
        // Given
        String player1 = "Player1";
        String player2 = "Player2";
        game.addPlayer(player1);
        game.addPlayer(player2);

        // When
        game.removePlayer(player1);

        // Then
        assertEquals(1, game.getPlayers().size());
        assertFalse(game.getPlayers().contains(player1));
        assertTrue(game.getPlayers().contains(player2));
    }

    @Test
    void testSetGameCode() {
        // Given
        String newCode = "TEST";

        // When
        game.setGameCode(newCode);

        // Then
        assertEquals(newCode, game.getGameCode());
    }

    @Test
    void testSetStatus() {
        // Given
        Game.GameStatus newStatus = Game.GameStatus.PLAYING;

        // When
        game.setStatus(newStatus);

        // Then
        assertEquals(newStatus, game.getStatus());
    }

    @Test
    void testSetTimeRemaining() {
        // Given
        int newTime = 30;

        // When
        game.setTimeRemaining(newTime);

        // Then
        assertEquals(newTime, game.getTimeRemaining());
    }

    @Test
    void testGameStatusEnum() {
        // Verify all enum values exist
        assertEquals(3, Game.GameStatus.values().length);
        assertNotNull(Game.GameStatus.valueOf("LOBBY"));
        assertNotNull(Game.GameStatus.valueOf("PLAYING"));
        assertNotNull(Game.GameStatus.valueOf("FINISHED"));
    }

    @Test
    void testMultiplePlayersManagement() {
        // Add multiple players
        for (int i = 1; i <= 5; i++) {
            game.addPlayer("Player" + i);
        }

        assertEquals(5, game.getPlayers().size());

        // Remove some players
        game.removePlayer("Player2");
        game.removePlayer("Player4");

        assertEquals(3, game.getPlayers().size());
        assertTrue(game.getPlayers().contains("Player1"));
        assertFalse(game.getPlayers().contains("Player2"));
        assertTrue(game.getPlayers().contains("Player3"));
        assertFalse(game.getPlayers().contains("Player4"));
        assertTrue(game.getPlayers().contains("Player5"));
    }

    @Test
    void testGameLifecycle() {
        // Create game in LOBBY
        assertEquals(Game.GameStatus.LOBBY, game.getStatus());

        // Add players
        game.addPlayer("Player1");
        game.addPlayer("Player2");

        // Start game
        game.setStatus(Game.GameStatus.PLAYING);
        assertEquals(Game.GameStatus.PLAYING, game.getStatus());

        // Simulate time passing
        game.setTimeRemaining(30);
        assertEquals(30, game.getTimeRemaining());

        // Finish game
        game.setStatus(Game.GameStatus.FINISHED);
        game.setTimeRemaining(0);
        assertEquals(Game.GameStatus.FINISHED, game.getStatus());
        assertEquals(0, game.getTimeRemaining());
    }

    @Test
    void testGetPlayers() {
        // Test that getPlayers returns the actual list
        game.addPlayer("Player1");
        assertEquals(1, game.getPlayers().size());
        
        // Verify it's the same list reference
        game.getPlayers().add("Player2");
        assertEquals(2, game.getPlayers().size());
    }
}
