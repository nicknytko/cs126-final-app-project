package edu.illinois.finalproject;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.messaging.FirebaseMessaging;

import java.util.Map;

public class ChatApi {
    private static FirebaseDatabase database = FirebaseDatabase.getInstance();
    private static DatabaseReference dbRef = database.getReference();
    private static final String CHATS_DATABASE_PATH = "chats";
    private static final String MESSAGES_DATABASE_PATH = "messages";
    private static final String USERS_DATABASE_PATH = "users";

    /**
     * The types of chat that a room can be.  Either a group chat or a 1-on-1 user chat.
     */
    public enum Type {
        GROUP,
        ONE_ON_ONE;

        @Override
        public String toString() {
            switch (this) {
                case GROUP:
                    return "group";
                case ONE_ON_ONE:
                    return "one_on_one";
                default:
                    return "unknown";
            }
        }
    }

    /**
     * Create a chat room object and return its new key.
     *
     * @param name    Name of the group chat.  Can be null if one on one chat.
     * @param userIds User ids that will be in the chat when it is created.
     * @param type    Type of chat room this will be.  Group chat or one on one.
     * @return The id of the chat room that was created.
     */
    public static String createChat(String name, String[] userIds, Type type) {
        DatabaseReference newChat =
                dbRef.child(CHATS_DATABASE_PATH).push();

        newChat.child("type").setValue(type.toString());
        if (userIds != null) {
            for (String user : userIds) {
                addUserToChat(newChat.getKey(), user);
            }
        }
        if (name != null) {
            newChat.child("name").setValue(name);
        }

        return newChat.getKey();
    }

    /**
     * Create a chat room object and return its new key.
     *
     * @param data chat room data.
     * @return The id of the chat room that was created.
     */
    public static String createChat(ChatRoom data) {
        DatabaseReference newChat =
                dbRef.child(CHATS_DATABASE_PATH).push();
        newChat.setValue(data);
        return newChat.getKey();
    }

    /**
     * Delete a chat room from firebase.
     *
     * @param chatKey Chat key to delete from the database.
     */
    public static void deleteChat(String chatKey) {
        final String finalChatKey = chatKey;
        dbRef.child(CHATS_DATABASE_PATH)
                .child(chatKey)
                .child("users")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                         /* Remove all users from the chatroom first */
                        GenericTypeIndicator<Map<String, Boolean>> userMapType =
                                new GenericTypeIndicator<Map<String, Boolean>>() {
                                };
                        Map<String, Boolean> users = dataSnapshot.getValue(userMapType);
                        for (String user : users.keySet()) {
                            removeUserFromChat(finalChatKey, user);
                        }
                        /* Now delete the actual chatroom */
                        database.getReference()
                                .child(CHATS_DATABASE_PATH)
                                .child(finalChatKey)
                                .setValue(null);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    /**
     * Adds a user to a chat room, and adds the chat room to the user.
     *
     * @param chatKey Chat room id to add user to.
     * @param userId  User id to add to the chat room.
     */
    public static void addUserToChat(String chatKey, String userId) {
        dbRef.child(CHATS_DATABASE_PATH)
                .child(chatKey)
                .child("users")
                .child(userId)
                .setValue(true);
        dbRef.child(USERS_DATABASE_PATH)
                .child(userId)
                .child("chats")
                .child(chatKey)
                .setValue(true);
        FirebaseMessaging.getInstance().subscribeToTopic(chatKey);
    }

    /**
     * Removes a user from a chat room, and removes the chat room from the user.
     *
     * @param chatKey Chat room id to remove from.
     * @param userId  User id to remove from the chat room.
     */
    public static void removeUserFromChat(String chatKey, String userId) {
        dbRef.child(CHATS_DATABASE_PATH)
                .child(chatKey)
                .child("users")
                .child(userId)
                .setValue(null);
        dbRef.child(USERS_DATABASE_PATH)
                .child(userId)
                .child("chats")
                .child(chatKey)
                .setValue(null);
        FirebaseMessaging.getInstance().unsubscribeFromTopic(chatKey);
    }

    /**
     * Appends a message to a chatroom, automatically giving a timestamp.
     *
     * @param chatKey Chat room id to append to.
     * @param message Message data to send.
     * @return Id of the newly created message.
     */
    public static String addMessageToChat(String chatKey, ChatMessage message) {
        dbRef.child(CHATS_DATABASE_PATH)
                .child(chatKey)
                .child("lastMessage")
                .setValue(message);
        DatabaseReference newMessageRef =
                dbRef.child(MESSAGES_DATABASE_PATH)
                        .child(chatKey)
                        .push();
        newMessageRef.setValue(message);

        return newMessageRef.getKey();
    }

    /**
     * Get all messages sent in a given chat.
     *
     * @param chatKey      Chat room id to get messages from.
     * @param newDataAdded Function to run when message data changes.
     */
    public static void setMessageHandler(String chatKey, final ChildEventListener newDataAdded) {
        dbRef.child(MESSAGES_DATABASE_PATH)
                .child(chatKey)
                .addChildEventListener(newDataAdded);

        dbRef.child(MESSAGES_DATABASE_PATH)
                .child(chatKey)
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if (!dataSnapshot.exists()) {
                            newDataAdded.onChildAdded(null, null);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    /**
     * Removes a message handler from a given chat room.
     *
     * @param chatKey      Chat room id to get messages from.
     * @param dataCallback Function to run when message data changes.
     */
    public static void removeMessageHandler(String chatKey, ChildEventListener dataCallback) {
        dbRef.child(MESSAGES_DATABASE_PATH)
                .child(chatKey)
                .removeEventListener(dataCallback);
    }

    /**
     * Creates a new user object and stores it in the Firebase database.
     *
     * @param userId User id of the user, should match the one from Firebase Authentication.
     * @param name   Full name of the user.
     * @param email  Email address of the user.
     */
    public static void createUser(String userId, final String name, final String email) {
        final DatabaseReference userRef = dbRef.child(USERS_DATABASE_PATH).child(userId);
        userRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (!dataSnapshot.exists()) {
                    /* Create the user only if they do not already exist */
                    ChatUser user = new ChatUser(email, name);
                    if (FirebaseAuth.getInstance().getCurrentUser() != null &&
                            FirebaseAuth.getInstance().getCurrentUser().getPhotoUrl() != null) {
                        user.setProfilePicture(FirebaseAuth.getInstance()
                                .getCurrentUser()
                                .getPhotoUrl()
                                .toString());
                    }
                    userRef.setValue(user);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Updates a chatroom's metadata.
     *
     * @param chatId  Chatroom ID to update.
     * @param details Data to update with.
     */
    public static void updateChatDetails(String chatId, ChatRoom details) {
        dbRef.child(CHATS_DATABASE_PATH)
                .child(chatId)
                .setValue(details);
    }

    /**
     * Deletes a user object and removes it from all of their chatrooms.
     *
     * @param userKey User id to delete from firebase.
     */
    public static void deleteUser(String userKey) {
        final String finalUserKey = userKey;
        dbRef.child(USERS_DATABASE_PATH)
                .child(userKey)
                .child("chats")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                         /* Remove all chats from the user first */
                        GenericTypeIndicator<Map<String, Boolean>> chatMapType =
                                new GenericTypeIndicator<Map<String, Boolean>>() {
                                };
                        Map<String, Boolean> chats = dataSnapshot.getValue(chatMapType);
                        for (String chat : chats.keySet()) {
                            removeUserFromChat(chat, finalUserKey);
                        }
                        /* Now delete the user */
                        dbRef.child(USERS_DATABASE_PATH)
                                .child(finalUserKey)
                                .setValue(null);
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    /**
     * Get all the chats that a user is in.
     *
     * @param userId       Id of the user to lookup.
     * @param newChatCallback Function that is run whenever a new chatroom is created
     * @param dataCallback Function that is run for each chatroom that is loaded.
     */
    public static void getAllUserChats(String userId, final StateChangeListener newChatCallback,
                                       final ValueEventListener dataCallback) {
        /* First get the list of all chats that the user is in */
        dbRef.child(USERS_DATABASE_PATH)
                .child(userId)
                .child("chats")
                .addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        newChatCallback.onChange();
                        /* From each chat, get its data from the chats json group */
                        GenericTypeIndicator<Map<String, Boolean>> chatMapType =
                                new GenericTypeIndicator<Map<String, Boolean>>() {
                                };
                        Map<String, Boolean> chats = dataSnapshot.getValue(chatMapType);
                        if (chats != null) {
                            for (String chat : chats.keySet()) {
                                dbRef.child(CHATS_DATABASE_PATH)
                                        .child(chat)
                                        .addValueEventListener(dataCallback);
                            }
                        } else {
                            /* Call the callback to let activity know there
                            are no chatrooms */
                            dataCallback.onDataChange(null);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    /**
     * Subscribe a user to the notifications for all of their chatrooms.
     *
     * @param userId User id to subscribe.
     */
    public static void subscribeAllUserChats(String userId) {
        dbRef.child(USERS_DATABASE_PATH)
                .child(userId)
                .child("chats")
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        GenericTypeIndicator<Map<String, Boolean>> chatMapType =
                                new GenericTypeIndicator<Map<String, Boolean>>() {
                                };
                        Map<String, Boolean> chats = dataSnapshot.getValue(chatMapType);
                        if (chats != null) {
                            for (String chat : chats.keySet()) {
                                FirebaseMessaging.getInstance().subscribeToTopic(chat);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
    }

    /**
     * Looks up a user's data by their user id.
     *
     * @param userId       User id to look up by.
     * @param dataCallback Function that is run when the user is located.
     */
    public static void getUser(String userId, final ValueEventListener dataCallback) {
        dbRef.child(USERS_DATABASE_PATH)
                .child(userId)
                .addListenerForSingleValueEvent(dataCallback);
    }

    /**
     * Searches for a given user by their full name.
     *
     * @param name         Name to search by.
     * @param dataCallback Function to run when the data is retrieved.
     */
    public static void searchUserByName(String name, ChildEventListener dataCallback) {
        name = name.toLowerCase();
        dbRef.child(USERS_DATABASE_PATH)
                .orderByChild("searchName")
                .startAt(name)
                .endAt(name + "\uf8ff")
                .addChildEventListener(dataCallback);

        /* Code for partial match found at:
         https://stackoverflow.com/questions/38618953/
             how-to-do-a-simple-search-in-string-in-firebase-database
         The \uf8ff is a high unicode character and so it will match most
         other characters.
          */
    }

    interface StateChangeListener {
        void onChange();
    }
}
