package edu.illinois.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseApp;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

public class SignInActivity extends AppCompatActivity implements View.OnClickListener {
    private GoogleSignInClient googleClient;
    private FirebaseAuth firebaseAuth;
    private static final int REQUEST_CODE_SIGN_IN = 1;
    private static final String LOG_TAG = "SignInActivity";

    /**
     * Launch the account details activity that will display details for currently signed in
     * account.
     */
    private void launchDetailsActivity() {
        Intent detailIntent = new Intent(this, ChatsActivity.class);
        startActivity(detailIntent);
        /* Close this activity so that we can't go back to the sign in */
        finish();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        FirebaseApp.initializeApp(this);
        googleClient = SignInOptions.getClient(this);
        firebaseAuth = FirebaseAuth.getInstance();

        findViewById(R.id.sib_google_sign_in).setOnClickListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser user = firebaseAuth.getCurrentUser();
        if (user != null) {
            launchDetailsActivity();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == REQUEST_CODE_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                authenticateFirebase(account);
            } catch (ApiException e) {
                Toast.makeText(this, "Failed to log in: " + e.getMessage(), Toast.LENGTH_SHORT)
                        .show();
                e.printStackTrace();
                Log.e(LOG_TAG, e.getMessage());
                Log.e(LOG_TAG, "Status code: " + e.getStatusCode());
            }
        }
    }

    /**
     * Authenticates a google account with firebase.  The account will get registered with Firebase
     * if it hasn't already.
     *
     * @param googleAccount Google account to authenticate with.
     */
    private void authenticateFirebase(GoogleSignInAccount googleAccount) {
        AuthCredential credential =
                GoogleAuthProvider.getCredential(googleAccount.getIdToken(), null);

        firebaseAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        launchDetailsActivity();
                    }
                });
    }

    @Override
    public void onClick(View v) {
        if (v.getId() == R.id.sib_google_sign_in) {
            Intent signInIntent = googleClient.getSignInIntent();
            startActivityForResult(signInIntent, REQUEST_CODE_SIGN_IN);
        }
    }
}
