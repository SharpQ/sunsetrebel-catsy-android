package com.sunsetrebel.catsy;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;
import com.sunsetrebel.MapsActivity;

import java.util.Objects;
import java.util.concurrent.TimeUnit;

public class VerifyPhone extends AppCompatActivity {
    private Button verifyBtn;
    private EditText inputCode;
    private ProgressBar progressBar;
    private String phoneNumber;
    private com.google.firebase.auth.FirebaseAuth fAuth;
    private String systemVerificationCode;
    private final FirebaseAuth firebaseAuth = new FirebaseAuth();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_verify_phone);
        fAuth = firebaseAuth.getFAuth();

        verifyBtn = findViewById(R.id.buttonVerify);
        inputCode = findViewById(R.id.editSmsCode);
        progressBar = findViewById(R.id.progressBarVerify);
        progressBar.setVisibility(View.GONE);

        phoneNumber = getIntent().getStringExtra("phoneNumber");
        sendVerificationCodeToUser(phoneNumber);
        verifyBtn.setOnClickListener(v -> {
            String code = inputCode.getText().toString();
            if (code.isEmpty() || code.length() <6) {
                inputCode.setError("Please enter valid code");
                inputCode.requestFocus();
                return;
            }
            progressBar.setVisibility(View.VISIBLE);
            verifyCode(code);
        });
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

    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
        @Override
        public void onCodeSent(@NonNull String s, @NonNull PhoneAuthProvider.ForceResendingToken forceResendingToken) {
            super.onCodeSent(s, forceResendingToken);
            systemVerificationCode = s;
        }

        @Override
        public void onVerificationCompleted(@NonNull PhoneAuthCredential phoneAuthCredential) {
            String code = phoneAuthCredential.getSmsCode();
            if(code!=null) {
                progressBar.setVisibility(View.VISIBLE);
                verifyCode(code);
            }
        }

        @Override
        public void onVerificationFailed(@NonNull FirebaseException e) {
            Toast.makeText(VerifyPhone.this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    };

    private void verifyCode(String userInputCode) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(systemVerificationCode, userInputCode);
        signInUserByCredentials(credential);
    }

    private void signInUserByCredentials(PhoneAuthCredential credential){
        fAuth.signInWithCredential(credential).addOnCompleteListener(VerifyPhone.this, task -> {
            if(task.isSuccessful()){
                Intent intent = new Intent(getApplicationContext(), Tutorial.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
            } else {
                Toast.makeText(VerifyPhone.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}