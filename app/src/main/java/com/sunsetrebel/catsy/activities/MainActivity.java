package com.sunsetrebel.catsy.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.fragments.AccountFragment;
import com.sunsetrebel.catsy.fragments.NewEventPrimaryFragment;
import com.sunsetrebel.catsy.fragments.EventListFragment;
import com.sunsetrebel.catsy.fragments.MapsFragment;
import com.sunsetrebel.catsy.utils.MediaPlayerService;

import nl.joery.animatedbottombar.AnimatedBottomBar;



public class MainActivity extends AppCompatActivity {
    AnimatedBottomBar animatedBottomBar;
    FragmentManager fragmentManager;

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.parseColor("#00000000"));
    }

    private void showSystemUI() {
        getWindow().getDecorView().setSystemUiVisibility(0);
        getWindow().setStatusBarColor(getResources().getColor(R.color.primaryDarkColor));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        MediaPlayerService.playIntro(this);
        hideSystemUI();

        Fragment mapsfragment = new MapsFragment();
        fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.frameLayoutMain, mapsfragment)
                .commit();

        animatedBottomBar = findViewById(R.id.animatedBottomBar);
        animatedBottomBar.selectTabById(R.id.navigationBarMap, true);
        animatedBottomBar.setOnTabSelectListener((lastIndex, lastTab, newIndex, newTab) -> {
            Fragment fragment = null;
            switch (newTab.getId()) {
                case R.id.navigationBarMap:
                    fragment = new MapsFragment();
                    break;
                case R.id.navigationBarEventList:
                    fragment = new EventListFragment();
                    break;
                case R.id.navigationBarNewEvent:
                    fragment = new NewEventPrimaryFragment();
                    break;
                case R.id.navigationBarAccount:
                    fragment = new AccountFragment();
                    break;
            }
            fragmentManager.beginTransaction().replace(R.id.frameLayoutMain, fragment).commit();
            MediaPlayerService.playNavigation(this);
        });
    }
}
