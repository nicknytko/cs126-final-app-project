package edu.illinois.finalproject;

/**
 * Created by Nicolas Nytko on 12/6/17.
 */

public class ChatRoom {
    private String name;
    private String icon;
    private ChatMessage lastMessage;

    ChatRoom() {
        name = null;
        icon = null;
        lastMessage = null;
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
}
