package edu.illinois.finalproject;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public class UserSearchAdapter extends RecyclerView.Adapter<UserViewHolder> {
    private List<ChatUser> users = new ArrayList<>();
    private List<String> userIds = new ArrayList<>();
    private SearchUsersActivity activity;

    UserSearchAdapter(SearchUsersActivity activity) {
        this.activity = activity;
    }

    @Override
    public int getItemViewType(int position) {
        return R.layout.select_user_item;
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
        final int finalPos = position;
        holder.bind(users.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.returnUser(userIds.get(finalPos));
            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    /**
     * Add a user to the search list recycler view.
     *
     * @param userId ID of the user to add.
     * @param user   User to add.
     */
    public void addUser(String userId, ChatUser user) {
        /* Don't add the user if they already exist */
        boolean userExists = false;
        for (String currentUserId : userIds) {
            if (currentUserId.equals(userId)) {
                userExists = true;
            }
        }
        if (!userExists) {
            userIds.add(userId);
            users.add(user);
            notifyDataSetChanged();
        }
    }

    /**
     * Clear the list so that no users are shown.
     */
    public void removeAllUsers() {
        users.clear();
        userIds.clear();
        notifyDataSetChanged();
    }
}