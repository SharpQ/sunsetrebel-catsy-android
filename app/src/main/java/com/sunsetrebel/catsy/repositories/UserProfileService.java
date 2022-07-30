package com.sunsetrebel.catsy.repositories;

import androidx.lifecycle.LiveData;

import com.sunsetrebel.catsy.models.MainUserProfileModel;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class UserProfileService {
    private FirebaseFirestoreService firebaseFirestoreService;
    private static MainUserProfileModel mainUserProfileModel;
    private static UserProfileService instance;
    private ScheduledExecutorService executorService;
    private LiveData<MainUserProfileModel> mainUserProfileModelLiveData;
    private FirebaseAuthService firebaseAuthService;

    public interface GetUpdatedUserProfileCallback {
        void onResponse(MainUserProfileModel userProfile);
    }

    public UserProfileService() {
        firebaseAuthService = FirebaseAuthService.getInstance();
        firebaseFirestoreService = FirebaseFirestoreService.getInstance();
//        if (mainUserProfileModel == null) {
//            executorService = Executors.newSingleThreadScheduledExecutor();
//            executorService.scheduleAtFixedRate(getUserProfileRunnable, 0, 1, TimeUnit.SECONDS);
//        }
        if (mainUserProfileModelLiveData == null) {
            mainUserProfileModelLiveData = firebaseFirestoreService.getMainUserProfileMutableLiveData(firebaseAuthService.getFirebaseClient().getUid());
        }
    }

//    private final Runnable getUserProfileRunnable = new Runnable() {
//        @Override
//        public void run() {
//            firebaseFirestoreService.getUserProfile(userProfile -> {
//                if (userProfile != null) {
//                    setUserProfile(userProfile);
//                    executorService.shutdown();
//                }
//            }, fAuth.getUid());
//        }
//    };

    public void removeMainProfileListener() {
        if (mainUserProfileModelLiveData != null) {
            firebaseFirestoreService.removeMainUserProfileListener();
        }
        mainUserProfileModelLiveData = null;
    }

    public static UserProfileService getInstance() {
        if (instance == null) {
            instance = new UserProfileService();
        }
        return instance;
    }

    public MainUserProfileModel getUserProfile() {
        return mainUserProfileModelLiveData.getValue();
    }

    public LiveData<MainUserProfileModel> getUserProfileModelLiveData() {
        return mainUserProfileModelLiveData;
    }

    public void setUserProfile(MainUserProfileModel mainUserProfileModel) {
        UserProfileService.mainUserProfileModel = mainUserProfileModel;
    }

    public void getUpdateUserProfile(GetUpdatedUserProfileCallback getUpdatedUserProfileCallback) {
        firebaseFirestoreService.getUserProfile(userProfile -> {
            setUserProfile(userProfile);
            getUpdatedUserProfileCallback.onResponse(userProfile);
        }, firebaseAuthService.getFirebaseClient().getUid());
    }

    public void removeInstance() {
        removeMainProfileListener();
        setUserProfile(null);
        instance = null;
        firebaseAuthService.signOutFirebase();
    }
}
