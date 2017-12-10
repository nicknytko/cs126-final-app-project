package edu.illinois.finalproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.GenericTypeIndicator;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

/**
 * Created by Nicolas Nytko on 12/6/17.
 */

/**
 * Activity displaying all of the given messages in a single chatroom.
 */

public class MessagesActivity extends AppCompatActivity {
    public static final String CHAT_ID_PARCELABLE_TAG = "chat_id";
    private ChildEventListener messageHandler;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        /* Enable the back button */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        /* Set up UI elements */
        final EditText messageInput = (EditText) findViewById(R.id.et_chat_input);
        final MessagesViewAdapter adapter = new MessagesViewAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);

        /* Get chat data like name & icon */
        final String chatId = getIntent().getStringExtra(CHAT_ID_PARCELABLE_TAG);
        ChatApi.getChatroomDetails(chatId, new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                ChatRoom chatRoom = dataSnapshot.getValue(ChatRoom.class);
                /* Do stuff with this */
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        /* Set up a handler to handle messages getting sent */
        messageHandler = new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                adapter.addMessage(dataSnapshot.getValue(ChatMessage.class));
            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {}

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {}

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        };
        ChatApi.setMessageHandler(chatId, messageHandler);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_messages);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

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
