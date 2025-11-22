package com.edu.eci.DrawSync.service;

import com.edu.eci.DrawSync.model.Game;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class GameServiceTest {

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private GameService gameService;

    @BeforeEach
    void setUp() throws Exception {
        // Use reflection to set the private messagingTemplate field
        java.lang.reflect.Field field = GameService.class.getDeclaredField("messagingTemplate");
        field.setAccessible(true);
        field.set(gameService, messagingTemplate);
    }

    @Test
    void testCreateGame() {
        // Given
        String creator = "Player1";

        // When
        Game game = gameService.createGame(creator);

        // Then
        assertNotNull(game);
        assertNotNull(game.getGameCode());
        assertEquals(4, game.getGameCode().length());
        assertTrue(game.getPlayers().contains(creator));
        assertEquals(Game.GameStatus.LOBBY, game.getStatus());
        assertEquals(60, game.getTimeRemaining());
    }

    @Test
    void testJoinGame_Success() {
        // Given
        String creator = "Player1";
        String joiner = "Player2";
        Game game = gameService.createGame(creator);
        String gameCode = game.getGameCode();

        // When
        Game joinedGame = gameService.joinGame(gameCode, joiner);

        // Then
        assertNotNull(joinedGame);
        assertEquals(2, joinedGame.getPlayers().size());
        assertTrue(joinedGame.getPlayers().contains(joiner));
        assertEquals(Game.GameStatus.LOBBY, joinedGame.getStatus());
    }

    @Test
    void testJoinGame_NonexistentGame() {
        // Given
        String nonexistentCode = "XXXX";
        String player = "Player1";

        // When
        Game result = gameService.joinGame(nonexistentCode, player);

        // Then
        assertNull(result);
    }

    @Test
    void testJoinGame_FinishedGame() {
        // Given
        String creator = "Player1";
        Game game = gameService.createGame(creator);
        String gameCode = game.getGameCode();
        
        // Manually set game to FINISHED
        game.setStatus(Game.GameStatus.FINISHED);

        // When
        Game result = gameService.joinGame(gameCode, "Player2");

        // Then
        assertNull(result);
    }

    @Test
    void testJoinGame_PlayingGame() {
        // Given
        String creator = "Player1";
        Game game = gameService.createGame(creator);
        String gameCode = game.getGameCode();
        
        // Start the game
        gameService.startGame(gameCode);

        // When
        Game result = gameService.joinGame(gameCode, "Player2");

        // Then
        assertNotNull(result);
        assertEquals(2, result.getPlayers().size());
        assertTrue(result.getPlayers().contains("Player2"));
    }

    @Test
    void testStartGame_Success() throws InterruptedException {
        // Given
        String creator = "Player1";
        Game game = gameService.createGame(creator);
        String gameCode = game.getGameCode();

        // When
        gameService.startGame(gameCode);

        // Then
        assertEquals(Game.GameStatus.PLAYING, game.getStatus());
        
        // Verify timer started (wait a bit and check messaging template was called)
        Thread.sleep(1100); // Wait for at least one timer tick
        verify(messagingTemplate, atLeastOnce()).convertAndSend(
            eq("/topic/" + gameCode + "/timer"),
            anyInt()
        );
    }

    @Test
    void testStartGame_AlreadyPlaying() {
        // Given
        String creator = "Player1";
        Game game = gameService.createGame(creator);
        String gameCode = game.getGameCode();
        
        // Start game first time
        gameService.startGame(gameCode);
        int firstTimeRemaining = game.getTimeRemaining();

        // When - try to start again
        gameService.startGame(gameCode);

        // Then - should not restart (status already PLAYING)
        assertEquals(Game.GameStatus.PLAYING, game.getStatus());
    }

    @Test
    void testStartGame_NonexistentGame() {
        // Given
        String nonexistentCode = "XXXX";

        // When/Then - should not throw exception
        assertDoesNotThrow(() -> gameService.startGame(nonexistentCode));
    }

    @Test
    void testGetGame_Success() {
        // Given
        String creator = "Player1";
        Game game = gameService.createGame(creator);
        String gameCode = game.getGameCode();

        // When
        Game retrievedGame = gameService.getGame(gameCode);

        // Then
        assertNotNull(retrievedGame);
        assertEquals(gameCode, retrievedGame.getGameCode());
        assertEquals(creator, retrievedGame.getPlayers().get(0));
    }

    @Test
    void testGetGame_NonexistentGame() {
        // Given
        String nonexistentCode = "XXXX";

        // When
        Game result = gameService.getGame(nonexistentCode);

        // Then
        assertNull(result);
    }

    @Test
    void testMultipleGamesIndependence() {
        // Given
        String creator1 = "Player1";
        String creator2 = "Player2";

        // When
        Game game1 = gameService.createGame(creator1);
        Game game2 = gameService.createGame(creator2);

        // Then
        assertNotEquals(game1.getGameCode(), game2.getGameCode());
        assertEquals(1, game1.getPlayers().size());
        assertEquals(1, game2.getPlayers().size());
        assertTrue(game1.getPlayers().contains(creator1));
        assertTrue(game2.getPlayers().contains(creator2));
    }

    @Test
    void testGameCodeUniqueness() {
        // Given/When - create multiple games
        Game game1 = gameService.createGame("Player1");
        Game game2 = gameService.createGame("Player2");
        Game game3 = gameService.createGame("Player3");

        // Then - all game codes should be unique
        assertNotEquals(game1.getGameCode(), game2.getGameCode());
        assertNotEquals(game1.getGameCode(), game3.getGameCode());
        assertNotEquals(game2.getGameCode(), game3.getGameCode());
    }
}
