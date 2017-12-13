package edu.illinois.finalproject;

import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by nicolas on 12/13/17.
 */

public class UserViewHolder extends RecyclerView.ViewHolder {
    private CircularImageView icon;
    private TextView name;
    private TextView email;

    public UserViewHolder(View itemView) {
        super(itemView);

        icon = (CircularImageView) itemView.findViewById(R.id.iv_profile_picture);
        name = (TextView) itemView.findViewById(R.id.tv_user_name);
        email = (TextView) itemView.findViewById(R.id.tv_user_email);
    }

    /**
     * Bind a user to this view holder so that their data may be seen.
     *
     * @param user User to bind.
     */
    public void bind(ChatUser user) {
        name.setText(user.getName());
        email.setText(user.getEmail());

        if (user.getProfilePicture() != null) {
            Picasso.with(icon.getContext())
                    .load(user.getProfilePicture())
                    .into(icon);
        } else {
            icon.setImageResource(android.R.drawable.sym_def_app_icon);
        }
    }
}