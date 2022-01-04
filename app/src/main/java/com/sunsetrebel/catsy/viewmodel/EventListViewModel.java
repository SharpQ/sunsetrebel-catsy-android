package com.sunsetrebel.catsy.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;
import com.sunsetrebel.catsy.models.EventModel;
import com.sunsetrebel.catsy.models.UserProfileModel;
import com.sunsetrebel.catsy.repositories.FirebaseFirestoreService;
import com.sunsetrebel.catsy.repositories.UserProfileService;
import java.util.List;

public class EventListViewModel extends ViewModel {
    private LiveData<List<EventModel>> eventList;
    private static FirebaseFirestoreService firebaseFirestoreService;
    private static UserProfileService userProfileService;
    private EventModel selectedEvent;
    private static UserProfileModel userProfileModel;

    public void init() {
        userProfileService = UserProfileService.getInstance();
        userProfileModel = userProfileService.getUserProfile();
        firebaseFirestoreService = FirebaseFirestoreService.getInstance();
        eventList = firebaseFirestoreService.getEventListMutableLiveData();
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
        return userProfileModel.getUserId().equals(event.getHostId());
    }

    public void getEventParticipants(GetEventParticipantsCallback getEventParticipantsCallback, EventModel eventModel) {
        firebaseFirestoreService.getEventParticipants(value -> {
            eventModel.setJoinedUsersList(value);
            getEventParticipantsCallback.onResponse(value);
        }, eventModel);
    }

    public boolean isUserJoinedToEvent(List<String> joinedUsers) {
        for (String user : joinedUsers) {
            if (userProfileModel.getUserId().equals(user)) {
                return true;
            }
        }
        return false;
    }

    public void joinEvent(SetUserInteractEventCallback setUserInteractEventCallback, Context context, EventModel eventModel) {
        firebaseFirestoreService.setUserJoinEvent(value -> {
            setUserInteractEventCallback.onResponse(value);
        }, context, eventModel, userProfileModel.getUserId());
    }

    public void leaveEvent(SetUserInteractEventCallback setUserInteractEventCallback, Context context, EventModel eventModel) {
        firebaseFirestoreService.setUserLeaveEvent(value -> {
            setUserInteractEventCallback.onResponse(value);
        }, context, eventModel, userProfileModel.getUserId());
    }

    public void likeEvent(SetUserInteractEventCallback setUserInteractEventCallback, String eventId) {
        firebaseFirestoreService.setEventAsLikedByUser(value -> {
            if (value) {
                userProfileModel.addLikedEvents(eventId);
            }
            setUserInteractEventCallback.onResponse(value);
        }, userProfileModel.getUserId(), eventId);
    }

    public boolean isEventLikedByUser(String eventId) {
        List<String> likedEvents = userProfileModel.getLikedEvents();
        if (likedEvents.size() > 0) {
            for (String user : likedEvents) {
                if (eventId.equals(user)) {
                    return true;
                }
            }
        }
        return false;
    }

}
