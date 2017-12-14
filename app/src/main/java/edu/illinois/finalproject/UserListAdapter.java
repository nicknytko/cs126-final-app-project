package edu.illinois.finalproject;

/**
 * Created by nicolas on 12/13/17.
 */

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class UserListAdapter extends RecyclerView.Adapter<UserViewHolder> {
    private List<ChatUser> users = new ArrayList<>();
    private List<String> userIds = new ArrayList<>();

    @Override
    public int getItemViewType(int position) {
        return R.layout.select_user_small_item;
    }

    @Override
    public UserViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View messageView = LayoutInflater
                .from(parent.getContext())
                .inflate(viewType, parent, false);
        return new UserViewHolder(messageView);
    }

    @Override
    public void onBindViewHolder(UserViewHolder holder, int position) {
        holder.bind(users.get(position));
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    /**
     * Finds the user ID from a specific view position.
     * @param position Index to get id from.
     * @return User ID as a string.
     */
    public String getUserId(int position) {
        return userIds.get(position);
    }

    /**
     * Add a user to the list of users.
     *
     * @param userId ID of the user to add.
     * @param user   User to add.
     */
    public void addUser(String userId, ChatUser user) {
        userIds.add(userId);
        users.add(user);
        notifyDataSetChanged();
    }

    /**
     * Removes a user from the list of users.
     * @param position User index to remove.
     */
    public void removeUser(int position) {
        userIds.remove(position);
        users.remove(position);
        notifyDataSetChanged();
    }

    /**
     * Generate a map of users that can be sent directly to firebase.
     * @return Map of users.
     */
    public Map<String, Boolean> getUserIds() {
        HashMap<String, Boolean> usersMap = new HashMap<>();
        for (String userId : userIds) {
            usersMap.put(userId, true);
        }
        return usersMap;
    }
}