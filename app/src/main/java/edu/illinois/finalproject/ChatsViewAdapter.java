package edu.illinois.finalproject;

import android.content.Context;
import android.content.Intent;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * Created by Nicolas Nytko on 12/6/17.
 */

public class ChatsViewAdapter extends
        RecyclerView.Adapter<ChatsViewAdapter.ChatsViewHolder> {
    private List<ChatRoom> chats = new ArrayList<>();

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
        holder.bind(chats.get(position));
        final Context context = holder.itemView.getContext();
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, MessagesActivity.class);
                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return chats.size();
    }

    public void addChat(ChatRoom chatRoom) {
        chats.add(chatRoom);
        notifyDataSetChanged();
    }

    public void removeChat(int position) {
        chats.remove(position);
        notifyDataSetChanged();
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

        public void bind(ChatRoom chatRoom) {
            name.setText(chatRoom.getName());
            if (chatRoom.getIcon() != null) {
                Picasso.with(icon.getContext())
                        .load(chatRoom.getIcon())
                        .into(icon);
            } else {
                icon.setImageResource(android.R.drawable.sym_def_app_icon);
            }
            if (chatRoom.getLastMessage() != null) {
                lastMessage.setText(chatRoom.getLastMessage().getMessage());
                SimpleDateFormat dateFormat =
                        new SimpleDateFormat("M/d/y h:mm a", Locale.US);
                lastTimeStamp.setText(
                        dateFormat.format(new Date(chatRoom.getLastMessage().getTimestamp())));
            } else {
                lastMessage.setText("");
                lastTimeStamp.setText("");
            }
        }
    }
}
