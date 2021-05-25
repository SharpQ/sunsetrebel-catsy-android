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
import android.widget.LinearLayout;
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
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;
import com.hbb20.CountryCodePicker;
import com.sunsetrebel.MapsActivity;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Registration extends AppCompatActivity {
    private TextInputLayout mLayoutFullName, mLayoutEmail, mLayoutPhone, mLayoutPassword;
    private TextInputEditText mEditFullName, mEditEmail, mEditPhone, mEditPassword;
    private ImageView slideImageEmail;
    private ImageView slideImagePhone;
    private LinearLayout countryAndPhone;
    private Switch switchRegister;
    private Button mRegisterBtn;
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
    private CountryCodePicker ccp;

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
        setContentView(R.layout.activity_registration);
        mActivity = Registration.this;
        firebaseAuth.createGoogleAuthRequestGetInstance(getApplicationContext());
        firebaseAuth.InitializeFacebookSdk(getApplicationContext());
        fAuth = firebaseAuth.getFAuth();
        mCallbackManager = CallbackManager.Factory.create();

        mLayoutFullName = findViewById(R.id.inputLayoutUserFullName);
        mEditFullName = findViewById(R.id.inputEditUserFullName);
        mLayoutEmail = findViewById(R.id.inputLayoutUserEmail);
        mEditEmail = findViewById(R.id.inputEditUserEmail);
        mLayoutPhone = findViewById(R.id.inputLayoutUserPhone);
        mEditPhone = findViewById(R.id.inputEditUserPhone);
        mLayoutPassword = findViewById(R.id.inputLayoutUserPassword);
        mEditPassword = findViewById(R.id.inputEditUserPassword);
        mRegisterBtn = findViewById(R.id.buttonRegister);
        progressBar = findViewById(R.id.progressBarRegister);
        mGoogleAuthBtn = findViewById(R.id.buttonRegisterGoogle);
        mFacebookAuthBtn = findViewById(R.id.buttonRegisterFacebook);
        switchRegister = findViewById(R.id.switchRegister);
        slideImageEmail = findViewById(R.id.slideImageEmail);
        slideImagePhone = findViewById(R.id.slideImagePhone);
        ccp = findViewById(R.id.countryCodeDDL);
        countryAndPhone = findViewById(R.id.linearEditPhone);

        switchRegister.setOnClickListener(v -> {
            if (mLayoutEmail.getVisibility() == View.VISIBLE)
            {
                setUIStatePhone();
            } else {
                setUIStateEmail();
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
                setUIStatePhone();
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

        mRegisterBtn.setOnClickListener(v -> {
            String email = mEditEmail.getText().toString().trim();
            String countryCode = ccp.getSelectedCountryCode();
            String phone = mEditPhone.getText().toString().trim();
            String password = mEditPassword.getText().toString().trim();
            String fullName = mEditFullName.getText().toString().trim();

            if (isOTPregistration) {
                if (TextUtils.isEmpty(phone)) {
                    mEditPhone.setError("Please enter phone");
                    return;
                } else if (!checkPhoneFormat(phone)) {
                    mEditPhone.setError("Phone is incorrect");
                    return;
                }
            } else {
                if (TextUtils.isEmpty(email)) {
                    mLayoutEmail.setError("Please enter email");
                    return;
                } else if (!checkEmailFormat(email)) {
                    mLayoutEmail.setError("Email is incorrect");
                    return;
                }
            }

            if (TextUtils.isEmpty(password)) {
                mLayoutPassword.setError("Please enter password");
                return;
            }

            if (password.length() < 6) {
                mLayoutPassword.setError("Please enter password more than 6 characters");
                return;
            }

            if (TextUtils.isEmpty(fullName)) {
                mLayoutFullName.setError("Please enter your full name");
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            phone = "+" + countryCode + phone;

            if (isOTPregistration) {
                Intent intent = new Intent(getApplicationContext(), VerifyPhone.class);
                intent.putExtra("phoneNumber", phone);
                intent.putExtra("isTutorialNextPage", true);
                startActivity(intent);
                setUIStatePhone();
                progressBar.setVisibility(View.GONE);
            } else {
                fAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        startActivity(new Intent(getApplicationContext(), Tutorial.class));
                        finish();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        restartActivity(mActivity);
                        setUIStatePhone();
                        Toast.makeText(Registration.this, "Email authentication failed!" + Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
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
                        startActivity(new Intent(getApplicationContext(), Tutorial.class));
                        finish();
                    } else {
                        restartActivity(mActivity);
                        setUIStatePhone();
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
                        startActivity(new Intent(getApplicationContext(), Tutorial.class));
                        finish();
                    } else {
                        restartActivity(mActivity);
                        setUIStatePhone();
                        Toast.makeText(getApplicationContext(), "Facebook authentication failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void restartActivity(Activity activity) {
        activity.recreate();
    }

    private void setUIStateEmail() {
        isOTPregistration = false;
        mLayoutEmail.setVisibility(View.VISIBLE);
        mLayoutEmail.setEnabled(true);
        slideImageEmail.setVisibility(View.VISIBLE);
        countryAndPhone.setVisibility(View.INVISIBLE);
        countryAndPhone.setEnabled(false);
        slideImagePhone.setVisibility(View.INVISIBLE);
        switchRegister.setChecked(true);
    }

    private void setUIStatePhone() {
        isOTPregistration = true;
        mLayoutEmail.setVisibility(View.INVISIBLE);
        mLayoutEmail.setEnabled(false);
        slideImageEmail.setVisibility(View.INVISIBLE);
        countryAndPhone.setVisibility(View.VISIBLE);
        countryAndPhone.setEnabled(true);
        slideImagePhone.setVisibility(View.VISIBLE);
        switchRegister.setChecked(false);
    }
}