package com.sunsetrebel.catsy.utils;

import android.util.Log;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.Source;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import com.sunsetrebel.catsy.utils.EventListService;

//    public enum AuthTypes {
//        PHONE,
//        EMAIL,
//        GOOGLE_AUTH,
//        FACEBOOK_AUTH
//    }

public class FirebaseFirestoreService {
    private FirebaseFirestore fStore = null;
    private DocumentReference documentReference = null;

    public interface GetUserCallback {
        void onResponse(Boolean value);
    }

    public interface GetUserNameCallback {
        void onResponse(String value);
    }

    public interface GetEventsCallback {
        void onResponse(List<Map<String, Object>> events);
    }

    private FirebaseFirestore getInstance() {
        return fStore = FirebaseFirestore.getInstance();
    }

    public void createNewUserByEmail(String userID, String fullName, String email){
        fStore = getInstance();
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
        fStore = getInstance();
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
        fStore = getInstance();
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
        fStore = getInstance();
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

    public void createNewEvent(String userID, String eventName, String eventLocation, String eventDate,
                               String eventType, String eventDescr, String userName){
        fStore = getInstance();
        String eventId = fStore.collection("eventList").document().getId();
        documentReference = fStore.collection("eventList").document(eventId);
        Map<String, Object> event = new HashMap<>();
        event.put("eventId", eventId);
        event.put("eventName", eventName);
        event.put("eventLocation", eventLocation);
        event.put("eventDate", eventDate);
        event.put("eventType", eventType);
        event.put("eventDescr", eventDescr);
        event.put("userId", userID);
        event.put("userName", userName);
        documentReference.set(event).addOnSuccessListener(aVoid -> Log.d("INFO", "New event created! EventId: " + eventId));
    }

    public void getUserInFirestore(GetUserCallback getUserCallback, String userId){
        fStore = getInstance();
        DocumentReference existingUser = fStore.collection("userProfiles").document(userId);
        existingUser.get(Source.SERVER).addOnCompleteListener(task -> {
            DocumentSnapshot document;
            document = task.getResult();
            getUserCallback.onResponse(document.exists());
        });
    }

    public void getUserNameInFirestore(GetUserNameCallback getUserNameCallback, String userId){
        fStore = getInstance();
        DocumentReference existingUser = fStore.collection("userProfiles").document(userId);
        existingUser.get(Source.SERVER).addOnCompleteListener(task -> {
            DocumentSnapshot document;
            document = task.getResult();
            getUserNameCallback.onResponse(document.get("fullName").toString());
        });
    }

    public void getEventList(GetEventsCallback getEventsCallback){
        fStore = getInstance();
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
