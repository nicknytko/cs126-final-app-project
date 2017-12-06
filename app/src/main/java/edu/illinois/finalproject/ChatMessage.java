package edu.illinois.finalproject;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

/**
 * Created by Nicolas Nytko on 11/28/17.
 */

@IgnoreExtraProperties
public class ChatMessage {
    private String message;
    private long timestamp;
    private String user;

    public ChatMessage() {
        message = null;
        timestamp = 0;
        user = null;
    }

    public ChatMessage(String message, String user) {
        this.message = message;
        this.timestamp = new Date().getTime();
        this.user = user;
    }

    public ChatMessage(String message, long timestamp, String user) {
        this.message = message;
        this.timestamp = timestamp;
        this.user = user;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUserId() {
        return user;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setUser(String user) {
        this.user = user;
    }
}
