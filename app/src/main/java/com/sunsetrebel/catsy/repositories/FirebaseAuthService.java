package com.sunsetrebel.catsy.repositories;

import android.content.Context;
import android.content.Intent;
import android.widget.Toast;
import com.facebook.FacebookSdk;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.utils.LoginType;

public class FirebaseAuthService {
    private static FirebaseAuthService instance;
    private GoogleSignInClient mGoogleSignInClient;
    private android.content.Context context;
    private FirebaseAuth fAuth;
    private final static int RC_SIGN_IN = 123;
    private FirebaseUser user;

    public FirebaseAuthService() {
        fAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
    }

    public static FirebaseAuthService getInstance() {
        if (instance == null) {
            instance = new FirebaseAuthService();
        }
        return instance;
    }

    public com.google.firebase.auth.FirebaseAuth getFirebaseClient() {
        if (fAuth == null) {
            fAuth = com.google.firebase.auth.FirebaseAuth.getInstance();
        }
        return fAuth;
    }

    public static int getRCSignIn() {
        return RC_SIGN_IN;
    }

    public void setFirebaseUser(FirebaseUser user, LoginType loginType) {
        this.user = user;
        FirebaseFirestoreService firebaseFirestoreService = FirebaseFirestoreService.getInstance();
        firebaseFirestoreService.getUserInFirestore(value -> {
            if(!value) {
                if (loginType == LoginType.GOOGLE) {
                    firebaseFirestoreService.createNewUserByGoogle(fAuth.getCurrentUser().getUid(), fAuth.getCurrentUser().getDisplayName(), fAuth.getCurrentUser().getEmail(),
                            fAuth.getCurrentUser().getPhoneNumber(), fAuth.getCurrentUser().getPhotoUrl().toString());
                } else if (loginType == LoginType.FACEBOOK) {
                    firebaseFirestoreService.createNewUserByFacebook(fAuth.getCurrentUser().getUid(), fAuth.getCurrentUser().getDisplayName(), fAuth.getCurrentUser().getEmail(),
                            fAuth.getCurrentUser().getPhoneNumber(), fAuth.getCurrentUser().getPhotoUrl().toString());
                } else if (loginType == LoginType.PHONE) {
                    firebaseFirestoreService.createNewUserByPhone(fAuth.getCurrentUser().getUid(), fAuth.getCurrentUser().getDisplayName(), fAuth.getCurrentUser().getPhoneNumber());
                } else if (loginType == LoginType.EMAIL) {
                    firebaseFirestoreService.createNewUserByEmail(fAuth.getCurrentUser().getUid(), fAuth.getCurrentUser().getDisplayName(), fAuth.getCurrentUser().getEmail());
                }
            }
        }, fAuth.getCurrentUser().getUid());
    }

    public void signOutFirebase() {
        com.google.firebase.auth.FirebaseAuth.getInstance().signOut();
    }

    public void InitializeFacebookSdk(android.content.Context context) {
        FacebookSdk.sdkInitialize(context);
    }

    public boolean isUserLoggedIn() {
        user = fAuth.getCurrentUser();
        if (user != null) {
            return true;
        }
        return false;
    }

    public void createGoogleAuthRequestGetInstance(android.content.Context context) {
        // Configure Google Sign In
        GoogleSignInOptions gso = new GoogleSignInOptions
                .Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(context.getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(context, gso);
    }

    public Intent signInGoogle(Context context) {
        //context - current page getApplicationContext()
        this.context = context;
        return mGoogleSignInClient.getSignInIntent();
    }


    public GoogleSignInAccount onFirebaseResponse(int requestCode, Intent data) {
        GoogleSignInAccount account = null;
        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                // Google Sign In was successful, authenticate with Firebase
                account = task.getResult(ApiException.class);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                Toast.makeText(context, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
        return account;
    }
}
