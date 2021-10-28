package com.sunsetrebel.catsy.repositories;

import android.util.Log;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Source;
import com.sunsetrebel.catsy.utils.AccessType;
import com.sunsetrebel.catsy.utils.EventListService;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FirebaseFirestoreService {
    private static FirebaseFirestoreService instance;
    private FirebaseFirestore fStore;
    private DocumentReference documentReference = null;

    public FirebaseFirestoreService() {
        fStore = FirebaseFirestore.getInstance();
    }

    public static FirebaseFirestoreService getInstance() {
        if (instance == null) {
            instance = new FirebaseFirestoreService();
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

    public interface GetEventsCallback {
        void onResponse(List<Map<String, Object>> events);
    }

    public void createNewUserByEmail(String userID, String fullName, String email){
        fStore = getFirestoreClient();
        documentReference = fStore.collection("userProfiles").document(userID);
        if (fullName == null) {
            fullName = "userName";
        }
        Map<String, Object> user = new HashMap<>();
        user.put("fullName", fullName);
        user.put("email", email);
        user.put("phone", null);
        user.put("profileImg", null);
        documentReference.set(user).addOnSuccessListener(aVoid -> Log.d("INFO", "User profile created! UserID: " + userID));
    }

    public void createNewUserByPhone(String userID, String fullName, String phone){
        fStore = getFirestoreClient();
        documentReference = fStore.collection("userProfiles").document(userID);
        if (fullName == null) {
            fullName = "userName";
        }
        Map<String, Object> user = new HashMap<>();
        user.put("fullName", fullName);
        user.put("email", null);
        user.put("phone", phone);
        user.put("profileImg", null);
        documentReference.set(user).addOnSuccessListener(aVoid -> Log.d("INFO", "User profile created! UserID: " + userID));
    }

    public void createNewUserByFacebook(String userID, String fullName, String email, String phone, String profileUrl){
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

    public void createNewUserByGoogle(String userID, String fullName, String email, String phone, String profileUrl){
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

    public void createNewPublicEvent(String userID, String eventTitle, String eventLocation, LatLng eventGeoLocation, Date eventStartTime,
                                     Date eventEndTime, AccessType accessType, String eventDescr, String eventMinAge, String eventMaxAge,
                                     String eventAttendees, String eventAvatar, List<Enum<?>> eventThemes, String userName){
        fStore = getFirestoreClient();
        String eventId = fStore.collection("eventList").document().getId();
        documentReference = fStore.collection("eventList").document(eventId);
        Map<String, Object> event = new HashMap<>();
        event.put("eventId", eventId);
        event.put("eventTitle", eventTitle);
        event.put("eventLocation", eventLocation);
        event.put("eventGeoLocation", eventGeoLocation);
        event.put("eventStartTime", eventStartTime);
        event.put("eventEndTime", eventEndTime);
        event.put("accessType", accessType);
        event.put("eventDescription", eventDescr);
        event.put("eventMinAge", eventMinAge);
        event.put("eventMaxAge", eventMaxAge);
        event.put("eventAttendees", eventAttendees);
        event.put("eventAvatar", eventAvatar);
        event.put("eventThemes", eventThemes);
        event.put("userId", userID);
        event.put("userName", userName);
        documentReference.set(event).addOnSuccessListener(aVoid -> Log.d("INFO", "New public event created! EventId: " + eventId));
    }

    public void createNewPrivateEvent(String userID, String eventTitle, String eventLocation, LatLng eventGeoLocation, Date eventStartTime,
                                      Date eventEndTime, AccessType accessType, String eventDescr, String eventMinAge, String eventMaxAge,
                                      String eventAttendees, String eventAvatar, List<Enum<?>> eventThemes, String userName){
        fStore = getFirestoreClient();
        String eventId = fStore.collection("userProfiles").document(userID).collection("userEvents").document().getId();
        documentReference = fStore.collection("userProfiles").document(userID).collection("userEvents").document(eventId);
        Map<String, Object> event = new HashMap<>();
        event.put("eventId", eventId);
        event.put("eventTitle", eventTitle);
        event.put("eventLocation", eventLocation);
        event.put("eventGeoLocation", eventGeoLocation);
        event.put("eventStartTime", eventStartTime);
        event.put("eventEndTime", eventEndTime);
        event.put("accessType", accessType);
        event.put("eventDescription", eventDescr);
        event.put("eventMinAge", eventMinAge);
        event.put("eventMaxAge", eventMaxAge);
        event.put("eventAttendees", eventAttendees);
        event.put("eventAvatar", eventAvatar);
        event.put("eventThemes", eventThemes);
        event.put("userId", userID);
        event.put("userName", userName);
        documentReference.set(event).addOnSuccessListener(aVoid -> Log.d("INFO", "New private event created! EventId: " + eventId));
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

    public void getEventList(GetEventsCallback getEventsCallback){
        fStore = getFirestoreClient();
        CollectionReference eventList = fStore.collection("eventList");
        eventList.get().addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                List<Map<String, Object>> events = new ArrayList<>();
                for (QueryDocumentSnapshot document : task.getResult()) {
                    Map<String, Object> map = document.getData();
                    events.add(map);
                }
                EventListService.setListToNotUpdate();
                getEventsCallback.onResponse(events);
            } else {
                Log.d("INFO", String.valueOf(task.getException()));
            }
        });
    }
}
