package com.sunsetrebel.catsy.viewmodel;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;

import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.enums.NotificationType;
import com.sunsetrebel.catsy.models.InviteToEventModel;
import com.sunsetrebel.catsy.models.InviteToFriendsListModel;
import com.sunsetrebel.catsy.models.MainUserProfileModel;
import com.sunsetrebel.catsy.repositories.FirebaseAuthService;
import com.sunsetrebel.catsy.repositories.FirebaseFirestoreService;
import com.sunsetrebel.catsy.repositories.UserProfileService;
import com.sunsetrebel.catsy.utils.CustomToastUtil;

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
        notificationTypeToDisplay.setValue(NotificationType.ALL);
        if (initialNotificationList == null) {
            initialNotificationList = firebaseFirestoreService.getNotificationsMutableLiveData(mainUserProfileModel.getUserId());
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
        initialNotificationList = null;
    }

    public void setNotificationTypeToDisplay(NotificationType notificationType) {
        notificationTypeToDisplay.setValue(notificationType);
    }

    public void acceptFriendInvite(Context context, InviteToFriendsListModel inviteToFriendsList) {
        firebaseFirestoreService.acceptFriendInvite(value -> {
            if (value) {
                CustomToastUtil.showSuccessToast(context, inviteToFriendsList.getSenderName() + context.getResources().getText(R.string.friend_invite_accept_success).toString());
                Log.d("DEBUG", inviteToFriendsList.getSenderId() + " SUCCESS adding user to friend list!");
            } else {
                CustomToastUtil.showSuccessToast(context, inviteToFriendsList.getSenderName() + context.getResources().getText(R.string.friend_invite_accept_fail).toString());
                Log.d("DEBUG", inviteToFriendsList.getSenderId() + " FAILED to add user to friend list!");
            }
        }, inviteToFriendsList);
    }

    public void declineFriendInvite(InviteToFriendsListModel inviteToFriendsList) {
        firebaseFirestoreService.declineFriendInvite(value -> {
            if (value) {
                Log.d("DEBUG", inviteToFriendsList.getSenderId() + " SUCCESS decline user friend request!");
            } else {
                Log.d("DEBUG", inviteToFriendsList.getSenderId() + " FAILED decline user friend request!");
            }
        }, inviteToFriendsList);
    }

    public void acceptEventInvite(Context context, InviteToEventModel inviteToEventModel) {
        firebaseFirestoreService.acceptEventInvite(value -> {
            if (value) {
                Log.d("DEBUG", "You joined event: " + inviteToEventModel.getEventTitle() + "!");
                CustomToastUtil.showSuccessToast(context, context.getResources().getString(R.string.event_detailed_join_success) + inviteToEventModel.getEventTitle() + "!");
            } else {
                Log.d("DEBUG", "Failed to join event: " + inviteToEventModel.getEventTitle() + "!");
                CustomToastUtil.showFailToast(context, context.getResources().getString(R.string.event_detailed_join_fail) + inviteToEventModel.getEventTitle() + "!");
            }
        }, mainUserProfileModel, inviteToEventModel);
    }

    public void declineEventInvite(InviteToEventModel inviteToEventModel) {
        firebaseFirestoreService.declineEventInvite(value -> {
            if (value) {
                Log.d("DEBUG", "SUCCESS decline event invite: " + inviteToEventModel.getEventId() + "!");
            } else {
                Log.d("DEBUG", "FAIL decline event invite: " + inviteToEventModel.getEventId() + "!");
            }
        }, inviteToEventModel);
    }
}
