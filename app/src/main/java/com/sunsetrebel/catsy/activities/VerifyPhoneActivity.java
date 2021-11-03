package com.sunsetrebel.catsy.activities;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.sunsetrebel.catsy.repositories.FirebaseAuthService;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.repositories.FirebaseFirestoreService;
import com.sunsetrebel.catsy.utils.LoginType;

import java.util.Locale;
import java.util.concurrent.TimeUnit;

public class VerifyPhoneActivity extends AppCompatActivity {
    private AppCompatButton verifyBtn, resentCodeBtn;
    private TextInputLayout inputLayoutCode;
    private TextInputEditText inputEditCode;
    private TextView textVerifyPhoneDescription, textResetCountdown;
    private ProgressBar progressBar;
    private String phoneNumber, fullName, systemVerificationCode, verifyDescription;
    private com.google.firebase.auth.FirebaseAuth fAuth;
    private boolean isTutorialNextPage;
    private final FirebaseAuthService firebaseAuthService = FirebaseAuthService.getInstance();
    private Activity mActivity;
    private static final long START_TIME_IN_MILLIS = 60000;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning = false;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        mActivity = VerifyPhoneActivity.this;
        fAuth = firebaseAuthService.getFirebaseClient();

        verifyBtn = findViewById(R.id.buttonVerify);
        inputLayoutCode = findViewById(R.id.inputLayoutSmsCode);
        inputEditCode = findViewById(R.id.inputEditSmsCode);
        progressBar = findViewById(R.id.progressBarVerify);
        textVerifyPhoneDescription = findViewById(R.id.textVerifyPhoneDescription);
        textResetCountdown = findViewById(R.id.textResetCountdown);
        resentCodeBtn = findViewById(R.id.buttonResentCode);
        progressBar.setVisibility(View.GONE);

        phoneNumber = getIntent().getStringExtra("phoneNumber");
        fullName = getIntent().getStringExtra("fullName");
        verifyDescription = getResources().getString(R.string.verify_phone_description) + phoneNumber;
        textVerifyPhoneDescription.setText(verifyDescription);
        isTutorialNextPage = getIntent().getBooleanExtra("isTutorialNextPage", false);
        sendVerificationCodeToUser(phoneNumber);

        resentCodeBtn.setOnClickListener(v -> {
            textResetCountdown.setVisibility(View.VISIBLE);
            resentCodeBtn.setEnabled(false);
            resentCodeBtn.setAlpha(0.25f);
            startTimer();
            sendVerificationCodeToUser(phoneNumber);
        });

        verifyBtn.setOnClickListener(v -> {
            String code = inputEditCode.getText().toString();
            if (code.isEmpty() || code.length() <6) {
                inputLayoutCode.setError("Please enter valid code");
                inputEditCode.requestFocus();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            verifyCode(code);
        });

        updateCountDownText();
    }

    private void sendVerificationCodeToUser(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(fAuth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity (for callback binding)
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    private final PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            systemVerificationCode = s;
        }

        @Override
        public void onVerificationCompleted(PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if (code != null) {
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(FirebaseException e) {
            Toast.makeText(VerifyPhoneActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String userInputCode) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(systemVerificationCode, userInputCode);
        if (credential != null) {
            signInUserByCredentials(credential);
        }
    }

    private void signInUserByCredentials(PhoneAuthCredential credential){
        fAuth.signInWithCredential(credential).addOnCompleteListener(VerifyPhoneActivity.this, task -> {
            if (task.isSuccessful()){
                firebaseAuthService.setFirebaseUser(fAuth.getCurrentUser(), LoginType.PHONE);
                Intent intent;
                if (isTutorialNextPage) {
                    intent = new Intent(getApplicationContext(), TutorialActivity.class);
                }
                else {
                    intent = new Intent(getApplicationContext(), MainActivity.class);
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Toast.makeText(VerifyPhoneActivity.this, "OTP authentication failed!", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void startTimer() {
        mCountDownTimer = new CountDownTimer(mTimeLeftInMillis, 1000) {
            @Override
            public void onTick(long millisUntilFinished) {
                mTimeLeftInMillis = millisUntilFinished;
                updateCountDownText();
            }

            @Override
            public void onFinish() {
                mTimerRunning = false;
                textResetCountdown.setVisibility(View.INVISIBLE);
                resentCodeBtn.setEnabled(true);
                resentCodeBtn.setAlpha(1f);
                mTimeLeftInMillis = START_TIME_IN_MILLIS;
            }
        }.start();
        mTimerRunning = true;
    }

    private void updateCountDownText() {
        int seconds = (int) (mTimeLeftInMillis) / 1000 % 60;
        String timeLeftFormatted = String.format(Locale.getDefault(), ":%02d", seconds);
        textResetCountdown.setText(timeLeftFormatted);
    }

    private void restartActivity(Activity activity) {
        activity.recreate();
    }
}