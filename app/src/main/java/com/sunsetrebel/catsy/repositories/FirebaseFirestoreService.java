package com.sunsetrebel.catsy.repositories;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.sunsetrebel.catsy.R;
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
    private static MutableLiveData<List<EventModel>> eventListMutableLiveData;
    private ListenerRegistration eventListListener = null;

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

    public interface GetEventListCallback {
        void onResponse(List<EventModel> eventList);
    }

    public interface GetEventParticipantsCallback {
        void onResponse(List<String> value);
    }

    public interface SetUserInteractEventCallback {
        void onResponse(Boolean isResponseSuccessful);
    }

    public void createNewUser(String userID, String fullName, String email, String phone, String profileUrl){
        fStore = getFirestoreClient();
        DocumentReference documentReference = fStore.collection("userProfiles").document(userID);
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

    public void createNewEvent(Context context, String hostId, String hostName, String hostProfileImg, String eventTitle, String eventLocation, LatLng eventGeoLocation,
                               Date eventStartTime, Date eventEndTime, AccessType eventAccessType, String eventDescr, Integer eventMinAge,
                               Integer eventMaxAge, Integer eventMaxPerson, String eventAvatar, List<Enum<?>> eventThemes, Timestamp createTS, Timestamp updateTS){
        fStore = getFirestoreClient();
        String eventId = null;
        DocumentReference eventDocumentReference = null, eventPersonalDocumentReference = null, firstUserDocumentReference = null;
        if (eventAccessType == AccessType.PUBLIC || eventAccessType == AccessType.SELECTIVE) {
            eventId = fStore.collection("eventList").document().getId();
            eventDocumentReference = fStore.collection("eventList").document(eventId);
            eventPersonalDocumentReference = fStore.collection("userProfiles").document(hostId).collection("userEvents").document(eventId);
            firstUserDocumentReference = fStore.collection("eventList").document(eventId).collection("usersJoined").document(hostId);
        } else if (eventAccessType == AccessType.PRIVATE) {
            eventId = fStore.collection("userProfiles").document(hostId).collection("userEvents").document().getId();
            eventDocumentReference = fStore.collection("userProfiles").document(hostId).collection("userEvents").document(eventId);
            firstUserDocumentReference = fStore.collection("userProfiles").document(hostId).collection("userEvents").document(eventId).collection("usersJoined").document(hostId);
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
        event.put("eventMaxPerson", eventMaxPerson);
        event.put("eventAvatar", eventAvatar);
        event.put("eventThemes", eventThemes);
        event.put("eventParticipants", 1);
        event.put("hostId", hostId);
        event.put("hostName", hostName);
        event.put("hostProfileImg", hostProfileImg);
        event.put("createTS", createTS);
        event.put("updateTS", updateTS);
        Map<String, Object> firstUserMap = new HashMap<>();
        firstUserMap.put("userId", hostId);
        DocumentReference finalEventPersonalDocumentReference = eventPersonalDocumentReference;
        DocumentReference finalFirstUserDocumentReference = firstUserDocumentReference;
        eventDocumentReference.set(event).addOnSuccessListener(aVoid -> {
            finalFirstUserDocumentReference.set(firstUserMap);
            if (eventAccessType == AccessType.PUBLIC || eventAccessType == AccessType.SELECTIVE) {
                finalEventPersonalDocumentReference.set(event);
            }
            Log.d("INFO", "New event created! EventId: " + finalEventId);
            Toast.makeText(context, context.getResources().getString(R.string.new_event_event_created_notification), Toast.LENGTH_SHORT).show();
        }).addOnFailureListener(e -> {
            Log.d("INFO", "Failed to create new event!");
            Toast.makeText(context, context.getResources().getString(R.string.new_event_event_failed_create_notification), Toast.LENGTH_SHORT).show();
        });
    }

    public void setUserJoinEvent(SetUserInteractEventCallback setUserInteractEventCallback, Context context, EventModel event, String userId) {
        fStore = getFirestoreClient();
        String eventTitle = event.getEventTitle();
        String eventId = event.getEventId();
        String hostId = event.getHostId();
        AccessType accessType = event.getAccessType();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("userId", userId);
        if (accessType == AccessType.PUBLIC || accessType == AccessType.SELECTIVE) {
            fStore.collection("eventList").document(eventId).collection("usersJoined").document(userId).set(userMap).addOnSuccessListener(aVoid -> {
                fStore.collection("userProfiles").document(userId).update("joinedEvents",
                        FieldValue.arrayUnion(eventId)).addOnSuccessListener(aVoid1 -> {
                            fStore.collection("eventList").document(eventId).update("eventParticipants", FieldValue.increment(1)).addOnSuccessListener(aVoid2 -> {
                                joinUserReturnSuccess(context, eventTitle, setUserInteractEventCallback);
                            }).addOnFailureListener(e -> joinUserReturnFail(context, eventTitle, setUserInteractEventCallback));
                        }).addOnFailureListener(e -> joinUserReturnFail(context, eventTitle, setUserInteractEventCallback));
            }).addOnFailureListener(e -> {
                joinUserReturnFail(context, eventTitle, setUserInteractEventCallback);
            });
        } else if (accessType == AccessType.PRIVATE) {
            fStore.collection("userProfiles").document(hostId).collection("userEvents").document(eventId).collection("usersJoined").document(hostId).set(userMap)
                    .addOnSuccessListener(aVoid -> {
                        fStore.collection("userProfiles").document(userId).update("joinedEvents",
                        FieldValue.arrayUnion(eventId)).addOnSuccessListener(aVoid1 -> {
                            fStore.collection("userProfiles").document(hostId).collection("userEvents").document(eventId)
                                    .update("eventParticipants", FieldValue.increment(1)).addOnSuccessListener(aVoid2 -> {
                                joinUserReturnSuccess(context, eventTitle, setUserInteractEventCallback);
                            }).addOnFailureListener(e -> joinUserReturnFail(context, eventTitle, setUserInteractEventCallback));
                        }).addOnFailureListener(e -> joinUserReturnFail(context, eventTitle, setUserInteractEventCallback));
                joinUserReturnSuccess(context, eventTitle, setUserInteractEventCallback);
            }).addOnFailureListener(e -> {
                joinUserReturnFail(context, eventTitle, setUserInteractEventCallback);
            });
        }
    }

    private void joinUserReturnFail(Context context, String eventTitle, SetUserInteractEventCallback setUserInteractEventCallback) {
        Log.d("INFO", "Failed to join event: " + eventTitle + "!");
        Toast.makeText(context, context.getResources().getString(R.string.event_detailed_join_fail) + eventTitle + "!", Toast.LENGTH_SHORT).show();
        setUserInteractEventCallback.onResponse(false);
    }

    private void joinUserReturnSuccess(Context context, String eventTitle, SetUserInteractEventCallback setUserInteractEventCallback) {
        Log.d("INFO", "You joined event: " + eventTitle + "!");
        Toast.makeText(context, context.getResources().getString(R.string.event_detailed_join_success) + eventTitle + "!", Toast.LENGTH_SHORT).show();
        setUserInteractEventCallback.onResponse(true);
    }

    public void setUserLeaveEvent(SetUserInteractEventCallback setUserInteractEventCallback, Context context, EventModel event, String userId) {
        fStore = getFirestoreClient();
        String eventTitle = event.getEventTitle();
        String eventId = event.getEventId();
        String hostId = event.getHostId();
        AccessType accessType = event.getAccessType();
        if (accessType == AccessType.PUBLIC || accessType == AccessType.SELECTIVE) {
            fStore.collection("eventList").document(eventId).collection("usersJoined").document(userId).delete().addOnSuccessListener(aVoid -> {
                fStore.collection("userProfiles").document(userId).update("joinedEvents",
                        FieldValue.arrayRemove(eventId)).addOnSuccessListener(aVoid1 -> {
                    fStore.collection("eventList").document(eventId).update("eventParticipants", FieldValue.increment(-1))
                            .addOnSuccessListener(aVoid2 -> {
                                leaveUserReturnSuccess(context, eventTitle, setUserInteractEventCallback);
                            }).addOnFailureListener(e -> leaveUserReturnFail(context, eventTitle, setUserInteractEventCallback));
                        }).addOnFailureListener(e -> leaveUserReturnFail(context, eventTitle, setUserInteractEventCallback));
            }).addOnFailureListener(e -> {
                leaveUserReturnFail(context, eventTitle, setUserInteractEventCallback);
            });
        } else if (accessType == AccessType.PRIVATE) {
            fStore.collection("userProfiles").document(hostId).collection("userEvents").document(eventId).collection("usersJoined").document(hostId).delete().addOnSuccessListener(aVoid -> {
                fStore.collection("userProfiles").document(userId).update("joinedEvents",
                        FieldValue.arrayRemove(eventId)).addOnSuccessListener(aVoid1 -> {
                    fStore.collection("userProfiles").document(hostId).collection("userEvents").document(eventId)
                            .update("eventParticipants", FieldValue.increment(-1)).addOnSuccessListener(aVoid2 -> {
                                leaveUserReturnSuccess(context, eventTitle, setUserInteractEventCallback);
                            }).addOnFailureListener(e -> leaveUserReturnFail(context, eventTitle, setUserInteractEventCallback));
                        }).addOnFailureListener(e -> leaveUserReturnFail(context, eventTitle, setUserInteractEventCallback));
            }).addOnFailureListener(e -> {
                leaveUserReturnFail(context, eventTitle, setUserInteractEventCallback);
            });
        }
    }

    private void leaveUserReturnFail(Context context, String eventTitle, SetUserInteractEventCallback setUserInteractEventCallback) {
        Log.d("INFO", "Failed to leave event: " + eventTitle + "!");
        Toast.makeText(context, context.getResources().getString(R.string.event_detailed_leave_fail) + eventTitle + "!", Toast.LENGTH_SHORT).show();
        setUserInteractEventCallback.onResponse(false);
    }

    private void leaveUserReturnSuccess(Context context, String eventTitle, SetUserInteractEventCallback setUserInteractEventCallback) {
        Log.d("INFO", "You left event: " + eventTitle + "!");
        Toast.makeText(context, context.getResources().getString(R.string.event_detailed_leave_success) + eventTitle + "!", Toast.LENGTH_SHORT).show();
        setUserInteractEventCallback.onResponse(true);
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
        existingUser.get(Source.SERVER).addOnSuccessListener(documentSnapshot -> {
            getUserNameCallback.onResponse(documentSnapshot.get("fullName").toString());
        });
        existingUser.get(Source.SERVER).addOnFailureListener(e -> getUserNameCallback.onResponse(null));
    }

    public void getUserProfileImgInFirestore(GetUserNameCallback getUserNameCallback, String userId){
        fStore = getFirestoreClient();
        DocumentReference existingUser = fStore.collection("userProfiles").document(userId);
        existingUser.get(Source.SERVER).addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.get("profileImg") != null) {
                getUserNameCallback.onResponse(documentSnapshot.get("profileImg").toString());
            } else {
                getUserNameCallback.onResponse(null);
            }
        });
        existingUser.get(Source.SERVER).addOnFailureListener(e -> getUserNameCallback.onResponse(null));
    }

    public MutableLiveData<List<EventModel>> getEventListMutableLiveData() {
        fStore = getFirestoreClient();
        eventListListener = fStore.collection("eventList").addSnapshotListener(new EventListener<QuerySnapshot>() {
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

    public void removeEventListListener() {
        if (eventListListener != null) {
            eventListListener.remove();
        }
    }

    public void getEventParticipants(GetEventParticipantsCallback getEventParticipantsCallback, EventModel eventModel) {
        fStore = getFirestoreClient();
        String hostId = eventModel.getHostId();
        String eventId = eventModel.getEventId();
        if (eventModel.getAccessType() == AccessType.PUBLIC || eventModel.getAccessType() == AccessType.SELECTIVE) {
            fStore.collection("eventList").document(eventId).collection("usersJoined").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<String> usersList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        if (document != null) {
                            usersList.add(document.getId());
                        }
                    }
                    getEventParticipantsCallback.onResponse(usersList);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    getEventParticipantsCallback.onResponse(null);
                }
            });
        } else {
            fStore.collection("userProfiles").document(hostId).collection("userEvents").document(eventId).collection("usersJoined").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
                @Override
                public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                    List<String> usersList = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        if (document != null) {
                            usersList.add(document.getId());
                        }
                    }
                    getEventParticipantsCallback.onResponse(usersList);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    getEventParticipantsCallback.onResponse(null);
                }
            });
        }
    }

    public void getEventList(GetEventListCallback getEventListCallback) {
        fStore = getFirestoreClient();
        fStore.collection("eventList").get().addOnSuccessListener(new OnSuccessListener<QuerySnapshot>() {
            @Override
            public void onSuccess(QuerySnapshot queryDocumentSnapshots) {
                List<EventModel> eventList = new ArrayList<>();
                for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                    if (document != null) {
                        EventModel event = convertDocumentToModel(document.getData());
                        eventList.add(event);
                    }
                }
                getEventListCallback.onResponse(eventList);
            }
        });

        fStore.collection("eventList").get(Source.SERVER).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                getEventListCallback.onResponse(null);
            }
        });
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
                convertObjectToInteger(map.get("eventParticipants")), null, convertObjectToInteger(map.get("eventMaxPerson")),
                convertObjectToString(map.get("eventAvatar")), convertEventThemes((ArrayList<Object[]>) map.get("eventThemes")),
                ((Timestamp) map.get("createTS")).toDate(), ((Timestamp) map.get("updateTS")).toDate());
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
