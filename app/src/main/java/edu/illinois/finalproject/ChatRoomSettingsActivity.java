package edu.illinois.finalproject;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

import static android.support.v7.widget.helper.ItemTouchHelper.LEFT;
import static android.support.v7.widget.helper.ItemTouchHelper.RIGHT;

/**
 * Created by nicolas on 12/13/17.
 */

public class ChatRoomSettingsActivity extends AppCompatActivity {
    public static final String CHAT_DATA_PARCELABLE_TAG = "chatData";
    public static final String CHAT_ID_PARCELABLE_TAG = "chatId";
    private static final int FIND_USER_REQUEST_CODE = 0;
    private ChatRoom chatRoom;
    private String chatRoomId;
    private CircularImageView chatIcon;
    private EditText chatName;
    private EditText chatIconText;
    private UserListAdapter adapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        if (getIntent().hasExtra(CHAT_DATA_PARCELABLE_TAG)) {
            chatRoom = getIntent().getParcelableExtra(CHAT_DATA_PARCELABLE_TAG);
        }
        if (getIntent().hasExtra(CHAT_ID_PARCELABLE_TAG)) {
            chatRoomId = getIntent().getStringExtra(CHAT_ID_PARCELABLE_TAG);
        }
        chatName = (EditText) findViewById(R.id.et_chat_name);
        chatIconText = (EditText) findViewById(R.id.et_chat_icon);
        chatIcon = (CircularImageView) findViewById(R.id.iv_chat_icon);

        /* Start the user search activity when the add user button is pressed */
        final Context context = this;
        findViewById(R.id.btn_add_user).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(context, SearchUsersActivity.class);
                startActivityForResult(intent, FIND_USER_REQUEST_CODE);
            }
        });
        setupRecyclerView();

        /* Update the group pic when focus is changed away from the input box */
        chatIconText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                if (!focus) {
                    Picasso.with(context).load(chatIconText.getText().toString()).into(chatIcon);
                }
            }
        });

        if (chatRoom != null) {
            if (chatRoom.getUsers().isEmpty()) {
                chatRoom.getUsers().put(FirebaseAuth.getInstance().getUid(), true);
            }
            setUsersList(chatRoom.getUsers().keySet().toArray(new String[0]));
            setDefaultLabels();
            getSupportActionBar().setTitle(getString(R.string.chat_settings_title));
        } else {
            getSupportActionBar().setTitle(getString(R.string.chat_new_chat_title));
            setUsersList(new String[]{FirebaseAuth.getInstance().getUid()});
        }
    }

    /**
     * Sets up the recycler view and swiping mechanism.
     */
    private void setupRecyclerView() {
        adapter = new UserListAdapter();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_user_list);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
        ItemTouchHelper itemTouchHelper = new ItemTouchHelper(
                new ItemTouchHelper.SimpleCallback(0, LEFT | RIGHT) {
                    @Override
                    public boolean onMove(RecyclerView recyclerView,
                                          RecyclerView.ViewHolder viewHolder,
                                          RecyclerView.ViewHolder target) {
                        return false;
                    }

                    @Override
                    public int getSwipeDirs(RecyclerView recyclerView,
                                            RecyclerView.ViewHolder viewHolder) {
                        /* Prevent the user from swiping on their own self */
                        String userId = adapter.getUserId(viewHolder.getAdapterPosition());
                        if (!userId.equals(FirebaseAuth.getInstance().getUid())) {
                            return super.getSwipeDirs(recyclerView, viewHolder);
                        } else {
                            return 0;
                        }
                    }

                    @Override
                    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
                        if (chatRoomId != null) {
                            ChatApi.removeUserFromChat(chatRoomId,
                                    adapter.getUserId(viewHolder.getAdapterPosition()));
                        }
                        adapter.removeUser(viewHolder.getAdapterPosition());
                    }
                });
        itemTouchHelper.attachToRecyclerView(recyclerView);
    }

    /**
     * Populates the users recycler view list.
     */
    private void setUsersList(String[] users) {
        for (final String userId : users) {
            UserCache.getUser(userId, new UserCache.UserLoadedCallback() {
                @Override
                public void onLoaded(ChatUser user) {
                    adapter.addUser(userId, user);
                }
            });
        }
    }

    /**
     * Fills the values on the edit text fields.
     */
    private void setDefaultLabels() {
        chatName.setText(chatRoom.getName());
        chatIconText.setText(chatRoom.getIcon());
        final Context context = this;
        if (chatRoom.getIcon() != null && !chatRoom.getIcon().isEmpty()) {
            Picasso.with(context).load(chatRoom.getIcon()).into(chatIcon);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            if (chatRoom == null) {
                chatRoom = new ChatRoom();
                chatRoom.setTypeEnum(ChatApi.Type.GROUP);
            }
            chatRoom.setIcon(chatIconText.getText().toString());
            chatRoom.setName(chatName.getText().toString());
            chatRoom.setUsers(adapter.getUserIds());
            if (chatRoomId == null) {
                String newChatId = ChatApi.createChat(chatRoom);
                for (String userId : adapter.getUserIds().keySet()) {
                    ChatApi.addUserToChat(newChatId, userId);
                }
            } else {
                ChatApi.updateChatDetails(chatRoomId, chatRoom);
            }

            Intent result = new Intent();
            result.putExtra(CHAT_DATA_PARCELABLE_TAG, chatRoom);
            setResult(Activity.RESULT_OK, result);
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (data != null && requestCode == FIND_USER_REQUEST_CODE) {
            final String newUserId =
                    data.getStringExtra(SearchUsersActivity.USER_ID_PARCELABLE_TAG);
            if (!adapter.userExists(newUserId)) {
                UserCache.loadUser(newUserId, new UserCache.UserLoadedCallback() {
                    @Override
                    public void onLoaded(ChatUser newUserData) {
                        adapter.addUser(newUserId, newUserData);
                        if (chatRoom != null) {
                            ChatApi.addUserToChat(chatRoomId, newUserId);
                        }
                    }
                });
            }
        }
    }

}
