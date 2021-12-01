package com.sunsetrebel.catsy.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.sunsetrebel.catsy.models.EventModel;
import com.sunsetrebel.catsy.repositories.FirebaseFirestoreService;

import java.util.List;

public class EventListViewModel extends ViewModel {
    private LiveData<List<EventModel>> eventList;
    private FirebaseFirestoreService firebaseFirestoreService;
    private EventModel selectedEvent;

    public void init() {
        firebaseFirestoreService = FirebaseFirestoreService.getInstance();
        eventList = firebaseFirestoreService.getEventListMutableLiveData();
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
}
