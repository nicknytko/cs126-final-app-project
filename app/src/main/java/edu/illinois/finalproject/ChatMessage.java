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
    private String userId;

    public ChatMessage() {
        message = null;
        timestamp = 0;
        userId = null;
    }

    public ChatMessage(String message, String userId) {
        this.message = message;
        this.timestamp = new Date().getTime();
        this.userId = userId;
    }

    public ChatMessage(String message, long timestamp, String userId) {
        this.message = message;
        this.timestamp = timestamp;
        this.userId = userId;
    }

    public String getMessage() {
        return message;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public String getUserId() {
        return userId;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}
