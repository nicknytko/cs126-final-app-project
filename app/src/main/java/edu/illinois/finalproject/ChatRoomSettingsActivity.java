package edu.illinois.finalproject;

import android.content.Context;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;

import com.mikhaellopez.circularimageview.CircularImageView;
import com.squareup.picasso.Picasso;

/**
 * Created by nicolas on 12/13/17.
 */

public class ChatRoomSettingsActivity extends AppCompatActivity {
    public static final String CHAT_DATA_PARCELABLE_TAG = "chatData";
    private ChatRoom chatRoom;
    private CircularImageView chatIcon;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_settings);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(getString(R.string.chat_settings_title));

        chatRoom = getIntent().getParcelableExtra(CHAT_DATA_PARCELABLE_TAG);
        final EditText chatName = (EditText) findViewById(R.id.et_chat_name);
        final EditText chatIconText = (EditText) findViewById(R.id.et_chat_icon);
        chatIcon = (CircularImageView) findViewById(R.id.iv_chat_icon);

        chatName.setText(chatRoom.getName());
        chatIconText.setText(chatRoom.getIcon());
        final Context context = this;
        Picasso.with(context).load(chatRoom.getIcon()).into(chatIcon);
        chatIconText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View view, boolean focus) {
                if (!focus) {
                    Picasso.with(context).load(chatIconText.getText().toString()).into(chatIcon);
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
