package edu.illinois.finalproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by Nicolas Nytko on 12/6/17.
 */

/**
 * Activity displaying all of the given messages in a single chatroom.
 */

public class MessagesActivity extends AppCompatActivity {
    public static final String CHAT_ID_PARCELABLE_TAG = "chatId";
    public static final String CHAT_DATA_PARCELABLE_TAG = "chatData";
    private ChildEventListener messageHandler;
    private String chatId;
    private ChatRoom chatRoom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        /* Set up UI elements */
        final EditText messageInput = (EditText) findViewById(R.id.et_chat_input);
        final MessagesViewAdapter adapter = new MessagesViewAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_messages);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);
        final ProgressBar progressBar = (ProgressBar) findViewById(R.id.pb_loading_spinner);

        /* Get chat data like name & icon */
        chatId = getIntent().getStringExtra(CHAT_ID_PARCELABLE_TAG);
        chatRoom = getIntent().getParcelableExtra(CHAT_DATA_PARCELABLE_TAG);
        updateActionBar(chatRoom);

        /* Set up a handler to handle messages getting sent */
        messageHandler = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                if (dataSnapshot != null) {
                    adapter.addMessage(dataSnapshot.getValue(ChatMessage.class));
                }
                progressBar.setVisibility(View.INVISIBLE);
                recyclerView.setVisibility(View.VISIBLE);
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {
            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        };
        ChatApi.setMessageHandler(chatId, messageHandler);

        findViewById(R.id.fab_send_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Dont allow the user to send blank messages */
                if (messageInput.getText().length() > 0) {
                    ChatMessage newMessage = new ChatMessage(messageInput.getText().toString(),
                            FirebaseAuth.getInstance().getUid());
                    messageInput.setText("");
                    ChatApi.addMessageToChat(chatId, newMessage);
                }
            }
        });
    }

    /**
     * Updates the top action bar to display chatroom details.
     * @param details Current chatroom to pull details from.
     */
    private void updateActionBar(ChatRoom details) {
        ActionBar actionBar = getSupportActionBar();
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM);
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setDisplayShowCustomEnabled(true);
        actionBar.setCustomView(R.layout.action_bar_messages);

        TextView title = (TextView) actionBar.getCustomView().findViewById(R.id.tv_chat_name);
        title.setText(details.getName());

        ImageView settings = (ImageView) actionBar.getCustomView()
                .findViewById(R.id.iv_settings_button);
        if (details.getTypeEnum() == ChatApi.Type.GROUP) {
            final Context context = this;
            settings.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    Intent intent = new Intent(context, ChatRoomSettingsActivity.class);
                    intent.putExtra(ChatRoomSettingsActivity.CHAT_DATA_PARCELABLE_TAG, chatRoom);
                    startActivityForResult(intent, 0);
                }
            });
        } else {
            settings.setVisibility(View.INVISIBLE);
        }

        CircularImageView image = (CircularImageView) actionBar.getCustomView()
                .findViewById(R.id.iv_chat_icon);
        Picasso.with(this).load(details.getIcon()).into(image);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (messageHandler != null) {
            ChatApi.removeMessageHandler(chatId, messageHandler);
            messageHandler = null;
        }
    }

    /* Adapted from StackOverflow:
       https://stackoverflow.com/questions/14545139/android-back-button-in-the-title-bar */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
