package com.sunsetrebel.catsy.activities;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.repositories.FirebaseAuthService;
import com.sunsetrebel.catsy.utils.CustomToastUtil;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ForgotEmailActivity extends AppCompatActivity {
    private AppCompatButton resetPasswordBtn;
    private TextInputLayout inputLayoutEmail;
    private TextInputEditText inputEditEmail;
    private ProgressBar progressBar;
    private com.google.firebase.auth.FirebaseAuth fAuth;
    private final FirebaseAuthService firebaseAuthService = FirebaseAuthService.getInstance();

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
        setContentView(R.layout.activity_forgot_password);
        hideSystemUI();
        fAuth = firebaseAuthService.getFirebaseClient();

        progressBar = findViewById(R.id.pb_forgot_password);
        resetPasswordBtn = findViewById(R.id.button_reset_password);
        inputLayoutEmail = findViewById(R.id.til_forgot_email);
        inputEditEmail = findViewById(R.id.tiet_forgot_email);
        progressBar.setVisibility(View.GONE);

        resetPasswordBtn.setOnClickListener(v -> {
            String email = inputEditEmail.getText().toString();
            if(!validateEmail(email)) {
                return;
            }
            progressBar.setVisibility(View.VISIBLE);

            fAuth.sendPasswordResetEmail(email).addOnSuccessListener(aVoid -> {
                CustomToastUtil.showSuccessToast(this, getResources().getText(R.string.forgot_password_success_notific).toString());
                Log.d("DEBUG", getResources().getText(R.string.forgot_password_success_notific).toString());
                Intent intent = new Intent(getApplicationContext(), LoginActivity.class);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
                startActivity(intent);
                Animatoo.animateFade(this);  //fire the zoom animation
            }).addOnFailureListener(e -> {
                CustomToastUtil.showFailToast(this, getResources().getText(R.string.forgot_password_fail_notific).toString());
                Log.d("DEBUG", getResources().getText(R.string.forgot_password_fail_notific).toString());
            });
        });

    }

    private boolean validateEmail(String email) {
        if (TextUtils.isEmpty(email)) {
            inputLayoutEmail.setError("Please enter email");
            inputEditEmail.requestFocus();
            return false;
        } else if (!checkEmailFormat(email)) {
            inputLayoutEmail.setError("Email is incorrect");
            inputEditEmail.requestFocus();
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

}