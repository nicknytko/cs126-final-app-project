package edu.illinois.finalproject;

import android.app.Activity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Nicolas Nytko on 12/6/17.
 */

public class UserSearchAdapter extends
        RecyclerView.Adapter<UserSearchAdapter.UserViewHolder> {
    private List<ChatUser> users = new ArrayList<>();
    private Activity activity;

    UserSearchAdapter(Activity activity) {
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
        holder.bind(users.get(position));
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                activity.finish();
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
     * @param user User to add.
     */
    public void addUser(ChatUser user) {
        users.add(user);
        notifyDataSetChanged();
    }

    /**
     * Clear the list so that no users are shown.
     */
    public void removeAllUsers() {
        users.clear();
        notifyDataSetChanged();
    }

    public class UserViewHolder extends RecyclerView.ViewHolder {
        private CircularImageView icon;
        private TextView name;

        public UserViewHolder(View itemView) {
            super(itemView);

            icon = (CircularImageView) itemView.findViewById(R.id.iv_profile_picture);
            name = (TextView) itemView.findViewById(R.id.tv_user_name);
        }

        /**
         * Bind a user to this view holder so that their data may be seen.
         *
         * @param user User to bind.
         */
        public void bind(ChatUser user) {
            name.setText(user.getName());
            if (user.getProfilePicture() != null) {
                Picasso.with(icon.getContext())
                        .load(user.getProfilePicture())
                        .into(icon);
            } else {
                icon.setImageResource(android.R.drawable.sym_def_app_icon);
            }
        }
    }
}