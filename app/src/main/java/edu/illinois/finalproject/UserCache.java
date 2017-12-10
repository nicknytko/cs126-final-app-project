package edu.illinois.finalproject;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import java.util.HashMap;
import java.util.Map;

/**
 * Class for caching user metadata so that it doesn't have to be continually loaded in.
 */

public class UserCache {
    private static Map<String, ChatUser> users = new HashMap<>();

    /**
     * Checks if a user is stored in the cache.
     * @param userId User to look up.
     * @return True if the user is cached.
     */
    public static boolean isUserCached(String userId) {
        return users.containsKey(userId);
    }

    /**
     * Loads a user from Firebase.
     * @param userId ID of the user to retrieve.
     * @param callback Function that is called when the data is retrieved.
     */
    public static void loadUser(final String userId, final UserLoadedCallback callback) {
        ChatApi.getUser(userId, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChatUser loadedUser = null;
                users.put(userId, loadedUser);
                if (callback != null) {
                    callback.onLoaded(loadedUser);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Loads a user from the cache if available, otherwise from the Firebase DB.
     * @param userId ID of the user to retrieve.
     * @param callback Function that is called when the data is retrieved.
     */
    public static void getUser(String userId, final UserLoadedCallback callback) {
        if (isUserCached(userId)) {
            callback.onLoaded(users.get(userId));
        } else {
            loadUser(userId, callback);
        }
    }

    interface UserLoadedCallback {
        void onLoaded(ChatUser user);
    }
}
