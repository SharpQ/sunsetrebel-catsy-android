package com.sunsetrebel.catsy.repositories;

import android.util.Log;

import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.sunsetrebel.catsy.models.EventModel;
import com.sunsetrebel.catsy.utils.AccessType;
import com.sunsetrebel.catsy.utils.EventThemes;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FirebaseFirestoreService {
    private static FirebaseFirestoreService instance;
    private FirebaseFirestore fStore;
    private DocumentReference documentReference = null;
    private static MutableLiveData<List<EventModel>> eventListMutableLiveData;

    public FirebaseFirestoreService() {
        fStore = FirebaseFirestore.getInstance();
    }

    public static FirebaseFirestoreService getInstance() {
        if (instance == null) {
            instance = new FirebaseFirestoreService();
            eventListMutableLiveData = new MutableLiveData<>();
        }
        return instance;
    }

    private FirebaseFirestore getFirestoreClient() {
        if (fStore == null) {
            fStore = FirebaseFirestore.getInstance();
        }
        return fStore;
    }

    public interface GetUserCallback {
        void onResponse(Boolean value);
    }

    public interface GetUserNameCallback {
        void onResponse(String value);
    }

    public void createNewUser(String userID, String fullName, String email, String phone, String profileUrl){
        fStore = getFirestoreClient();
        documentReference = fStore.collection("userProfiles").document(userID);
        if (fullName == null) {
            fullName = "userName";
        }
        Map<String, Object> user = new HashMap<>();
        user.put("fullName", fullName);
        user.put("email", email);
        user.put("phone", phone);
        user.put("profileImg", profileUrl);
        documentReference.set(user).addOnSuccessListener(aVoid -> Log.d("INFO", "User profile created! UserID: " + userID));
    }

    public void createNewEvent(String hostId, String hostName, String hostProfileImg, String eventTitle, String eventLocation, LatLng eventGeoLocation,
                               Date eventStartTime, Date eventEndTime, AccessType eventAccessType, String eventDescr, Integer eventMinAge,
                               Integer eventMaxAge, Integer eventMaxPerson, String eventAvatar, List<Enum<?>> eventThemes){
        fStore = getFirestoreClient();
        String eventId = null;
        if (eventAccessType == AccessType.PUBLIC || eventAccessType == AccessType.SELECTIVE) {
            eventId = fStore.collection("eventList").document().getId();
            documentReference = fStore.collection("eventList").document(eventId);
        } else if (eventAccessType == AccessType.PRIVATE) {
            eventId = fStore.collection("userProfiles").document(hostId).collection("userEvents").document().getId();
            documentReference = fStore.collection("userProfiles").document(hostId).collection("userEvents").document(eventId);
        }
        String finalEventId = eventId;
        Map<String, Object> event = new HashMap<>();
        event.put("eventId", finalEventId);
        event.put("eventTitle", eventTitle);
        event.put("eventLocation", eventLocation);
        event.put("eventGeoLocation", eventGeoLocation);
        event.put("eventStartTime", eventStartTime);
        event.put("eventEndTime", eventEndTime);
        event.put("eventAccessType", eventAccessType);
        event.put("eventDescription", eventDescr);
        event.put("eventMinAge", eventMinAge);
        event.put("eventMaxAge", eventMaxAge);
        event.put("eventParticipants", 1);
        event.put("eventMaxPerson", eventMaxPerson);
        event.put("eventAvatar", eventAvatar);
        event.put("eventThemes", eventThemes);
        event.put("hostId", hostId);
        event.put("hostName", hostName);
        event.put("hostProfileImg", hostProfileImg);
        documentReference.set(event).addOnSuccessListener(aVoid -> Log.d("INFO", "New event created! EventId: " + finalEventId));
    }

    public void getUserInFirestore(GetUserCallback getUserCallback, String userId){
        fStore = getFirestoreClient();
        DocumentReference existingUser = fStore.collection("userProfiles").document(userId);
        existingUser.get(Source.SERVER).addOnCompleteListener(task -> {
            DocumentSnapshot document;
            document = task.getResult();
            getUserCallback.onResponse(document.exists());
        });
    }

    public void getUserNameInFirestore(GetUserNameCallback getUserNameCallback, String userId){
        fStore = getFirestoreClient();
        DocumentReference existingUser = fStore.collection("userProfiles").document(userId);
        existingUser.get(Source.SERVER).addOnCompleteListener(task -> {
            DocumentSnapshot document;
            document = task.getResult();
            getUserNameCallback.onResponse(document.get("fullName").toString());
        });
    }

    public void getUserProfileImgInFirestore(GetUserNameCallback getUserNameCallback, String userId){
        fStore = getFirestoreClient();
        DocumentReference existingUser = fStore.collection("userProfiles").document(userId);
        existingUser.get(Source.SERVER).addOnCompleteListener(task -> {
            DocumentSnapshot document;
            document = task.getResult();
            if (document.get("profileImg") != null) {
                getUserNameCallback.onResponse(document.get("profileImg").toString());
            } else {
                getUserNameCallback.onResponse(null);
            }
        });
    }

    public MutableLiveData<List<EventModel>> getEventListMutableLiveData() {
        fStore = getFirestoreClient();
        fStore.collection("eventList").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                List<EventModel> eventList = new ArrayList<>();
                for (QueryDocumentSnapshot document : value) {
                    if (document != null) {
                        EventModel event = convertDocumentToModel(document.getData());
                        eventList.add(event);
                    }
                }
                eventListMutableLiveData.postValue(eventList);
            }
        });
        return eventListMutableLiveData;
    }

    private EventModel convertDocumentToModel(Map<String, Object> map) {
        return new EventModel(map.get("hostId").toString(), map.get("hostName").toString(),
                convertObjectToString(map.get("hostProfileImg")), map.get("eventId").toString(),
                map.get("eventTitle").toString(), map.get("eventLocation").toString(),
                convertMapToLatLng((HashMap <String, Number>) map.get("eventGeoLocation")),
                ((Timestamp) map.get("eventStartTime")).toDate(),
                ((Timestamp) map.get("eventEndTime")).toDate(),
                AccessType.valueOf(map.get("eventAccessType").toString()),
                map.get("eventDescription").toString(),
                convertObjectToInteger(map.get("eventMinAge")), convertObjectToInteger(map.get("eventMaxAge")),
                ((Number) map.get("eventParticipants")).intValue(), convertObjectToInteger(map.get("eventMaxPerson")),
                convertObjectToString(map.get("eventAvatar")), convertEventThemes((ArrayList<Object[]>) map.get("eventThemes")));
    }

    private String convertObjectToString(Object object) {
        if (object != null) {
            return object.toString();
        } else {
            return null;
        }
    }

    private Integer convertObjectToInteger(Object object) {
        if (object != null) {
            return ((Number) object).intValue();
        } else {
            return null;
        }
    }

    private LatLng convertMapToLatLng(HashMap <String, Number> mapLatLng) {
        return new LatLng(mapLatLng.get("latitude").doubleValue(), mapLatLng.get("longitude").doubleValue());
    }

    private List<EventThemes> convertEventThemes(ArrayList<Object[]> eventObjects) {
        List<EventThemes> convertedEventThemeServices = new ArrayList<>();
        if (eventObjects != null) {
            for (Object object : eventObjects) {
                convertedEventThemeServices.add(EventThemes.valueOf(object.toString()));
            }
        }
        return convertedEventThemeServices;
    }
}
