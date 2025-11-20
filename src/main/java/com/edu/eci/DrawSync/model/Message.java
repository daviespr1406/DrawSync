package com.edu.eci.DrawSync.model;

public class Message {
    private String user;
    private String content;
    private long timestamp;

    public Message() {
    }

    public Message(String user, String content) {
        this.user = user;
        this.content = content;
        this.timestamp = System.currentTimeMillis();
    }

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }
}
