package com.sunsetrebel.catsy;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.sunsetrebel.MapsActivity;

public class Onboarding extends AppCompatActivity {

    private LinearLayout mDotLayout;
    private ViewPager mSlideViewPager;
    private SliderAdapter sliderAdapter;
    private Button mToRegisterBtn;
    private Button mToLoginBtn;
    private Button mGoogleAuthBtn;
    private int RC_SIGN_IN;
    private com.google.firebase.auth.FirebaseAuth fAuth;
    private final FirebaseAuth firebaseAuth = new FirebaseAuth();

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseAuth.checkCurrentUser()) {
            startActivity(new Intent(getApplicationContext(), MapsActivity.class));
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        firebaseAuth.createGoogleAuthRequestGetInstance(getApplicationContext());
        fAuth = firebaseAuth.getFAuth();

        mSlideViewPager = findViewById(R.id.slideViewPager);
        mDotLayout = findViewById(R.id.dotsLayout);
        mToRegisterBtn = findViewById(R.id.buttonGoToRegister);
        mToLoginBtn = findViewById(R.id.buttonGoToLogin);
        mGoogleAuthBtn = findViewById(R.id.buttonGoToGoogle);

        sliderAdapter = new SliderAdapter(this);
        mSlideViewPager.setAdapter(sliderAdapter);
        addDotsIndicator(0);
        mSlideViewPager.addOnPageChangeListener(viewListener);

        mToRegisterBtn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Registration.class));
            finish();
        });

        mToLoginBtn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), Login.class));
            finish();
        });

        mGoogleAuthBtn.setOnClickListener(v -> {
                    RC_SIGN_IN = FirebaseAuth.getRCSignIn();
                    Intent signInIntent = firebaseAuth.signInGoogle(getApplicationContext());
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                }
        );
    }

    public void addDotsIndicator(int position) {
        TextView[] mDots = new TextView[3];
        mDotLayout.removeAllViews();

        for (int i = 0; i< mDots.length; i++) {
            mDots[i] = new TextView(this);
            mDots[i].setText(Html.fromHtml("&#8226;"));
            mDots[i].setTextSize(35);
            mDots[i].setGravity(View.TEXT_ALIGNMENT_CENTER);
            mDots[i].setTextColor(getResources().getColor(R.color.primaryLightColor));
            mDotLayout.addView(mDots[i]);
        }
        mDots[position].setTextColor(getResources().getColor(R.color.primaryTextColor));
    }

    ViewPager.OnPageChangeListener viewListener = new ViewPager.OnPageChangeListener() {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            addDotsIndicator(position);
        }

        @Override
        public void onPageScrollStateChanged(int state) {

        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        GoogleSignInAccount account = firebaseAuth.onFirebaseResponse(requestCode, data);
        firebaseAuthWithGoogle(account.getIdToken());
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        firebaseAuth.setFirebaseUser(fAuth.getCurrentUser());
                        startActivity(new Intent(getApplicationContext(), MapsActivity.class));
                    } else {
                        Toast.makeText(getApplicationContext(), "Google authentication failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }
}