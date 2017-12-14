package edu.illinois.finalproject;

import android.os.Parcel;
import android.os.Parcelable;

import com.google.firebase.database.Exclude;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Nicolas Nytko on 12/6/17.
 */

public class ChatRoom implements Parcelable {
    private String name;
    private String icon;
    private ChatMessage lastMessage;
    private Map<String, Boolean> users;
    private ChatApi.Type type;

    ChatRoom() {
        name = null;
        icon = null;
        lastMessage = null;
        users = null;
        type = null;
    }

    ChatRoom(String name, String icon, ChatMessage lastMessage) {
        this.name = name;
        this.icon = icon;
        this.lastMessage = lastMessage;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public ChatMessage getLastMessage() {
        return lastMessage;
    }

    public void setLastMessage(ChatMessage lastMessage) {
        this.lastMessage = lastMessage;
    }

    public Map<String, Boolean> getUsers() {
        return users;
    }

    public void setUsers(Map<String, Boolean> users) {
        this.users = users;
    }

    public String getType() {
        return type.toString();
    }

    public void setType(String type) {
        this.type = ChatApi.Type.valueOf(type.toUpperCase());
    }

    @Exclude
    public ChatApi.Type getTypeEnum() {
        return type;
    }

    @Exclude
    public void setTypeEnum(ChatApi.Type type) {
        this.type = type;
    }

    @Exclude
    public long getLastTimestamp() {
        if (lastMessage == null) {
            return 0;
        } else {
            return lastMessage.getTimestamp();
        }
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(this.name);
        dest.writeString(this.icon);
        dest.writeParcelable(this.lastMessage, flags);
        dest.writeInt(this.users.size());
        for (Map.Entry<String, Boolean> entry : this.users.entrySet()) {
            dest.writeString(entry.getKey());
            dest.writeValue(entry.getValue());
        }
        dest.writeInt(this.type == null ? -1 : this.type.ordinal());
    }

    protected ChatRoom(Parcel in) {
        this.name = in.readString();
        this.icon = in.readString();
        this.lastMessage = in.readParcelable(ChatMessage.class.getClassLoader());
        int usersSize = in.readInt();
        this.users = new HashMap<String, Boolean>(usersSize);
        for (int i = 0; i < usersSize; i++) {
            String key = in.readString();
            Boolean value = (Boolean) in.readValue(Boolean.class.getClassLoader());
            this.users.put(key, value);
        }
        int tmpType = in.readInt();
        this.type = tmpType == -1 ? null : ChatApi.Type.values()[tmpType];
    }

    public static final Parcelable.Creator<ChatRoom> CREATOR = new Parcelable.Creator<ChatRoom>() {
        @Override
        public ChatRoom createFromParcel(Parcel source) {
            return new ChatRoom(source);
        }

        @Override
        public ChatRoom[] newArray(int size) {
            return new ChatRoom[size];
        }
    };
}
