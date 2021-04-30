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
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.sunsetrebel.MapsActivity;
import java.util.Objects;

public class Registration extends AppCompatActivity {
    EditText mFullName, mEmail, mPassword, mPhone;
    Button mRegisterBtn;
    Button mGoogleAuthBtn;
    ProgressBar progressBar;
    private int RC_SIGN_IN;
    com.google.firebase.auth.FirebaseAuth fAuth;
    FirebaseAuth firebaseAuth = new FirebaseAuth();

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
        fAuth = FirebaseAuth.getFAuth();

        mFullName = findViewById(R.id.editFullName);
        mEmail = findViewById(R.id.editUserEmail);
        mPassword = findViewById(R.id.editUserPassword);
        mPhone = findViewById(R.id.editUserPhone);
        mRegisterBtn = findViewById(R.id.buttonRegister);
        progressBar = findViewById(R.id.progressBarRegister);
        mGoogleAuthBtn = findViewById(R.id.buttonRegisterGoogle);

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
}