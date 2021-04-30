package com.sunsetrebel.catsy;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import com.sunsetrebel.MapsActivity;

public class Tutorial extends AppCompatActivity {
    Button mSkipBtn;
    Button mSignOut;
    FirebaseAuth firebaseAuth = new FirebaseAuth();

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
            firebaseAuth.signOutFirebase();
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        });
    }
}