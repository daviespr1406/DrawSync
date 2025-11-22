package com.edu.eci.DrawSync.controller;

public class Message {
    private String type; // "chat" o "draw"
    private String user;
    private String content;

    private Double x0;
    private Double y0;
    private Double x1;
    private Double y1;

    public Message() {}

    public Message(String type, String user, String content,
                   Double x0, Double y0, Double x1, Double y1) {
        this.type = type;
        this.user = user;
        this.content = content;
        this.x0 = x0;
        this.y0 = y0;
        this.x1 = x1;
        this.y1 = y1;
    }

    public String getType() { return type; }
    public void setType(String type) { this.type = type; }

    public String getUser() { return user; }
    public void setUser(String user) { this.user = user; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Double getX0() { return x0; }
    public void setX0(Double x0) { this.x0 = x0; }

    public Double getY0() { return y0; }
    public void setY0(Double y0) { this.y0 = y0; }

    public Double getX1() { return x1; }
    public void setX1(Double x1) { this.x1 = x1; }

    public Double getY1() { return y1; }
    public void setY1(Double y1) { this.y1 = y1; }
}

