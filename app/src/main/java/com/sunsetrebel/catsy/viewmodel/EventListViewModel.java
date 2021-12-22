package com.sunsetrebel.catsy.viewmodel;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.google.firebase.auth.FirebaseAuth;
import com.sunsetrebel.catsy.models.EventModel;
import com.sunsetrebel.catsy.repositories.FirebaseAuthService;
import com.sunsetrebel.catsy.repositories.FirebaseFirestoreService;

import java.util.List;

public class EventListViewModel extends ViewModel {
    private LiveData<List<EventModel>> eventList;
    private FirebaseFirestoreService firebaseFirestoreService;
    private FirebaseAuthService firebaseAuthService;
    private EventModel selectedEvent;

    public void init() {
        firebaseFirestoreService = FirebaseFirestoreService.getInstance();
        eventList = firebaseFirestoreService.getEventListMutableLiveData();
        firebaseAuthService = FirebaseAuthService.getInstance();
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
        FirebaseAuth fAuth = firebaseAuthService.getFirebaseClient();
        return fAuth.getCurrentUser().getUid().equals(event.getUserID());
    }

    public boolean isUserJoinedToEvent(List<String> joinedUsers) {
        FirebaseAuth fAuth = firebaseAuthService.getFirebaseClient();
        String userId = fAuth.getCurrentUser().getUid();
        for (String user : joinedUsers) {
            if (userId.equals(user)) {
                return true;
            }
        }
        return false;
    }

    public void joinEvent() {

    }

    public void leaveEvent() {

    }
}
