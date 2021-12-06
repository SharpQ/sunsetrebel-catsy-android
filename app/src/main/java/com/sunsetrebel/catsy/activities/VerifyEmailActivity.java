package com.sunsetrebel.catsy.activities;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.PhoneAuthCredential;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.repositories.FirebaseAuthService;
import com.sunsetrebel.catsy.utils.LoginType;

import java.util.Locale;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class VerifyEmailActivity extends AppCompatActivity {
    private AppCompatButton resentCodeBtn;
    private TextView textVerifyPhoneDescription, textResetCountdown;
    private ProgressBar progressBar;
    private String email, fullName, verifyDescription;
    private com.google.firebase.auth.FirebaseAuth fAuth;
    private boolean isTutorialNextPage;
    private final FirebaseAuthService firebaseAuthService = FirebaseAuthService.getInstance();
    private static final long START_TIME_IN_MILLIS = 60000;
    private CountDownTimer mCountDownTimer;
    private boolean mTimerRunning = false;
    private long mTimeLeftInMillis = START_TIME_IN_MILLIS;
    private ScheduledExecutorService executorService;

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.parseColor("#00000000"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_email);
        hideSystemUI();
        fAuth = firebaseAuthService.getFirebaseClient();

        progressBar = findViewById(R.id.progressBarVerify);
        textVerifyPhoneDescription = findViewById(R.id.tvVerifyEmailDescription);
        textResetCountdown = findViewById(R.id.textResetCountdown);
        resentCodeBtn = findViewById(R.id.buttonResentLink);
        progressBar.setVisibility(View.GONE);

        email = getIntent().getStringExtra("email");
        fullName = getIntent().getStringExtra("fullName");
        isTutorialNextPage = getIntent().getBooleanExtra("isTutorialNextPage", false);
        verifyDescription = getResources().getString(R.string.verify_email_description) + email;
        textVerifyPhoneDescription.setText(verifyDescription);

        sendVerificationLinkToUser();
        executorService = Executors.newSingleThreadScheduledExecutor();
        executorService.scheduleAtFixedRate(checkEmailRunnable, 0, 5, TimeUnit.SECONDS);

        resentCodeBtn.setOnClickListener(v -> {
            textResetCountdown.setVisibility(View.VISIBLE);
            resentCodeBtn.setEnabled(false);
            resentCodeBtn.setAlpha(0.25f);
            startTimer();
            sendVerificationLinkToUser();
        });

        updateCountDownText();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        executorService.shutdown();
        fAuth.signOut();
    }

    private void sendVerificationLinkToUser() {
        fAuth.getCurrentUser().sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()) {
                    Toast.makeText(VerifyEmailActivity.this, "Please check your email inbox!", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(VerifyEmailActivity.this, "Error sending verification link!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private final Runnable checkEmailRunnable = new Runnable() {
        @Override
        public void run() {
            checkEmailVerification();
        }
    };

    private void checkEmailVerification() {
        fAuth.getCurrentUser().reload();
        if (fAuth.getCurrentUser().isEmailVerified()) {
            executorService.shutdown();
            firebaseAuthService.setFirebaseUser(fAuth.getCurrentUser(), LoginType.EMAIL, fullName);
            Intent intent;
            if (isTutorialNextPage) {
                intent = new Intent(getApplicationContext(), TutorialActivity.class);
            }
            else {
                intent = new Intent(getApplicationContext(), MainActivity.class);
            }
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(intent);
        }
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