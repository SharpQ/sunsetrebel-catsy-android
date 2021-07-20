package com.sunsetrebel.catsy.activities;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.sunsetrebel.catsy.utils.FirebaseAuthService;
import com.sunsetrebel.catsy.R;

public class TutorialActivity extends AppCompatActivity {
    private Button mSkipBtn;
    private Button mSignOut;
    private final FirebaseAuthService firebaseAuthService = new FirebaseAuthService();

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