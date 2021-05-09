package com.sunsetrebel.catsy;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;
import com.sunsetrebel.MapsActivity;
import java.util.Objects;

public class Registration extends AppCompatActivity {
    private EditText mFullName, mEmail, mPassword, mPhone;
    private Button mRegisterBtn;
    private Button mGoogleAuthBtn;
    private LoginButton mFacebookAuthBtn;
    private ProgressBar progressBar;
    private int RC_SIGN_IN;
    private com.google.firebase.auth.FirebaseAuth fAuth;
    private final FirebaseAuth firebaseAuth = new FirebaseAuth();

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseAuth.checkCurrentUser()) {
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);
        firebaseAuth.createGoogleAuthRequestGetInstance(getApplicationContext());
        firebaseAuth.InitializeFacebookSdk(getApplicationContext());
        fAuth = FirebaseAuth.getFAuth();
        CallbackManager mCallbackManager = CallbackManager.Factory.create();

        mFullName = findViewById(R.id.editFullName);
        mEmail = findViewById(R.id.editUserEmail);
        mPassword = findViewById(R.id.editUserPassword);
        mPhone = findViewById(R.id.editUserPhone);
        mRegisterBtn = findViewById(R.id.buttonRegister);
        progressBar = findViewById(R.id.progressBarRegister);
        mGoogleAuthBtn = findViewById(R.id.buttonRegisterGoogle);
        mFacebookAuthBtn = findViewById(R.id.buttonRegisterFacebook);
        mFacebookAuthBtn.setReadPermissions("email", "public_profile");

        mFacebookAuthBtn.registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                firebaseAuthWithFacebook(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });

        mGoogleAuthBtn.setOnClickListener(v -> {
                    RC_SIGN_IN = FirebaseAuth.getRCSignIn();
                    Intent signInIntent = firebaseAuth.signInGoogle(getApplicationContext());
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
        );

        if (fAuth.getCurrentUser() != null) {
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            finish();
        }

        mRegisterBtn.setOnClickListener(v -> {
            String email = mEmail.getText().toString().trim();
            String password = mPassword.getText().toString().trim();

            if (TextUtils.isEmpty(email)) {
                mEmail.setError("Please enter email");
                return;
            }

            if (TextUtils.isEmpty(password)) {
                mEmail.setError("Please enter password");
                return;
            }

            if (password.length() < 6) {
                mPassword.setError("Please enter password more than 6 characters");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            // FIREBASE REGISTRATION BELOW
            fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                if (task.isSuccessful()) {
                    startActivity(new Intent(getApplicationContext(), Tutorial.class));
                } else {
                    Toast.makeText(Registration.this, "Sorry, some error occurred :(" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                    progressBar.setVisibility(View.GONE);
                }
            });
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GoogleSignInAccount account = firebaseAuth.onFirebaseResponse(requestCode, data);
        firebaseAuthWithGoogle(account.getIdToken());
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseAuth.setFirebaseUser(fAuth.getCurrentUser());
                        startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Google authentication failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void firebaseAuthWithFacebook(AccessToken token) {
        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        FirebaseAuth.setFirebaseUser(fAuth.getCurrentUser());
                        startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Facebook authentication failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}