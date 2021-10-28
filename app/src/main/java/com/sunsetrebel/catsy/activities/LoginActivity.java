package com.sunsetrebel.catsy.activities;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Switch;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.GoogleAuthProvider;
import com.hbb20.CountryCodePicker;
import com.sunsetrebel.catsy.repositories.FirebaseAuthService;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.repositories.FirebaseFirestoreService;
import com.sunsetrebel.catsy.utils.LoginType;

import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class LoginActivity extends AppCompatActivity {
    private TextInputLayout mLayoutEmail, mLayoutPhone, mLayoutPassword;
    private TextInputEditText mEditEmail, mEditPhone, mEditPassword;
    private ImageView slideImageEmail, slideImagePhone;
    private LinearLayout countryAndPhone;
    private Switch switchLogin;
    private Button mLoginBtn, mGoogleAuthBtn, mFacebookAuthBtn;
    private ProgressBar progressBar;
    private int RC_SIGN_IN;
    private boolean isGoogleAuth = false;
    private com.google.firebase.auth.FirebaseAuth fAuth;
    private CallbackManager mCallbackManager;
    private final FirebaseAuthService firebaseAuthService = FirebaseAuthService.getInstance();
    private final FirebaseFirestoreService firebaseFirestoreService = FirebaseFirestoreService.getInstance();
    private Activity mActivity;
    private boolean isOTPregistration = true;
    private CountryCodePicker ccp;

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseAuthService.isUserLoggedIn()) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            Animatoo.animateFade(this);  //fire the zoom animation
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mActivity = LoginActivity.this;
        firebaseAuthService.createGoogleAuthRequestGetInstance(getApplicationContext());
        firebaseAuthService.InitializeFacebookSdk(getApplicationContext());
        fAuth = firebaseAuthService.getFirebaseClient();
        mCallbackManager = CallbackManager.Factory.create();

        mLayoutEmail = findViewById(R.id.inputLayoutUserEmail);
        mEditEmail = findViewById(R.id.inputEditUserEmail);
        mLayoutPhone = findViewById(R.id.inputLayoutUserPhone);
        mEditPhone = findViewById(R.id.inputEditUserPhone);
        mLayoutPassword = findViewById(R.id.inputLayoutUserPassword);
        mEditPassword = findViewById(R.id.inputEditUserPassword);
        progressBar = findViewById(R.id.progressBarLogin);
        mLoginBtn = findViewById(R.id.buttonLogin);
        mGoogleAuthBtn = findViewById(R.id.buttonLoginGoogle);
        mFacebookAuthBtn = findViewById(R.id.buttonLoginFacebook);
        switchLogin = findViewById(R.id.switchLogin);
        slideImageEmail = findViewById(R.id.slideImageEmail);
        slideImagePhone = findViewById(R.id.slideImagePhone);
        ccp = findViewById(R.id.ddlCountryCode);
        countryAndPhone = findViewById(R.id.linearEditPhone);

        switchLogin.setOnClickListener(v -> {
            if (mLayoutEmail.getVisibility() == View.VISIBLE)
            {
                setUIStatePhone();
            } else {
                setUIStateEmail();
            }
        });

        mFacebookAuthBtn.setOnClickListener(v -> {

            LoginManager.getInstance().logInWithReadPermissions(LoginActivity.this, Arrays.asList("public_profile", "email"));


        });

        LoginManager.getInstance().registerCallback(mCallbackManager, new FacebookCallback<LoginResult>() {
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
            Intent signInIntent = firebaseAuthService.signInGoogle(getApplicationContext());
            startActivityForResult(signInIntent, FirebaseAuthService.getRCSignIn());
            Animatoo.animateFade(this);  //fire the zoom animation
            isGoogleAuth = true;
            }
        );

        mLoginBtn.setOnClickListener(v -> {
            String email = mEditEmail.getText().toString().trim();
            String countryCode = ccp.getSelectedCountryCode();
            String phone = mEditPhone.getText().toString().trim();
            String password = mEditPassword.getText().toString().trim();

            if (isOTPregistration) {
                if(!validatePhone(phone)) {
                    return;
                }
            } else {
                if(!validateEmail(email)) {
                    return;
                }
            }

            if(!isOTPregistration && !validatePassword(password)) {
                return;
            }

            progressBar.setVisibility(View.VISIBLE);
            phone = "+" + countryCode + phone;

            if (isOTPregistration) {
                Intent intent = new Intent(getApplicationContext(), VerifyPhoneActivity.class);
                intent.putExtra("phoneNumber", phone);
                intent.putExtra("isTutorialNextPage", false);
                startActivity(intent);
                Animatoo.animateFade(this);  //fire the zoom animation
                setUIStatePhone();
                progressBar.setVisibility(View.GONE);
            } else {
                fAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        firebaseAuthService.setFirebaseUser(fAuth.getCurrentUser(), LoginType.EMAIL);
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        Animatoo.animateFade(this);  //fire the zoom animation
                        finish();
                    } else {
                        progressBar.setVisibility(View.GONE);
                        restartActivity(mActivity);
                        setUIStatePhone();
                        Toast.makeText(LoginActivity.this, "Email authentication failed!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    private boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            mLayoutEmail.setError("Please enter email");
            return false;
        } else if (!checkEmailFormat(email)) {
            mLayoutEmail.setError("Email is incorrect");
            return false;
        }
        return true;
    }

    private boolean validatePhone(String phone) {
        if (TextUtils.isEmpty(phone)) {
            mEditPhone.setError("Please enter phone");
            return false;
        } else if (!checkPhoneFormat(phone)) {
            mEditPhone.setError("Phone is incorrect");
            return false;
        }
        return true;
    }

    private boolean validatePassword(String password) {
        if (TextUtils.isEmpty(password)) {
            mLayoutPassword.setError("Please enter password");
            return false;
        } else if (password.length() < 6) {
            mLayoutPassword.setError("Please enter password more than 6 characters");
            return false;
        }
        return true;
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
            GoogleSignInAccount account = firebaseAuthService.onFirebaseResponse(requestCode, data);
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
                        firebaseAuthService.setFirebaseUser(fAuth.getCurrentUser(), LoginType.GOOGLE);
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        Animatoo.animateFade(this);  //fire the zoom animation
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
                        firebaseAuthService.setFirebaseUser(fAuth.getCurrentUser(), LoginType.FACEBOOK);
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        Animatoo.animateFade(this);  //fire the zoom animation
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
        mLayoutPassword.setVisibility(View.VISIBLE);
        mLayoutPassword.setEnabled(true);
        slideImageEmail.setVisibility(View.VISIBLE);
        countryAndPhone.setVisibility(View.INVISIBLE);
        countryAndPhone.setEnabled(false);
        slideImagePhone.setVisibility(View.INVISIBLE);
        switchLogin.setChecked(true);
    }

    private void setUIStatePhone() {
        isOTPregistration = true;
        mLayoutEmail.setVisibility(View.INVISIBLE);
        mLayoutEmail.setEnabled(false);
        mLayoutPassword.setVisibility(View.INVISIBLE);
        mLayoutPassword.setEnabled(false);
        slideImageEmail.setVisibility(View.INVISIBLE);
        countryAndPhone.setVisibility(View.VISIBLE);
        countryAndPhone.setEnabled(true);
        slideImagePhone.setVisibility(View.VISIBLE);
        switchLogin.setChecked(false);
    }
}