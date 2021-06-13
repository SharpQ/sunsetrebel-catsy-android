package com.sunsetrebel.catsy.activities;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.sunsetrebel.catsy.utils.FirebaseAuthService;
import com.sunsetrebel.catsy.R;

public class TutorialActivity extends AppCompatActivity {
    private Button mSkipBtn;
    private Button mSignOut;
    private final FirebaseAuthService firebaseAuthService = new FirebaseAuthService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.Theme_Catsy);
        setContentView(R.layout.activity_tutorial);
        mSkipBtn = findViewById(R.id.skipTutorial);
        mSignOut = findViewById(R.id.buttonLogout);

        mSkipBtn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
            finish();
        });

        mSignOut.setOnClickListener(v -> {
            firebaseAuthService.signOutFirebase();
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            finish();
        });
    }
}