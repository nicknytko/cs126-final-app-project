package edu.illinois.finalproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.ValueEventListener;

import static android.support.v7.widget.helper.ItemTouchHelper.LEFT;
import static android.support.v7.widget.helper.ItemTouchHelper.RIGHT;

/**
 * Activity that will list all of the chats that the user is in.  Will be the first thing the user
 * sees on start up after the login.
 */

public class ChatsActivity extends AppCompatActivity {
    private static final int FIND_USER_REQUEST_CODE = 0;
    private ChatsViewAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView noChatsText = (TextView) findViewById(R.id.tv_no_chats);
        final ProgressBar progressSpinner = (ProgressBar) findViewById(R.id.pb_loading_spinner);

        adapter = new ChatsViewAdapter();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_chats);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        /* Add all of the user's chatrooms */
        ChatApi.subscribeAllUserChats(FirebaseAuth.getInstance().getUid());
        ChatApi.getAllUserChats(FirebaseAuth.getInstance().getUid(),
                new ValueEventListener() {
                    @Override
                    public void onDataChange(final DataSnapshot dataSnapshot) {
                        progressSpinner.setVisibility(View.INVISIBLE);

                        if (dataSnapshot == null) {
                            /* If datasnapshot is null then there are no chatrooms */
                            noChatsText.setVisibility(View.VISIBLE);
                        } else {
                            final ChatRoom room = dataSnapshot.getValue(ChatRoom.class);
                            /* Cache all the users so that they will be loaded by the time
                            the user picks a chat room */
                            for (String user : room.getUsers().keySet()) {
                                UserCache.loadUser(user, null);
                            }

                            if (room.getTypeEnum() == ChatApi.Type.GROUP) {
                                adapter.addChat(dataSnapshot.getKey(), room);
                            } else if (room.getTypeEnum() == ChatApi.Type.ONE_ON_ONE) {
                                addOneOnOneChat(dataSnapshot.getKey(), room);
                            }
                            noChatsText.setVisibility(View.INVISIBLE);
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });

        /* Handle swipes on the recycler view */
        ItemTouchHelper.SimpleCallback itemTouchCallback =
                new ItemTouchHelper.SimpleCallback(0, LEFT | RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        ChatApi.removeUserFromChat(
                                adapter.getChatId(viewHolder.getAdapterPosition()),
                                FirebaseAuth.getInstance().getUid());
                        adapter.removeChat(viewHolder.getAdapterPosition());
                        if (adapter.getItemCount() == 0) {
                            noChatsText.setVisibility(View.VISIBLE);
                        }
                    }
                };
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(itemTouchCallback);
        itemTouchHelper.attachToRecyclerView(recyclerView);

        /* FAB create new chat button */
        final Context context = this;
        findViewById(R.id.fab_create_new_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, SearchUsersActivity.class);
                //context.startActivity(intent);
                startActivityForResult(intent, FIND_USER_REQUEST_CODE);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == FIND_USER_REQUEST_CODE) {
            String newUser = data.getStringExtra(SearchUsersActivity.USER_ID_PARCELABLE_TAG);
            String[] users = new String[]{FirebaseAuth.getInstance().getUid(), newUser};
            ChatApi.createChat(null, users, ChatApi.Type.ONE_ON_ONE);
        }
    }

    /**
     * Add a one to one chatroom to the list.  These have to be specially handled because
     * metadata is gotten from the other user in the chatroom.
     * @param chatId ID of the chatroom.
     * @param room Metadata of the chatroom as gotten from Firebase.
     */
    private void addOneOnOneChat(final String chatId, final ChatRoom room) {
        String otherUser = null;
        String currentUser = FirebaseAuth.getInstance().getUid();
        for (String user : room.getUsers().keySet()) {
            if (!user.equals(currentUser)) {
                otherUser = user;
            }
        }
        if (otherUser != null) {
            UserCache.loadUser(otherUser, new UserCache.UserLoadedCallback() {
                @Override
                public void onLoaded(ChatUser user) {
                    ChatRoom userChat = new ChatRoom();
                    userChat.setLastMessage(room.getLastMessage());
                    userChat.setTypeEnum(room.getTypeEnum());
                    userChat.setUsers(room.getUsers());
                    userChat.setName(user.getName());
                    userChat.setIcon(user.getProfilePicture());
                    adapter.addChat(chatId, userChat);
                }
            });
        }
    }
}
