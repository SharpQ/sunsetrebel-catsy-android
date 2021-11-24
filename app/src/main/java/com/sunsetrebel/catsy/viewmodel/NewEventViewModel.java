package com.sunsetrebel.catsy.viewmodel;

import android.net.Uri;
import android.text.TextUtils;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.sunsetrebel.catsy.models.EventModel;
import com.sunsetrebel.catsy.repositories.FirebaseAuthService;
import com.sunsetrebel.catsy.repositories.FirebaseFirestoreService;
import com.sunsetrebel.catsy.repositories.FirebaseStorageService;
import com.sunsetrebel.catsy.utils.AccessType;
import com.sunsetrebel.catsy.utils.EventThemes;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewEventViewModel extends ViewModel {
    private EventModel eventModel;
    private com.google.firebase.auth.FirebaseAuth fAuth;
    private final FirebaseAuthService firebaseAuthService = FirebaseAuthService.getInstance();
    private final FirebaseFirestoreService firebaseFirestoreService = FirebaseFirestoreService.getInstance();
    private final FirebaseStorageService firebaseStorageService = FirebaseStorageService.getInstance();
    private String userFullName;
    private String userProfileImg;


    public void init() {
        eventModel = new EventModel();
        fAuth = firebaseAuthService.getFirebaseClient();
    }

    public void setNewEventPrimaryInfo(String eventTitle, AccessType accessType, Date eventStartTime,
                                     Date eventEndTime, List<EventThemes> eventThemes) {
        eventModel.setEventTitle(eventTitle);
        eventModel.setAccessType(accessType);
        eventModel.setEventStartTime(eventStartTime);
        eventModel.setEventEndTime(eventEndTime);
        eventModel.setEventThemes(eventThemes);
    }

    public void setNewEventLocation(String eventLocation, LatLng eventGeoLocation) {
        eventModel.setEventLocation(eventLocation);
        eventModel.setEventGeoLocation(eventGeoLocation);
    }

    public void completeNewEventInfo(String eventDescrValue, Uri eventAvatarURI, Integer eventMinAgeValue, Integer eventMaxAgeValue, Integer eventMaxPeopleValue) {
        eventModel.setEventDescr(eventDescrValue);
        eventModel.setEventMinAge(eventMinAgeValue);
        eventModel.setEventMaxAge(eventMaxAgeValue);
        eventModel.setEventMaxPerson(eventMaxPeopleValue);
        eventModel.setUserID(fAuth.getCurrentUser().getUid());
        createEvent(eventAvatarURI);
    }

    private void createEvent(Uri eventAvatarURI) {
        firebaseFirestoreService.getUserNameInFirestore(userName -> {
            eventModel.setUserName(userName);
            firebaseFirestoreService.getUserProfileImgInFirestore(userProfile -> {
                eventModel.setUserProfileImg(userProfile);
                if (eventAvatarURI != null) {
                    firebaseStorageService.getAvatarStorageReference(downloadUrl -> {
                        if (validNewEventParams()) {
                            firebaseFirestoreService.createNewEvent(eventModel.getUserID(), eventModel.getUserName(), eventModel.getUserProfileImg(),
                                    eventModel.getEventTitle(), eventModel.getEventLocation(), eventModel.getEventGeoLocation(), eventModel.getEventStartTime(),
                                    eventModel.getEventEndTime(), eventModel.getAccessType(), eventModel.getEventDescr(),
                                    eventModel.getEventMinAge(), eventModel.getEventMaxAge(), eventModel.getEventMaxPerson(), downloadUrl, convertEventThemes());
                        }
                    }, fAuth.getUid(), eventAvatarURI);
                } else {
                    if (validNewEventParams()) {
                        firebaseFirestoreService.createNewEvent(eventModel.getUserID(), eventModel.getUserName(), eventModel.getUserProfileImg(),
                                eventModel.getEventTitle(), eventModel.getEventLocation(), eventModel.getEventGeoLocation(), eventModel.getEventStartTime(),
                                eventModel.getEventEndTime(), eventModel.getAccessType(), eventModel.getEventDescr(),
                                eventModel.getEventMinAge(), eventModel.getEventMaxAge(), eventModel.getEventMaxPerson(), null, convertEventThemes());
                    }
                }
            }, fAuth.getUid());
        }, fAuth.getUid());
    }

    private boolean validNewEventParams() {
        return !TextUtils.isEmpty(eventModel.getUserID()) && !TextUtils.isEmpty(eventModel.getUserName())
                && !TextUtils.isEmpty(eventModel.getEventTitle()) && eventModel.getEventStartTime() != null
                && eventModel.getEventEndTime() != null && eventModel.getAccessType() != null
                && !TextUtils.isEmpty(eventModel.getEventDescr()) && !TextUtils.isEmpty(eventModel.getEventLocation())
                && eventModel.getEventGeoLocation() != null;
    }

    private List<Enum<?>> convertEventThemes() {
        List<EventThemes> eventThemesBefore = eventModel.getEventThemes();
        List<Enum<?>> eventThemesAfter = new ArrayList<>();
        if (eventThemesBefore != null) {
            eventThemesAfter.addAll(eventThemesBefore);
        }
        return eventThemesAfter;
    }
}
