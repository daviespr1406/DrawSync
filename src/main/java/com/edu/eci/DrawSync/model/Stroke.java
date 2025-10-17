package com.edu.eci.DrawSync.model;

import org.springframework.data.annotation.Id;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Stroke {
    @Id
    private String id;
    private String userId;
    private List<Point> points;
    private String color;
    private double width;
    private long timestamp;


}