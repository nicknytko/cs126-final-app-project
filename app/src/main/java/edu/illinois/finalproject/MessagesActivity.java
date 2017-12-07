package edu.illinois.finalproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

/**
 * Created by Nicolas Nytko on 12/6/17.
 */

/**
 * Activity displaying all of the given messages in a single chatroom.
 */

public class MessagesActivity extends AppCompatActivity {
    private static final String messageText1 = "Hello, World!";
    private static final String messageText2 = "This is an example of message that spans " +
            "multiple lines.  The view and 9-patch will automatically resize itself to fit" +
            " the message contents.";
    private static final String messageText3 = "This is a message that the user sent.";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_messages);

        /* Enable the back button */
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final EditText messageInput = (EditText) findViewById(R.id.et_chat_input);
        final MessagesViewAdapter adapter = new MessagesViewAdapter();
        LinearLayoutManager layoutManager = new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false);
        layoutManager.setStackFromEnd(true);

        final RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_messages);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(layoutManager);

        /* Load in some dummy data */

        ChatMessage tempMessage1 = new ChatMessage(messageText1, "user1");
        ChatMessage tempMessage2 = new ChatMessage(messageText2, "user1");
        ChatMessage tempMessage3 = new ChatMessage(messageText3, "self");
        adapter.addMessage(tempMessage1);
        adapter.addMessage(tempMessage2);
        adapter.addMessage(tempMessage3);

        findViewById(R.id.fab_send_message).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /* Dont allow the user to send blank messages */
                if (messageInput.getText().length() > 0) {
                    ChatMessage newMessage = new ChatMessage(messageInput.getText().toString(),
                            "self");
                    messageInput.setText("");
                    adapter.addMessage(newMessage);
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
