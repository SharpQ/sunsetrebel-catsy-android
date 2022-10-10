package com.sunsetrebel.catsy.repositories;

import androidx.lifecycle.LiveData;
import com.sunsetrebel.catsy.models.MainUserProfileModel;


public class UserProfileService {
    private FirebaseFirestoreService firebaseFirestoreService;
    private static UserProfileService instance;
    private LiveData<MainUserProfileModel> mainUserProfileModelLiveData;
    private FirebaseAuthService firebaseAuthService;


    public UserProfileService() {
        firebaseAuthService = FirebaseAuthService.getInstance();
        firebaseFirestoreService = FirebaseFirestoreService.getInstance();
        if (mainUserProfileModelLiveData == null) {
            mainUserProfileModelLiveData = firebaseFirestoreService.getMainUserProfileMutableLiveData(firebaseAuthService.getFirebaseClient().getUid());
        }
    }

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

    public void removeInstance() {
        removeMainProfileListener();
        instance = null;
        firebaseAuthService.signOutFirebase();
    }
}
