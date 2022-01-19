package com.sunsetrebel.catsy.viewmodel;

import android.content.Context;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MediatorLiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModel;
import com.sunsetrebel.catsy.models.EventModel;
import com.sunsetrebel.catsy.models.UserProfileModel;
import com.sunsetrebel.catsy.repositories.FirebaseFirestoreService;
import com.sunsetrebel.catsy.repositories.UserProfileService;

import java.util.ArrayList;
import java.util.List;

public class EventListViewModel extends ViewModel {
    private MediatorLiveData<List<EventModel>> filteredEventList = new MediatorLiveData<>();
    private MutableLiveData<String> searchRequest = new MutableLiveData<>();
    private LiveData<List<EventModel>> initialEventList;
    private static FirebaseFirestoreService firebaseFirestoreService;
    private EventModel selectedEvent;
    private static UserProfileModel userProfileModel;

    public interface SetUserInteractEventCallback {
        void onResponse(Boolean isResponseSuccessful);
    }

    public interface GetEventParticipantsCallback {
        void onResponse(List<String> value);
    }

    public void init() {
        UserProfileService userProfileService = UserProfileService.getInstance();
        userProfileModel = userProfileService.getUserProfile();
        firebaseFirestoreService = FirebaseFirestoreService.getInstance();
        initialEventList = firebaseFirestoreService.getEventListMutableLiveData();
        filteredEventList.addSource(initialEventList, new Observer<List<EventModel>>() {
            @Override
            public void onChanged(List<EventModel> eventModels) {
                combine(eventModels, searchRequest.getValue());
            }
        });

        filteredEventList.addSource(searchRequest, new Observer<String>() {
            @Override
            public void onChanged(String search) {
                combine(initialEventList.getValue(), search);
            }
        });
    }

    public void setSearchMutableLiveData(String search) {
        searchRequest.setValue(search);
    }

    private void combine(List<EventModel> eventModels, String searchRequest) {
        if (eventModels == null) {
            return;
        }

        if (searchRequest != null) {
            List<EventModel> eventModelsSort = new ArrayList<>();
            for (EventModel event : eventModels) {
                if (event.getEventTitle().contains(searchRequest) || event.getHostName().contains(searchRequest)
                        || event.getEventDescr().contains(searchRequest)) {
                    eventModelsSort.add(event);
                }
            }
            filteredEventList.setValue(eventModelsSort);
        } else {
            filteredEventList.setValue(eventModels);
        }
    }

    public LiveData<List<EventModel>> getLiveEventListData() {
        return filteredEventList;
    }

    public EventModel getSelectedEvent() {
        return selectedEvent;
    }

    public void setSelectedEvent(EventModel selectedEvent) {
        this.selectedEvent = selectedEvent;
    }

    public void removeEventListListener() {
        firebaseFirestoreService.removeEventListListener();
        filteredEventList.removeSource(initialEventList);
        filteredEventList.removeSource(searchRequest);
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
