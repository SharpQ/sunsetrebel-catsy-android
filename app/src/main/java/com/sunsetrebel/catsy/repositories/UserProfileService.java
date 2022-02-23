package com.sunsetrebel.catsy.repositories;

import com.sunsetrebel.catsy.models.MainUserProfileModel;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UserProfileService {
    private final FirebaseAuthService firebaseAuthService = FirebaseAuthService.getInstance();
    private final FirebaseFirestoreService firebaseFirestoreService = FirebaseFirestoreService.getInstance();
    private com.google.firebase.auth.FirebaseAuth fAuth;
    private static MainUserProfileModel mainUserProfileModel;
    private static UserProfileService instance;
    private ScheduledExecutorService executorService;

    public UserProfileService() {
        fAuth = firebaseAuthService.getFirebaseClient();
        if (mainUserProfileModel == null) {
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

    public MainUserProfileModel getUserProfile() {
        return mainUserProfileModel;
    }

    public void setUserProfile(MainUserProfileModel mainUserProfileModel) {
        UserProfileService.mainUserProfileModel = mainUserProfileModel;
    }

    public void updateUserProfile() {
        firebaseFirestoreService.getUserProfile(userProfile -> setUserProfile(userProfile), fAuth.getUid());
    }

    public void removeInstance() {
        setUserProfile(null);
        instance = null;
    }
}
