package edu.illinois.finalproject;

import android.content.Context;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;

/**
 * Created by Nicolas Nytko on 11/14/17.
 */

public class SignInOptions {
    /**
     * Get the sign in options required for this project.
     *
     * @return GoogleSignInOptions object.
     */
    public static GoogleSignInOptions getOptions() {
        GoogleSignInOptions googleOpts = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(ApiKey.OAUTH_2_SERVER)
                .requestProfile()
                .requestEmail()
                .build();

        return googleOpts;
    }

    /**
     * Get a Google sign in client that can be used for authenticating users with Google.
     *
     * @param context Context object that is needed by getClient()
     * @return A Google sign in client object.
     */
    public static GoogleSignInClient getClient(Context context) {
        return GoogleSignIn.getClient(context, getOptions());
    }
}
