package edu.illinois.finalproject;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.View;
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
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final TextView noChatsText = (TextView) findViewById(R.id.tv_no_chats);
        noChatsText.setVisibility(View.VISIBLE);

        final ChatsViewAdapter adapter = new ChatsViewAdapter();
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_chats);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        /* Add all of the user's chatrooms */
        ChatApi.getAllUserChats(FirebaseAuth.getInstance().getCurrentUser().getUid(),
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        adapter.addChat(dataSnapshot.getKey(),
                                dataSnapshot.getValue(ChatRoom.class));
                        noChatsText.setVisibility(View.INVISIBLE);
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
                context.startActivity(intent);
            }
        });
    }
}
