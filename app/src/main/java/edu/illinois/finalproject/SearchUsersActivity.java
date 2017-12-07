package edu.illinois.finalproject;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;

/**
 * Created by Nicolas Nytko on 12/6/17.
 */

/**
 * Activity to search for users in order to create a new chat room.
 */

public class SearchUsersActivity extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        SearchView searchView = (SearchView)
                findViewById(R.id.sv_search_users);
        searchView.onActionViewExpanded();

        final UserSearchAdapter adapter = new UserSearchAdapter(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_user_results);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));

        /* Add some dummy users */

        adapter.addUser(new ChatUser("email", "Demo User 1"));
        adapter.addUser(new ChatUser("email", "Demo User 2"));
        adapter.addUser(new ChatUser("email", "Demo User 3"));
        adapter.addUser(new ChatUser("email", "Demo User 4"));
    }
}
