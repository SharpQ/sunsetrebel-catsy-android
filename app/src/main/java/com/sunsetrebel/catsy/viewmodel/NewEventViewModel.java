package com.sunsetrebel.catsy.viewmodel;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.widget.Toast;

import androidx.lifecycle.ViewModel;

import com.google.android.gms.maps.model.LatLng;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.models.EventModel;
import com.sunsetrebel.catsy.models.UserProfileModel;
import com.sunsetrebel.catsy.repositories.FirebaseAuthService;
import com.sunsetrebel.catsy.repositories.FirebaseFirestoreService;
import com.sunsetrebel.catsy.repositories.FirebaseStorageService;
import com.sunsetrebel.catsy.repositories.UserProfileService;
import com.sunsetrebel.catsy.utils.AccessType;
import com.sunsetrebel.catsy.utils.CustomToastUtil;
import com.sunsetrebel.catsy.utils.EventThemes;

import com.google.firebase.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class NewEventViewModel extends ViewModel {
    private EventModel eventModel;
    private com.google.firebase.auth.FirebaseAuth fAuth;
    private final FirebaseAuthService firebaseAuthService = FirebaseAuthService.getInstance();
    private final FirebaseFirestoreService firebaseFirestoreService = FirebaseFirestoreService.getInstance();
    private final FirebaseStorageService firebaseStorageService = FirebaseStorageService.getInstance();
    private final UserProfileService userProfileService = UserProfileService.getInstance();
    private UserProfileModel userProfileModel;


    public void init() {
        eventModel = new EventModel();
        fAuth = firebaseAuthService.getFirebaseClient();
        userProfileModel = userProfileService.getUserProfile();
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

    public void completeNewEventInfo(Context context, String eventDescrValue, Uri eventAvatarURI, Integer eventMinAgeValue, Integer eventMaxAgeValue, Integer eventMaxPeopleValue) {
        Date date = new Date();
        Timestamp createTS = new Timestamp(date);
        eventModel.setCreateTS(createTS);
        eventModel.setUpdateTS(createTS);
        eventModel.setEventDescr(eventDescrValue);
        eventModel.setEventMinAge(eventMinAgeValue);
        eventModel.setEventMaxAge(eventMaxAgeValue);
        eventModel.setEventMaxPerson(eventMaxPeopleValue);
        eventModel.setHostId(fAuth.getCurrentUser().getUid());
        eventModel.setHostName(userProfileModel.getUserFullName());
        eventModel.setHostProfileImg(userProfileModel.getUserProfileImg());
        if (eventAvatarURI != null) {
            firebaseStorageService.getAvatarStorageReference(downloadUrl -> {
                createEvent(context, downloadUrl);
            }, fAuth.getUid(), eventAvatarURI);
        } else {
            createEvent(context, null);
        }
    }

    private void createEvent(Context context, String eventAvatarDownloadURI) {
        if (validNewEventParams()) {
            firebaseFirestoreService.createNewEvent(context, eventModel.getHostId(), eventModel.getHostName(), eventModel.getHostProfileImg(),
                    eventModel.getEventTitle(), eventModel.getEventLocation(), eventModel.getEventGeoLocation(), eventModel.getEventStartTime(),
                    eventModel.getEventEndTime(), eventModel.getAccessType(), eventModel.getEventDescr(),
                    eventModel.getEventMinAge(), eventModel.getEventMaxAge(), eventModel.getEventMaxPerson(), eventAvatarDownloadURI, getConvertedEventThemes(),
                    eventModel.getCreateTS(), eventModel.getUpdateTS());
        } else {
            CustomToastUtil.showFailToast(context, context.getResources().getString(R.string.new_event_event_failed_create_notification));
        }
    }

    private boolean validNewEventParams() {
        return !TextUtils.isEmpty(eventModel.getHostId()) && !TextUtils.isEmpty(eventModel.getHostName())
                && !TextUtils.isEmpty(eventModel.getEventTitle()) && eventModel.getEventStartTime() != null
                && eventModel.getEventEndTime() != null && eventModel.getAccessType() != null
                && !TextUtils.isEmpty(eventModel.getEventDescr()) && !TextUtils.isEmpty(eventModel.getEventLocation())
                && eventModel.getEventGeoLocation() != null;
    }

    private List<Enum<?>> getConvertedEventThemes() {
        List<EventThemes> eventThemesBefore = eventModel.getEventThemes();
        List<Enum<?>> eventThemesAfter = new ArrayList<>();
        if (eventThemesBefore != null) {
            eventThemesAfter.addAll(eventThemesBefore);
        }
        return eventThemesAfter;
    }
}
