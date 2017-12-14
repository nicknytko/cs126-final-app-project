package edu.illinois.finalproject;

import android.content.Context;
import android.content.Intent;
import android.support.v4.util.Pair;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class ChatsViewAdapter extends
        RecyclerView.Adapter<ChatsViewAdapter.ChatsViewHolder> {
    private List<Pair<String, ChatRoom>> chats = new ArrayList<>();

    @Override
    public int getItemViewType(int position) {
        return R.layout.select_message_item;
    }

    @Override
    public ChatsViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View messageView = LayoutInflater
                .from(parent.getContext())
                .inflate(viewType, parent, false);
        return new ChatsViewHolder(messageView);
    }

    @Override
    public void onBindViewHolder(ChatsViewHolder holder, int position) {
        holder.bind(getChat(position));
        final Context context = holder.itemView.getContext();
        final int finalPos = position;
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessagesActivity.class);
                intent.putExtra(MessagesActivity.CHAT_ID_PARCELABLE_TAG, getChatId(finalPos));
                intent.putExtra(MessagesActivity.CHAT_DATA_PARCELABLE_TAG, getChat(finalPos));
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    /**
     * Add a chatroom to the recycler view. Will get appended to the bottom.
     *
     * @param chatRoom Chatroom to add.
     */

    public void addChat(String chatId, ChatRoom chatRoom) {
        Pair<String, ChatRoom> newPair = new Pair<>(chatId, chatRoom);
        removeChatById(chatId);

        if (chats.size() == 0 ||
                chatRoom.getLastTimestamp() == 0 ||
                chatRoom.getLastTimestamp() < chats.get(0).second.getLastTimestamp()) {
            chats.add(0, newPair);
        } else {
            /* Insertion sort into the correct spot */
            int index = chats.size();
            for (int i = 0; i < chats.size() && index == chats.size(); i++) {
                if (chatRoom.getLastTimestamp() > chats.get(i).second.getLastTimestamp()) {
                    index = i;
                }
            }
            chats.add(index, newPair);
        }
        notifyDataSetChanged();
    }

    /**
     * Removes all chats from the list.
     */
    public void clearChats() {
        chats.clear();
        notifyDataSetChanged();
    }

    /**
     * Removes a chat by its chatId if it exists.
     *
     * @param chatId Chat Id to remove by.
     */
    public void removeChatById(String chatId) {
        for (int i = 0; i < chats.size(); i++) {
            if (chats.get(i).first.equals(chatId)) {
                chats.remove(i);
                return;
            }
        }
    }

    /**
     * Removes a single chatroom from the recycler view.
     *
     * @param position Index of the chatroom to delete.
     */

    public void removeChat(int position) {
        chats.remove(position);
        notifyDataSetChanged();
    }

    /**
     * Gets the chatroom id of a chatroom at a specific index.
     *
     * @param position Index of the chat to get id of.
     * @return String chatroom id that can be passed to Firebase.
     */
    public String getChatId(int position) {
        return chats.get(position).first;
    }

    /**
     * Gets the chatroom object at a specific index.
     *
     * @param position Index of the chat to get.
     * @return Chatroom object.
     */
    public ChatRoom getChat(int position) {
        return chats.get(position).second;
    }

    public class ChatsViewHolder extends RecyclerView.ViewHolder {
        private CircularImageView icon;
        private TextView name;
        private TextView lastMessage;
        private TextView lastTimeStamp;
        public View itemView;

        public ChatsViewHolder(View itemView) {
            super(itemView);

            this.itemView = itemView;
            icon = (CircularImageView) itemView.findViewById(R.id.iv_profile_picture);
            name = (TextView) itemView.findViewById(R.id.tv_chat_name);
            lastMessage = (TextView) itemView.findViewById(R.id.tv_chat_latest_message);
            lastTimeStamp = (TextView) itemView.findViewById(R.id.tv_chat_latest_timestamp);
        }

        /**
         * Displays details about a given chatroom.
         *
         * @param chatRoom Chatroom to display details about.
         */

        public void bind(ChatRoom chatRoom) {
            name.setText(chatRoom.getName());
            if (chatRoom.getIcon() != null && !chatRoom.getIcon().isEmpty()) {
                Picasso.with(icon.getContext())
                        .load(chatRoom.getIcon())
                        .into(icon);
            } else {
                icon.setImageResource(android.R.drawable.sym_def_app_icon);
            }

            /* Display the last sent message and timestamp if they exist */
            if (chatRoom.getLastMessage() != null) {
                lastMessage.setText(chatRoom.getLastMessage().getMessage());
                SimpleDateFormat dateFormat =
                        new SimpleDateFormat("M/d/y h:mm a", Locale.US);
                lastTimeStamp.setText(
                        dateFormat.format(new Date(chatRoom.getLastMessage().getTimestamp())));

                lastMessage.setVisibility(View.VISIBLE);
                lastTimeStamp.setVisibility(View.VISIBLE);
            } else {
                lastMessage.setVisibility(View.INVISIBLE);
                lastTimeStamp.setVisibility(View.INVISIBLE);
            }
        }
    }
}
