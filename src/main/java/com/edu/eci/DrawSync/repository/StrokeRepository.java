package com.edu.eci.DrawSync.repository;

import com.edu.eci.DrawSync.model.Stroke;
import org.springframework.data.mongodb.repository.MongoRepository;
import java.util.List;

public interface StrokeRepository extends MongoRepository<Stroke, String> {
    List<Stroke> findBySessionIdOrderByTimestampAsc(String sessionId);
}