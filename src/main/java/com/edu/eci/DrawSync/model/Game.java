package com.edu.eci.DrawSync.model;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;

@Getter
@Setter
@Document(collection = "games")
public class Game {
    @Id
    private String id;
    private String roomId;
    private List<String> players;
    private List<Stroke> strokes;
    private String state; // LOBBY, RUNNING, FINISHED
    private long createdAt;
    private long updatedAt;

}