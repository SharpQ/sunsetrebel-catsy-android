package com.sunsetrebel.catsy.activities;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.GoogleAuthProvider;
import com.sunsetrebel.catsy.utils.FirebaseAuthService;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.adapters.SliderAdapter;
import com.sunsetrebel.catsy.utils.FirebaseFirestoreService;

public class OnboardingActivity extends AppCompatActivity {

    private LinearLayout mDotLayout;
    private ViewPager mSlideViewPager;
    private SliderAdapter sliderAdapter;
    private Button mToRegisterBtn;
    private Button mToLoginBtn;
    private Button mGoogleAuthBtn;
    private int RC_SIGN_IN;
    private com.google.firebase.auth.FirebaseAuth fAuth;
    private final FirebaseAuthService firebaseAuthService = new FirebaseAuthService();
    private final FirebaseFirestoreService firebaseFirestoreService = new FirebaseFirestoreService();
    private Activity mActivity;

    @Override
    protected void onStart() {
        super.onStart();
        if(firebaseAuthService.checkCurrentUser()) {
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            Animatoo.animateFade(this);  //fire the zoom animation
            finish();
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_onboarding);
        mActivity = OnboardingActivity.this;
        firebaseAuthService.createGoogleAuthRequestGetInstance(getApplicationContext());
        fAuth = firebaseAuthService.getInstance();

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
            startActivity(new Intent(getApplicationContext(), RegistrationActivity.class));
            Animatoo.animateFade(this);  //fire the zoom animation

        });

        mToLoginBtn.setOnClickListener(v -> {
            startActivity(new Intent(getApplicationContext(), LoginActivity.class));
            Animatoo.animateFade(this);  //fire the zoom animation
        });

        mGoogleAuthBtn.setOnClickListener(v -> {
                    RC_SIGN_IN = FirebaseAuthService.getRCSignIn();
                    Intent signInIntent = firebaseAuthService.signInGoogle(getApplicationContext());
                    startActivityForResult(signInIntent, RC_SIGN_IN);
                    Animatoo.animateFade(this);  //fire the zoom animation
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
        GoogleSignInAccount account = firebaseAuthService.onFirebaseResponse(requestCode, data);
        if (account != null) {
            firebaseAuthWithGoogle(account.getIdToken());
        }
    }

    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        fAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, update UI with the signed-in user's information
                        firebaseAuthService.setFirebaseUser(fAuth.getCurrentUser());
                        firebaseFirestoreService.getUserInFirestore(value -> {
                            if(!value) {
                                firebaseFirestoreService.createNewUserByGoogle(fAuth.getCurrentUser().getUid(), fAuth.getCurrentUser().getDisplayName(), fAuth.getCurrentUser().getEmail(),
                                        fAuth.getCurrentUser().getPhoneNumber(), fAuth.getCurrentUser().getPhotoUrl().toString());
                            }
                        }, fAuth.getCurrentUser().getUid());
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        Animatoo.animateFade(this);  //fire the zoom animation
                        finish();
                    } else {
                        restartActivity(mActivity);
                        Toast.makeText(getApplicationContext(), "Google authentication failed!", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private static void restartActivity(Activity activity) {
        activity.recreate();
    }
}