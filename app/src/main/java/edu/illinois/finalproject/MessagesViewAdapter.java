package edu.illinois.finalproject;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

/**
 * Created by Nicolas Nytko on 12/6/17.
 */

public class MessagesViewAdapter extends
        RecyclerView.Adapter<MessagesViewAdapter.MessageViewHolder> {
    private List<ChatMessage> messages = new ArrayList<>();
    private RecyclerView recyclerView;

    @Override
    public void onAttachedToRecyclerView(RecyclerView recyclerView) {
        this.recyclerView = recyclerView;
    }

    @Override
    public int getItemViewType(int position) {
        if (messages.get(position).getUserId().equals(FirebaseAuth.getInstance().getUid())) {
            return R.layout.chat_message_self_item;
        } else {
            return R.layout.chat_message_other_item;
        }
    }

    @Override
    public MessageViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View messageView = LayoutInflater
                .from(parent.getContext())
                .inflate(viewType, parent, false);
        return new MessageViewHolder(messageView);
    }

    @Override
    public void onBindViewHolder(MessageViewHolder holder, int position) {
        holder.bind(messages.get(position));
    }

    @Override
    public int getItemCount() {
        return messages.size();
    }

    /**
     * Adds a single message, appending it to the bottom of the list and then automatically
     * scrolling to the bottom.
     *
     * @param newMessage Message to add.
     */

    public void addMessage(ChatMessage newMessage) {
        /* Insertion sort the new message into the correct place */
        if (messages.size() == 0 ||
                newMessage.getTimestamp() > messages.get(messages.size() - 1).getTimestamp()) {
            messages.add(newMessage);
        } else {
            int index = 0;
            for (int i = messages.size() - 2; i >= 0 && index == 0; i--) {
                if (newMessage.getTimestamp() < messages.get(i).getTimestamp()) {
                    index = i;
                }
            }
            messages.add(index, newMessage);
        }
        notifyDataSetChanged();
        recyclerView.scrollToPosition(messages.size() - 1);
    }

    public class MessageViewHolder extends RecyclerView.ViewHolder {
        private CircularImageView profilePicture;
        private TextView messageData;
        private TextView timeStamp;

        public MessageViewHolder(View itemView) {
            super(itemView);

            profilePicture = (CircularImageView) itemView.findViewById(R.id.iv_profile_picture);
            messageData = (TextView) itemView.findViewById(R.id.tv_message_text);
            timeStamp = (TextView) itemView.findViewById(R.id.tv_timestamp);
        }

        /**
         * Binds message data to this view holder so that it can be seen.
         *
         * @param message Message to bind.
         */

        public void bind(ChatMessage message) {
            messageData.setText(message.getMessage());
            timeStamp.setText(new Date(message.getTimestamp()).toString());
        }
    }
}
