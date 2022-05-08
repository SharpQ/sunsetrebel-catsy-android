package com.sunsetrebel.catsy.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.sunsetrebel.catsy.enums.NotificationType;
import com.sunsetrebel.catsy.models.EventModel;
import com.sunsetrebel.catsy.models.InviteToEventModel;
import com.sunsetrebel.catsy.models.InviteToFriendsListModel;
import com.sunsetrebel.catsy.models.MainUserProfileModel;
import com.sunsetrebel.catsy.repositories.FirebaseAuthService;
import com.sunsetrebel.catsy.repositories.FirebaseFirestoreService;
import com.sunsetrebel.catsy.repositories.UserProfileService;

import java.util.ArrayList;
import java.util.List;

public class ProfileViewModel extends ViewModel {
    private MainUserProfileModel mainUserProfileModel;
    private FirebaseAuthService firebaseAuthService;
    private UserProfileService userProfileService;
    private static FirebaseFirestoreService firebaseFirestoreService;
    private LiveData<List<Object>> initialNotificationList;
    private MediatorLiveData<List<Object>> filteredNotificationList = new MediatorLiveData<>();
    private MutableLiveData<Enum<NotificationType>> notificationTypeToDisplay = new MutableLiveData<>();

    public void init() {
        firebaseAuthService = FirebaseAuthService.getInstance();
        userProfileService = UserProfileService.getInstance();
        mainUserProfileModel = userProfileService.getUserProfile();
        firebaseFirestoreService = FirebaseFirestoreService.getInstance();
        initialNotificationList = firebaseFirestoreService.getNotificationsMutableLiveData(mainUserProfileModel.getUserId());
        notificationTypeToDisplay.setValue(NotificationType.ALL);
        filteredNotificationList.addSource(initialNotificationList, new Observer<List<Object>>() {
            @Override
            public void onChanged(List<Object> notificationList) {
                combine(notificationList, notificationTypeToDisplay.getValue());
            }
        });

        filteredNotificationList.addSource(notificationTypeToDisplay, new Observer<Enum<NotificationType>>() {
            @Override
            public void onChanged(Enum<NotificationType> notificationTypeToDisplay) {
                combine(initialNotificationList.getValue(), notificationTypeToDisplay);
            }
        });
    }

    private void combine(List<Object> inviteList, Enum<NotificationType> notificationTypeToDisplay) {
        if (inviteList == null) {
            return;
        }

        if (notificationTypeToDisplay == NotificationType.ALL) {
            filteredNotificationList.setValue(inviteList);
        } else {
            List<Object> inviteListSorted = new ArrayList<>();
            for (Object notification : inviteList) {
                if (notificationTypeToDisplay == NotificationType.ADD_FRIEND &&
                        notification instanceof InviteToFriendsListModel) {
                    inviteListSorted.add(notification);
                } else if (notificationTypeToDisplay == NotificationType.EVENT_INVITE &&
                        notification instanceof InviteToEventModel) {
                     inviteListSorted.add(notification);
                }
            }
            filteredNotificationList.setValue(inviteListSorted);
        }
    }

    public void logoutUser() {
        removeNotificationsListener();
        firebaseAuthService.signOutFirebase();
        userProfileService.removeInstance();
    }

    public MainUserProfileModel getUserProfile() {
        return mainUserProfileModel;
    }

    public LiveData<List<Object>> getNotificationsLiveData() {
        return filteredNotificationList;
    }

    public void removeNotificationsListener() {
        firebaseFirestoreService.removeNotificationsListener();
        filteredNotificationList.removeSource(initialNotificationList);
        filteredNotificationList.removeSource(notificationTypeToDisplay);
    }
}
