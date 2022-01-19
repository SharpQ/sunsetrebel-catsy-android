package com.sunsetrebel.catsy.repositories;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.Timestamp;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.Source;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.models.EventModel;
import com.sunsetrebel.catsy.models.UserProfileModel;
import com.sunsetrebel.catsy.utils.AccessType;
import com.sunsetrebel.catsy.utils.CustomToastUtil;
import com.sunsetrebel.catsy.utils.EventThemes;

import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class FirebaseFirestoreService {
    private static FirebaseFirestoreService instance;
    private FirebaseFirestore fStore;
    private static MutableLiveData<List<EventModel>> eventListMutableLiveData = new MutableLiveData<>();
    private ListenerRegistration eventListListener = null;
    private static boolean instanceJoinLeave = false;
    private static boolean instanceLike = false;
    private static boolean instanceCreateEvent = false;


    public FirebaseFirestoreService() {
        fStore = FirebaseFirestore.getInstance();
    }

    public static FirebaseFirestoreService getInstance() {
        if (instance == null) {
            instance = new FirebaseFirestoreService();
        }
        return instance;
    }

    public interface GetUserCallback {
        void onResponse(Boolean value);
    }

    public interface GetUserProfileCallback {
        void onResponse(UserProfileModel userProfile);
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

    private DocumentReference getUserProfileDocRef(String userId) {
        return fStore.collection("userProfiles").document(userId);
    }

    private String getIdForPublicEventDocRef() {
        return fStore.collection("eventList").document().getId();
    }

    private String getIdForPrivateEventDocRef(String hostId) {
        return fStore.collection("userProfiles").document(hostId).collection("userEvents").document().getId();
    }

    private DocumentReference getPublicEventDocRef(String eventId) {
        return fStore.collection("eventList").document(eventId);
    }

    private DocumentReference getPublicEventJoinedUserDocRef(String eventId, String userId) {
        return fStore.collection("eventList").document(eventId).collection("usersJoined").document(userId);
    }

    private DocumentReference getPrivateEventDocRef(String hostId, String eventId) {
        return fStore.collection("userProfiles").document(hostId).collection("privateEvents").document(eventId);
    }

    private DocumentReference getPrivateEventJoinedUserDocRef(String hostId, String eventId, String userId) {
        return fStore.collection("userProfiles").document(hostId).collection("privateEvents").document(eventId).collection("usersJoined").document(userId);
    }

    private CollectionReference getPublicEventParticipants(String eventId) {
        return fStore.collection("eventList").document(eventId).collection("usersJoined");
    }

    private CollectionReference getPrivateEventParticipants(String hostId, String eventId) {
        return fStore.collection("userProfiles").document(hostId).collection("userEvents").document(eventId).collection("usersJoined");
    }

    private CollectionReference getPublicEventList() {
        return fStore.collection("eventList");
    }


    public void createNewUser(String userId, String fullName, String email, String phone, String profileUrl) {
        Map<String, Object> user = new HashMap<>();
        if (fullName == null) {
            fullName = "userName";
        }
        user.put("userId", userId);
        user.put("userFullName", fullName);
        user.put("userEmail", email);
        user.put("userPhone", phone);
        user.put("userProfileImg", profileUrl);
        user.put("joinedEvents", null);
        user.put("hostedPublicEvents", null);
        user.put("hostedPrivateEvents", null);
        getUserProfileDocRef(userId).set(user).addOnSuccessListener(aVoid -> Log.d("INFO", "User profile created! UserID: " + userId));
    }

    public void createNewEvent(Context context, String hostId, String hostName, String hostProfileImg, String eventTitle, String eventLocation, LatLng eventGeoLocation,
                               Date eventStartTime, Date eventEndTime, AccessType eventAccessType, String eventDescr, Integer eventMinAge,
                               Integer eventMaxAge, Integer eventMaxPerson, String eventAvatar, List<Enum<?>> eventThemes, Timestamp createTS, Timestamp updateTS) {
        if (instanceCreateEvent) {
            return;
        }
        instanceCreateEvent = true;
        String eventId = null;
        if (eventAccessType == AccessType.PUBLIC || eventAccessType == AccessType.SELECTIVE) {
            eventId = getIdForPublicEventDocRef();
        } else if (eventAccessType == AccessType.PRIVATE) {
            eventId = getIdForPrivateEventDocRef(hostId);
        }
        String finalEventId = eventId;
        Map<String, Object> event = new HashMap<>();
        event.put("eventId", eventId);
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

        Task<Void> task1 = null, task2 = null, task3 = null;
        if (eventAccessType == AccessType.PUBLIC || eventAccessType == AccessType.SELECTIVE) {
            task1 = getPublicEventDocRef(eventId).set(event);
            task2 = getPublicEventJoinedUserDocRef(eventId, hostId).set(firstUserMap);
            task3 = getUserProfileDocRef(hostId).update("hostedPublicEvents", FieldValue.arrayUnion(eventId));
        } else if (eventAccessType == AccessType.PRIVATE) {
            task1 = getPrivateEventDocRef(hostId, eventId).set(event);
            task2 = getPrivateEventJoinedUserDocRef(hostId, eventId, hostId).set(firstUserMap);
            task3 = getUserProfileDocRef(hostId).update("hostedPrivateEvents", FieldValue.arrayUnion(eventId));
        }
        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1, task2, task3);
        allTasks.addOnSuccessListener(querySnapshots -> {
            instanceCreateEvent = false;
            Log.d("INFO", "New event created! EventId: " + finalEventId);
            CustomToastUtil.showSuccessToast(context, context.getResources().getString(R.string.new_event_event_created_notification));
        }).addOnFailureListener(e -> {
            instanceCreateEvent = false;
            Log.d("INFO", "Failed to create new event!");
            CustomToastUtil.showFailToast(context, context.getResources().getString(R.string.new_event_event_failed_create_notification));
        });
    }

    public void setUserJoinEvent(SetUserInteractEventCallback setUserInteractEventCallback, Context context, EventModel event, String userId) {
        if (instanceJoinLeave) {
            return;
        }
        instanceJoinLeave = true;
        String eventTitle = event.getEventTitle();
        String eventId = event.getEventId();
        String hostId = event.getHostId();
        AccessType accessType = event.getAccessType();
        Map<String, Object> userMap = new HashMap<>();
        userMap.put("userId", userId);

        Task<Void> task1 = null, task2 = null;
        if (accessType == AccessType.PUBLIC || accessType == AccessType.SELECTIVE) {
            task1 = getPublicEventJoinedUserDocRef(eventId, userId).set(userMap);
            task2 = getPublicEventDocRef(eventId).update("eventParticipants", FieldValue.increment(1));
        } else if (accessType == AccessType.PRIVATE) {
            task1 = getPrivateEventJoinedUserDocRef(hostId, eventId, userId).set(userMap);
            task2 = getPrivateEventDocRef(hostId, eventId).update("eventParticipants", FieldValue.increment(1));
        }
        Task<Void> task3 = getUserProfileDocRef(userId).update("joinedEvents", FieldValue.arrayUnion(eventId));
        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1, task2, task3);
        allTasks.addOnSuccessListener(querySnapshots -> joinUserReturnSuccess(context, eventTitle, setUserInteractEventCallback))
                .addOnFailureListener(e -> joinUserReturnFail(context, eventTitle, setUserInteractEventCallback));
    }

    private void joinUserReturnFail(Context context, String eventTitle, SetUserInteractEventCallback setUserInteractEventCallback) {
        Log.d("INFO", "Failed to join event: " + eventTitle + "!");
        CustomToastUtil.showFailToast(context, context.getResources().getString(R.string.event_detailed_join_fail) + eventTitle + "!");
        instanceJoinLeave = false;
        setUserInteractEventCallback.onResponse(false);
    }

    private void joinUserReturnSuccess(Context context, String eventTitle, SetUserInteractEventCallback setUserInteractEventCallback) {
        Log.d("INFO", "You joined event: " + eventTitle + "!");
        CustomToastUtil.showSuccessToast(context, context.getResources().getString(R.string.event_detailed_join_success) + eventTitle + "!");
        instanceJoinLeave = false;
        setUserInteractEventCallback.onResponse(true);
    }

    public void setUserLeaveEvent(SetUserInteractEventCallback setUserInteractEventCallback, Context context, EventModel event, String userId) {
        if (instanceJoinLeave) {
            return;
        }
        instanceJoinLeave = true;
        String eventTitle = event.getEventTitle();
        String eventId = event.getEventId();
        String hostId = event.getHostId();
        AccessType accessType = event.getAccessType();

        Task<Void> task1 = null, task2 = null;
        if (accessType == AccessType.PUBLIC || accessType == AccessType.SELECTIVE) {
            task1 = getPublicEventJoinedUserDocRef(eventId, userId).delete();
            task2 = getPublicEventDocRef(eventId).update("eventParticipants", FieldValue.increment(-1));
        } else if (accessType == AccessType.PRIVATE) {
            task1 = getPrivateEventJoinedUserDocRef(hostId, eventId, userId).delete();
            task2 = getPrivateEventDocRef(hostId, eventId).update("eventParticipants", FieldValue.increment(-1));
        }
        Task<Void> task3 = getUserProfileDocRef(userId).update("joinedEvents", FieldValue.arrayRemove(eventId));
        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1, task2, task3);
        allTasks.addOnSuccessListener(querySnapshots -> leaveUserReturnSuccess(context, eventTitle, setUserInteractEventCallback))
                .addOnFailureListener(e -> leaveUserReturnFail(context, eventTitle, setUserInteractEventCallback));
    }

    private void leaveUserReturnFail(Context context, String eventTitle, SetUserInteractEventCallback setUserInteractEventCallback) {
        Log.d("INFO", "Failed to leave event: " + eventTitle + "!");
        CustomToastUtil.showFailToast(context, context.getResources().getString(R.string.event_detailed_leave_fail) + eventTitle + "!");
        instanceJoinLeave = false;
        setUserInteractEventCallback.onResponse(false);
    }

    private void leaveUserReturnSuccess(Context context, String eventTitle, SetUserInteractEventCallback setUserInteractEventCallback) {
        Log.d("INFO", "You left event: " + eventTitle + "!");
        CustomToastUtil.showSuccessToast(context, context.getResources().getString(R.string.event_detailed_leave_success) + eventTitle + "!");
        instanceJoinLeave = false;
        setUserInteractEventCallback.onResponse(true);
    }

    public void isUserRegistered(GetUserCallback getUserCallback, String userId){
        getUserProfileDocRef(userId).get(Source.SERVER).addOnCompleteListener(task -> {
            DocumentSnapshot document = task.getResult();
            getUserCallback.onResponse(document.exists());
        });
    }

    public void getUserProfile(GetUserProfileCallback getUserProfileCallback, String userId) {
        getUserProfileDocRef(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.getData() != null) {
                getUserProfileCallback.onResponse(convertUserProfileDocumentToModel(documentSnapshot.getData()));
            } else {
                getUserProfileCallback.onResponse(null);
            }
        }).addOnFailureListener(e -> getUserProfileCallback.onResponse(null));
    }

    public void getEventList(GetEventListCallback getEventListCallback) {
        getPublicEventList().get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<EventModel> eventList = new ArrayList<>();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                if (document != null) {
                    EventModel event = convertEventDocumentToModel(document.getData());
                    eventList.add(event);
                }
            }
            getEventListCallback.onResponse(eventList);
        }).addOnFailureListener(e -> getEventListCallback.onResponse(null));
    }

    public MutableLiveData<List<EventModel>> getEventListMutableLiveData() {
        eventListListener = getPublicEventList().addSnapshotListener((value, error) -> {
            List<EventModel> eventList = new ArrayList<>();
            for (QueryDocumentSnapshot document : value) {
                if (document != null) {
                    EventModel event = convertEventDocumentToModel(document.getData());
                    eventList.add(event);
                }
            }
            eventListMutableLiveData.postValue(eventList);
        });
        return eventListMutableLiveData;
    }

    public void removeEventListListener() {
        if (eventListListener != null) {
            eventListListener.remove();
        }
    }

    public void getEventParticipants(GetEventParticipantsCallback getEventParticipantsCallback, EventModel eventModel) {
        String hostId = eventModel.getHostId();
        String eventId = eventModel.getEventId();
        Task<QuerySnapshot> task = null;

        if (eventModel.getAccessType() == AccessType.PUBLIC || eventModel.getAccessType() == AccessType.SELECTIVE) {
            task = getPublicEventParticipants(eventId).get();
        } else {
            task = getPrivateEventParticipants(hostId, eventId).get();
        }

        task.addOnSuccessListener(queryDocumentSnapshots -> {
            List<String> usersList = new ArrayList<>();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                if (document != null) {
                    usersList.add(document.getId());
                }
            }
            getEventParticipantsCallback.onResponse(usersList);
        }).addOnFailureListener(e -> getEventParticipantsCallback.onResponse(null));
    }

    public void setEventAsLikedByUser(SetUserInteractEventCallback setUserInteractEventCallback, String userId, String eventId) {
        Task<Void> task1 = getUserProfileDocRef(userId).update("likedEvents", FieldValue.arrayUnion(eventId));
        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1);
        allTasks.addOnSuccessListener(querySnapshots -> setUserInteractEventCallback.onResponse(true))
                .addOnFailureListener(e -> setUserInteractEventCallback.onResponse(false));
    }

    private EventModel convertEventDocumentToModel(Map<String, Object> map) {
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
                ((Timestamp) map.get("createTS")), ((Timestamp) map.get("updateTS")));
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

    private UserProfileModel convertUserProfileDocumentToModel(Map<String, Object> map) {
        return new UserProfileModel(map.get("userId").toString(), convertObjectToString(map.get("userEmail")),
                convertObjectToString(map.get("userPhone")), map.get("userFullName").toString(),
                convertObjectToString(map.get("userProfileImg")),
                convertUserProfileEvents((ArrayList<Object[]>) map.get("joinedEvents")),
                convertUserProfileEvents((ArrayList<Object[]>) map.get("hostedPublicEvents")),
                convertUserProfileEvents((ArrayList<Object[]>) map.get("hostedPrivateEvents")),
                convertUserProfileEvents((ArrayList<Object[]>) map.get("likedEvents")));
    }

    private List<String> convertUserProfileEvents(ArrayList<Object[]> userEventsObjects) {
        List<String> userEvents = new ArrayList<>();
        if (userEventsObjects != null) {
            for (Object object : userEventsObjects) {
                userEvents.add(object.toString());
            }
        }
        return userEvents;
    }
}
