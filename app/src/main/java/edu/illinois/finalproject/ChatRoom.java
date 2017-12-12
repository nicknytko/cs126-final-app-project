package edu.illinois.finalproject;

import com.google.firebase.database.Exclude;

import java.util.Map;

/**
 * Created by Nicolas Nytko on 12/6/17.
 */

public class ChatRoom {
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
        this.type = ChatApi.Type.valueOf("CHAT_" + type.toUpperCase());
    }

    @Exclude
    public ChatApi.Type getTypeEnum() {
        return type;
    }

    @Exclude
    public void setTypeEnum(ChatApi.Type type) {
        this.type = type;
    }
}
