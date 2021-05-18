package com.sunsetrebel.catsy;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Switch;
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

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Login extends AppCompatActivity {
    private EditText mEmail, mPhone, mPassword;
    private ImageView slideImageEmail;
    private ImageView slideImagePhone;
    private Switch switchLogin;
    private Button mLoginBtn;
    private Button mGoogleAuthBtn;
    private LoginButton mFacebookAuthBtn;
    private ProgressBar progressBar;
    private int RC_SIGN_IN;
    private boolean isGoogleAuth = false;
    private com.google.firebase.auth.FirebaseAuth fAuth;
    private CallbackManager mCallbackManager;
    private final FirebaseAuth firebaseAuth = new FirebaseAuth();
    private Activity mActivity;
    private boolean isOTPregistration = true;

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseAuth.checkCurrentUser()) {
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mActivity = Login.this;
        firebaseAuth.createGoogleAuthRequestGetInstance(getApplicationContext());
        firebaseAuth.InitializeFacebookSdk(getApplicationContext());
        fAuth = firebaseAuth.getFAuth();
        mCallbackManager = CallbackManager.Factory.create();

        mEmail = findViewById(R.id.editUserEmail);
        mPhone = findViewById(R.id.editUserPhone);
        mPassword = findViewById(R.id.editUserPassword);
        progressBar = findViewById(R.id.progressBarLogin);
        mLoginBtn = findViewById(R.id.buttonLogin);
        mGoogleAuthBtn = findViewById(R.id.buttonLoginGoogle);
        mFacebookAuthBtn = findViewById(R.id.buttonLoginFacebook);
        switchLogin = findViewById(R.id.switchLogin);
        slideImageEmail = findViewById(R.id.slideImageEmail);
        slideImagePhone = findViewById(R.id.slideImagePhone);

        switchLogin.setOnClickListener(v -> {
            if (mEmail.getVisibility() == View.VISIBLE)
            {
                isOTPregistration = true;
                mEmail.setVisibility(View.INVISIBLE);
                mEmail.setEnabled(false);
                slideImageEmail.setVisibility(View.INVISIBLE);
                mPhone.setVisibility(View.VISIBLE);
                mPhone.setEnabled(true);
                slideImagePhone.setVisibility(View.VISIBLE);
            } else {
                isOTPregistration = false;
                mEmail.setVisibility(View.VISIBLE);
                mEmail.setEnabled(true);
                slideImageEmail.setVisibility(View.VISIBLE);
                mPhone.setVisibility(View.INVISIBLE);
                mPhone.setEnabled(false);
                slideImagePhone.setVisibility(View.INVISIBLE);
            }
        });

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
                restartActivity(mActivity);
                Toast.makeText(getApplicationContext(), "Facebook authentication failed!", Toast.LENGTH_SHORT).show();
            }
        });

        mGoogleAuthBtn.setOnClickListener(v -> {
            RC_SIGN_IN = FirebaseAuth.getRCSignIn();
            Intent signInIntent = firebaseAuth.signInGoogle(getApplicationContext());
            startActivityForResult(signInIntent, RC_SIGN_IN);
            isGoogleAuth = true;
            }
        );

        mLoginBtn.setOnClickListener(v -> {
            String email = mEmail.getText().toString().trim();
            String phone = mPhone.getText().toString().trim();
            String password = mPassword.getText().toString().trim();

            if (isOTPregistration) {
                if (TextUtils.isEmpty(phone)) {
                    mPhone.setError("Please enter phone");
                    return;
                } else if (!checkPhoneFormat(phone)) {
                    mPhone.setError("Phone is incorrect");
                    return;
                }
            } else {
                if (TextUtils.isEmpty(email)) {
                    mEmail.setError("Please enter email");
                    return;
                } else if (!checkEmailFormat(email)) {
                    mEmail.setError("Email is incorrect");
                    return;
                }
            }

            if (TextUtils.isEmpty(password)) {
                mPassword.setError("Please enter password");
                return;
            }

            if (password.length() < 6) {
                mPassword.setError("Please enter password more than 6 characters");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);

            if (isOTPregistration) {
                Intent intent = new Intent(getApplicationContext(), VerifyPhone.class);
                intent.putExtra("phoneNumber", phone);
                intent.putExtra("isTutorialNextPage", false);
                startActivity(intent);
            } else {
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                        finish();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        restartActivity(mActivity);
                        Toast.makeText(Login.this, "Email authentication failed!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private boolean checkEmailFormat(String text) {
        String expression = "^[\\w\\.-]+@([\\w\\-]+\\.)+[A-Z]{2,4}$";
        Pattern p = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(text);
        return m.matches();
    }

    private boolean checkPhoneFormat(String text) {
        if(!TextUtils.isEmpty(text)){
            text = text.substring(1);
            return TextUtils.isDigitsOnly(text);
        } else {
            return false;
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        mCallbackManager.onActivityResult(requestCode, resultCode, data);
        super.onActivityResult(requestCode, resultCode, data);
        if (isGoogleAuth) {
            GoogleSignInAccount account = firebaseAuth.onFirebaseResponse(requestCode, data);
            if (account != null) {
                firebaseAuthWithGoogle(account.getIdToken());
            }
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        firebaseAuth.setFirebaseUser(fAuth.getCurrentUser());
                        startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                        finish();
                    } else {
                        restartActivity(mActivity);
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
                        firebaseAuth.setFirebaseUser(fAuth.getCurrentUser());
                        startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                        finish();
                    } else {
                        restartActivity(mActivity);
                        Toast.makeText(getApplicationContext(), "Facebook authentication failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static void restartActivity(Activity activity) {
        activity.recreate();
    }
}