package com.edu.eci.DrawSync.repository;

import com.edu.eci.DrawSync.model.Game;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.data.mongodb.repository.Query;

import java.util.List;

public interface GameRepository extends MongoRepository<Game, String> {
    // Find games where a player participated, ordered by creation date descending
    @Query("{ 'players': ?0 }")
    List<Game> findByPlayersContaining(String player, Pageable pageable);

    @Query("{ 'players': ?0, 'status': 'FINISHED' }")
    List<Game> findByPlayersContainingAndStatus(String player, String status, Pageable pageable);
}