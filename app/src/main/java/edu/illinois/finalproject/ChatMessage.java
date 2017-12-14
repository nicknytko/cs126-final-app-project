package edu.illinois.finalproject;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Date;

/**
 * Class that represents a chatroom message in the firebase database.
 */

@IgnoreExtraProperties
public class ChatMessage implements Parcelable {
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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.message);
        dest.writeLong(this.timestamp);
        dest.writeString(this.userId);
    }

    protected ChatMessage(Parcel in) {
        this.message = in.readString();
        this.timestamp = in.readLong();
        this.userId = in.readString();
    }

    public static final Parcelable.Creator<ChatMessage> CREATOR = new Parcelable.Creator<ChatMessage>() {
        @Override
        public ChatMessage createFromParcel(Parcel source) {
            return new ChatMessage(source);
        }

        @Override
        public ChatMessage[] newArray(int size) {
            return new ChatMessage[size];
        }
    };
}
