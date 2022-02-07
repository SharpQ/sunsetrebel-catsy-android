package com.sunsetrebel.catsy.repositories;

import android.util.Log;

import com.sunsetrebel.catsy.models.UserProfileModel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UserProfileService {
    private final FirebaseAuthService firebaseAuthService = FirebaseAuthService.getInstance();
    private final FirebaseFirestoreService firebaseFirestoreService = FirebaseFirestoreService.getInstance();
    private com.google.firebase.auth.FirebaseAuth fAuth;
    private static UserProfileModel userProfileModel;
    private static UserProfileService instance;
    private ScheduledExecutorService executorService;

    public UserProfileService() {
        fAuth = firebaseAuthService.getFirebaseClient();
        if (userProfileModel == null) {
            executorService = Executors.newSingleThreadScheduledExecutor();
            executorService.scheduleAtFixedRate(getUserProfileRunnable, 0, 2, TimeUnit.SECONDS);
        }
    }

    private final Runnable getUserProfileRunnable = new Runnable() {
        @Override
        public void run() {
            firebaseFirestoreService.getUserProfile(userProfile -> {
                if (userProfile != null) {
                    setUserProfile(userProfile);
                    executorService.shutdown();
                }
            }, fAuth.getUid());
        }
    };

    public static UserProfileService getInstance() {
        if (instance == null) {
            instance = new UserProfileService();
        }
        return instance;
    }

    public UserProfileModel getUserProfile() {
        return userProfileModel;
    }

    public void setUserProfile(UserProfileModel userProfileModel) {
        UserProfileService.userProfileModel = userProfileModel;
    }

    public void updateUserProfile() {
        firebaseFirestoreService.getUserProfile(userProfile -> setUserProfile(userProfile), fAuth.getUid());
    }

    public void removeInstance() {
        setUserProfile(null);
        instance = null;
    }
}
