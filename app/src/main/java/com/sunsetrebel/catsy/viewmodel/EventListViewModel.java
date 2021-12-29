package com.sunsetrebel.catsy.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.sunsetrebel.catsy.models.EventModel;
import com.sunsetrebel.catsy.repositories.FirebaseAuthService;
import com.sunsetrebel.catsy.repositories.FirebaseFirestoreService;

import java.util.List;
import java.util.Locale;

public class EventListViewModel extends ViewModel {
    private LiveData<List<EventModel>> eventList;
    private FirebaseFirestoreService firebaseFirestoreService;
    private FirebaseAuthService firebaseAuthService;
    private EventModel selectedEvent;
    private String userId;

    public void init() {
        firebaseFirestoreService = FirebaseFirestoreService.getInstance();
        eventList = firebaseFirestoreService.getEventListMutableLiveData();
        firebaseAuthService = FirebaseAuthService.getInstance();
        userId = firebaseAuthService.getFirebaseClient().getCurrentUser().getUid();
    }

    public interface SetUserInteractEventCallback {
        void onResponse(Boolean isResponseSuccessful);
    }

    public interface GetEventParticipantsCallback {
        void onResponse(List<String> value);
    }

    public LiveData<List<EventModel>> getLiveEventListData() {
        return eventList;
    }

    public EventModel getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(EventModel selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    public void removeEventListListener() {
        firebaseFirestoreService.removeEventListListener();
    }

    public boolean isUserEventHost(EventModel event) {
        return userId.equals(event.getHostId());
    }

    public void getEventParticipants(GetEventParticipantsCallback getEventParticipantsCallback, EventModel eventModel) {
        firebaseFirestoreService.getEventParticipants(value -> {
            eventModel.setJoinedUsersList(value);
            getEventParticipantsCallback.onResponse(value);
        }, eventModel);
    }

    public boolean isUserJoinedToEvent(List<String> joinedUsers) {
        for (String user : joinedUsers) {
            if (userId.equals(user)) {
                return true;
            }
        }
        return false;
    }

    public void joinEvent(SetUserInteractEventCallback setUserInteractEventCallback, Context context, EventModel eventModel) {
        firebaseFirestoreService.setUserJoinEvent(value -> {
            setUserInteractEventCallback.onResponse(value);
        }, context, eventModel, userId);
    }

    public void leaveEvent(SetUserInteractEventCallback setUserInteractEventCallback, Context context, EventModel eventModel) {
        firebaseFirestoreService.setUserLeaveEvent(value -> {
            setUserInteractEventCallback.onResponse(value);
        }, context, eventModel, userId);
    }
}
