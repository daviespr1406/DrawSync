package com.edu.eci.DrawSync.repository;


import com.edu.eci.DrawSync.model.Game;
import org.springframework.data.mongodb.repository.MongoRepository;


public interface GameRepository extends MongoRepository<Game, String> {
}