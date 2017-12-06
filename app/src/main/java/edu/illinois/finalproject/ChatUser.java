package edu.illinois.finalproject;

import com.google.firebase.database.IgnoreExtraProperties;

import java.util.Map;

/**
 * Created by Nicolas Nytko on 11/29/17.
 */

@IgnoreExtraProperties
public class ChatUser {
    private Map<String, Boolean> chats;
    private String email;
    private String name;
    private String publicKey;
    private String profilePicture;

    ChatUser() {
        chats = null;
        email = null;
        name = null;
        publicKey = null;
        profilePicture = null;
    }

    ChatUser(String email, String name) {
        this.email = email;
        this.name = name;
        chats = null;
        publicKey = null;
    }

    public Map<String, Boolean> getChats() {
        return chats;
    }

    public void setChats(Map<String, Boolean> chats) {
        this.chats = chats;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPublicKey() {
        return publicKey;
    }

    public void setPublicKey(String publicKey) {
        this.publicKey = publicKey;
    }

    public String getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(String profilePicture) {
        this.profilePicture = profilePicture;
    }
}
