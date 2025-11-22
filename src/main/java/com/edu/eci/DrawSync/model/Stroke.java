package com.edu.eci.DrawSync.model;
import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.List;
@Getter
@Setter
@Document(collection = "strokes")
public class Stroke {
    @Id
    private String id;
    private String sessionId;
    private String userId;
    private String color;
    private double thickness;
    private List<Point> points;
    private long timestamp;

    public Stroke() {}


}
