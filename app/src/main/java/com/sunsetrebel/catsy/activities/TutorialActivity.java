package com.sunsetrebel.catsy.activities;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;

import android.content.Intent;
import android.os.Bundle;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.sunsetrebel.catsy.repositories.FirebaseAuthService;
import com.sunsetrebel.catsy.R;

public class TutorialActivity extends AppCompatActivity {
    private AppCompatButton mSkipBtn;
    private AppCompatButton mSignOut;
    private final FirebaseAuthService firebaseAuthService = FirebaseAuthService.getInstance();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutorial);
        mSkipBtn = findViewById(R.id.skipTutorial);
        mSignOut = findViewById(R.id.buttonLogout);

        mSkipBtn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            Animatoo.animateFade(this);  //fire the zoom animation
            finish();
        });

        mSignOut.setOnClickListener(v -> {
            firebaseAuthService.signOutFirebase();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            Animatoo.animateFade(this);  //fire the zoom animation
            finish();
        });
    }
}