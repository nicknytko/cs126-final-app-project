package edu.illinois.finalproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
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
    private static final int CREATE_GROUP_REQUEST_CODE = 1;
    private ChatsViewAdapter adapter;
    private TextView noChatsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        noChatsText = (TextView) findViewById(R.id.tv_no_chats);
        setupRecyclerView();
        loadUserChatRooms();
        setupCreateChatButton();
    }

    /**
     * Loads all of the user's chat rooms into memory, and also subscribes them to notifications.
     */
    private void loadUserChatRooms() {
        final ProgressBar progressSpinner = (ProgressBar) findViewById(R.id.pb_loading_spinner);
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
                            ChatRoom room = dataSnapshot.getValue(ChatRoom.class);
                            if (room != null) {
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
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                });
    }

    /**
     * Sets up the recycler view and the mechanism that allows for users to swipe away chats.
     */
    private void setupRecyclerView() {
        adapter = new ChatsViewAdapter();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_chats);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        /* Create the item touch helper that will get called if the user swipes
        left or right on a chatroom */
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
    }

    /**
     * Sets up the create chat FAB.  Adds a menu that will pop up when the button gets pressed.
     */
    private void setupCreateChatButton() {
        final Context context = this;
        findViewById(R.id.fab_create_new_chat).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                PopupMenu menu = new PopupMenu(context, v);
                menu.getMenuInflater().inflate(R.menu.menu_chats_type_dropdown,
                        menu.getMenu());
                menu.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
                    @Override
                    public boolean onMenuItemClick(MenuItem menuItem) {
                        if (menuItem.getItemId() == R.id.item_one_on_one) {
                            Intent intent = new Intent(context, SearchUsersActivity.class);
                            startActivityForResult(intent, FIND_USER_REQUEST_CODE);
                        } else if (menuItem.getItemId() == R.id.item_group) {
                            Intent intent = new Intent(context, ChatRoomSettingsActivity.class);
                            startActivityForResult(intent, CREATE_GROUP_REQUEST_CODE);
                        }
                        return true;
                    }
                });
                menu.show();
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
     *
     * @param chatId ID of the chatroom.
     * @param room   Metadata of the chatroom as gotten from Firebase.
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
