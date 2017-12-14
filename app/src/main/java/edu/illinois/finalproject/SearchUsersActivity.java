package edu.illinois.finalproject;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.SearchView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;

/**
 * Activity to search for users in order to create a new chat room.  Will return the user, if any,
 * that was selected in the search.
 */

public class SearchUsersActivity extends AppCompatActivity {
    public static final String USER_ID_PARCELABLE_TAG = "userId";
    private UserSearchAdapter adapter;
    private static final int MIN_SEARCH_LENGTH = 3;

    /**
     * The user has selected another user, terminate this activity and return the selected user.
     *
     * @param userId User ID that was selected.
     */
    public void returnUser(String userId) {
        Intent result = new Intent();
        result.putExtra(USER_ID_PARCELABLE_TAG, userId);
        setResult(Activity.RESULT_OK, result);
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        final SearchView searchView = (SearchView) findViewById(R.id.sv_search_users);
        searchView.onActionViewExpanded();
        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchUser(query);
                return true;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchUser(newText);
                return true;
            }
        });
        adapter = new UserSearchAdapter(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.rv_user_results);
        recyclerView.setAdapter(adapter);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,
                LinearLayoutManager.VERTICAL, false));
    }

    /**
     * Start a search for a given user through Firebase.
     *
     * @param query Search query.  Will match start of name.
     */
    private void searchUser(String query) {
        adapter.removeAllUsers();
        if (query.length() >= MIN_SEARCH_LENGTH) {
            ChatApi.searchUserByName(query, new ChildEventListener() {
                @Override
                public void onChildAdded(DataSnapshot dataSnapshot, String s) {
                    if (!dataSnapshot.getKey().equals(FirebaseAuth.getInstance().getUid())) {
                        /* Dont add ourselves */
                        adapter.addUser(dataSnapshot.getKey(),
                                dataSnapshot.getValue(ChatUser.class));
                    }
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
            });
        }
    }
}
