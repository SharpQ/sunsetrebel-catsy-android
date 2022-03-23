package com.sunsetrebel.catsy.repositories;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

import com.google.android.gms.maps.model.LatLng;
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
import com.sunsetrebel.catsy.models.CommonUserModel;
import com.sunsetrebel.catsy.models.EventModel;
import com.sunsetrebel.catsy.models.MainUserProfileModel;
import com.sunsetrebel.catsy.enums.AccessType;
import com.sunsetrebel.catsy.utils.CustomToastUtil;
import com.sunsetrebel.catsy.enums.EventThemes;

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
    //COLLECTIONS NAMES
    public static final String COLLECTION_USER_PROFILES = "userProfiles";
    public static final String COLLECTION_PUBLIC_EVENTS = "publicEvents";
    public static final String COLLECTION_EVENT_USERS_JOINED = "usersJoined";
    public static final String COLLECTION_PRIVATE_EVENTS = "privateEvents";
    //DOCUMENTS PROPERTIES NAMES
    public static final String DOCUMENT_USER_ID = "userId";
    public static final String DOCUMENT_USER_FULL_NAME = "userFullName";
    public static final String DOCUMENT_USER_EMAIL = "userEmail";
    public static final String DOCUMENT_USER_PHONE = "userPhone";
    public static final String DOCUMENT_USER_PROFILE_IMG = "userProfileImg";
    public static final String DOCUMENT_USER_JOINED_EVENTS = "joinedEvents";
    public static final String DOCUMENT_USER_LIKED_EVENTS = "likedEvents";
    public static final String DOCUMENT_USER_HOSTED_PUBLIC_EVENTS = "hostedPublicEvents";
    public static final String DOCUMENT_USER_HOSTED_PRIVATE_EVENTS = "hostedPrivateEvents";
    public static final String DOCUMENT_USER_LINK_TELEGRAM = "userLinkTelegram";
    public static final String DOCUMENT_USER_LINK_TIKTOK = "userLinkTikTok";
    public static final String DOCUMENT_USER_LINK_INSTAGRAM = "userLinkInstagram";
    public static final String DOCUMENT_USER_LINK_FACEBOOK = "userLinkFacebook";
    public static final String DOCUMENT_EVENT_ID = "eventId";
    public static final String DOCUMENT_EVENT_TITLE = "eventTitle";
    public static final String DOCUMENT_EVENT_LOCATION = "eventLocation";
    public static final String DOCUMENT_EVENT_GEOLOCATION = "eventGeoLocation";
    public static final String DOCUMENT_EVENT_START_TIME = "eventStartTime";
    public static final String DOCUMENT_EVENT_END_TIME = "eventEndTime";
    public static final String DOCUMENT_EVENT_ACCESS_TYPE = "eventAccessType";
    public static final String DOCUMENT_EVENT_DESCRIPTION = "eventDescription";
    public static final String DOCUMENT_EVENT_MIN_AGE = "eventMinAge";
    public static final String DOCUMENT_EVENT_MAX_AGE = "eventMaxAge";
    public static final String DOCUMENT_EVENT_MAX_PERSON = "eventMaxPerson";
    public static final String DOCUMENT_EVENT_AVATAR = "eventAvatar";
    public static final String DOCUMENT_EVENT_THEMES = "eventThemes";
    public static final String DOCUMENT_EVENT_PARTICIPANTS = "eventParticipants";
    public static final String DOCUMENT_EVENT_HOST_ID = "hostId";
    public static final String DOCUMENT_EVENT_HOST_NAME = "hostName";
    public static final String DOCUMENT_EVENT_HOST_PROFILE_IMG = "hostProfileImg";
    public static final String DOCUMENT_EVENT_CREATE_TS = "createTS";
    public static final String DOCUMENT_EVENT_UPDATE_TS = "updateTS";

    //INSTANCE
    public FirebaseFirestoreService() {
        fStore = FirebaseFirestore.getInstance();
    }

    public static FirebaseFirestoreService getInstance() {
        if (instance == null) {
            instance = new FirebaseFirestoreService();
        }
        return instance;
    }

    //INTERFACE
    public interface GetUserCallback {
        void onResponse(Boolean value);
    }

    public interface GetUserProfileCallback {
        void onResponse(MainUserProfileModel userProfile);
    }

    public interface GetEventListCallback {
        void onResponse(List<EventModel> eventList);
    }

    public interface GetEventParticipantsCallback {
        void onResponse(List<CommonUserModel> value);
    }

    public interface SetUserInteractEventCallback {
        void onResponse(Boolean isResponseSuccessful);
    }

    //COLLECTION PATH
    private CollectionReference getPublicEventsCollection() {
        return fStore.collection(COLLECTION_PUBLIC_EVENTS);
    }

    private DocumentReference getUserProfileDocument(String userId) {
        return fStore.collection(COLLECTION_USER_PROFILES).document(userId);
    }

    private DocumentReference getPublicEventDocument(String eventId) {
        return getPublicEventsCollection().document(eventId);
    }

    private CollectionReference getPublicEventParticipantsCollection(String eventId) {
        return getPublicEventDocument(eventId).collection(COLLECTION_EVENT_USERS_JOINED);
    }

    private DocumentReference getPublicEventJoinedUserDocument(String eventId, String userId) {
        return getPublicEventParticipantsCollection(eventId).document(userId);
    }

    private DocumentReference getPrivateEventDocument(String eventId) {
        return fStore.collection(COLLECTION_PRIVATE_EVENTS).document(eventId);
    }

    private CollectionReference getPrivateEventParticipantsCollection(String eventId) {
        return getPrivateEventDocument(eventId).collection(COLLECTION_EVENT_USERS_JOINED);
    }

    private DocumentReference getPrivateEventJoinedUserDocument(String eventId, String userId) {
        return getPrivateEventParticipantsCollection(eventId).document(userId);
    }

    private String getIdForPublicEventDocument() {
        return getPublicEventsCollection().document().getId();
    }

    private String getIdForPrivateEventDocument() {
        return fStore.collection(COLLECTION_PRIVATE_EVENTS).document().getId();
    }

    public void createNewUser(String userId, String fullName, String email, String phone, String profileUrl) {
        Map<String, Object> user = new HashMap<>();
        if (fullName == null) {
            fullName = "userName";
        }
        user.put(DOCUMENT_USER_ID, userId);
        user.put(DOCUMENT_USER_FULL_NAME, fullName);
        user.put(DOCUMENT_USER_EMAIL, email);
        user.put(DOCUMENT_USER_PHONE, phone);
        user.put(DOCUMENT_USER_PROFILE_IMG, profileUrl);
        user.put(DOCUMENT_USER_JOINED_EVENTS, null);
        user.put(DOCUMENT_USER_LIKED_EVENTS, null);
        user.put(DOCUMENT_USER_HOSTED_PUBLIC_EVENTS, null);
        user.put(DOCUMENT_USER_HOSTED_PRIVATE_EVENTS, null);
        user.put(DOCUMENT_USER_LINK_TELEGRAM, null);
        user.put(DOCUMENT_USER_LINK_TIKTOK, null);
        user.put(DOCUMENT_USER_LINK_INSTAGRAM, null);
        user.put(DOCUMENT_USER_LINK_FACEBOOK, null);
        getUserProfileDocument(userId).set(user).addOnSuccessListener(aVoid -> Log.d("DEBUG", "User profile created! UserID: " + userId));
    }

    public void createNewEvent(Context context, EventModel eventModel, MainUserProfileModel mainUserProfileModel) {
        if (instanceCreateEvent) {
            return;
        }
        instanceCreateEvent = true;
        String eventId = null;
        AccessType eventAccessType = eventModel.getAccessType();
        if (eventAccessType == AccessType.PUBLIC || eventAccessType == AccessType.SELECTIVE) {
            eventId = getIdForPublicEventDocument();
        } else if (eventAccessType == AccessType.PRIVATE) {
            eventId = getIdForPrivateEventDocument();
        }
        String finalEventId = eventId;
        Map<String, Object> event = new HashMap<>();
        event.put(DOCUMENT_EVENT_ID, eventId);
        event.put(DOCUMENT_EVENT_TITLE, eventModel.getEventTitle());
        event.put(DOCUMENT_EVENT_LOCATION, eventModel.getEventLocation());
        event.put(DOCUMENT_EVENT_GEOLOCATION, eventModel.getEventGeoLocation());
        event.put(DOCUMENT_EVENT_START_TIME, eventModel.getEventStartTime());
        event.put(DOCUMENT_EVENT_END_TIME, eventModel.getEventEndTime());
        event.put(DOCUMENT_EVENT_ACCESS_TYPE, eventAccessType);
        event.put(DOCUMENT_EVENT_DESCRIPTION, eventModel.getEventDescr());
        event.put(DOCUMENT_EVENT_MIN_AGE, eventModel.getEventMinAge());
        event.put(DOCUMENT_EVENT_MAX_AGE, eventModel.getEventMaxAge());
        event.put(DOCUMENT_EVENT_MAX_PERSON, eventModel.getEventMaxPerson());
        event.put(DOCUMENT_EVENT_AVATAR, eventModel.getEventAvatar());
        event.put(DOCUMENT_EVENT_THEMES, eventModel.getEventThemes());
        event.put(DOCUMENT_EVENT_PARTICIPANTS, 1);
        event.put(DOCUMENT_EVENT_HOST_ID, eventModel.getHostId());
        event.put(DOCUMENT_EVENT_HOST_NAME, eventModel.getHostName());
        event.put(DOCUMENT_EVENT_HOST_PROFILE_IMG, eventModel.getHostProfileImg());
        event.put(DOCUMENT_EVENT_CREATE_TS, eventModel.getCreateTS());
        event.put(DOCUMENT_EVENT_UPDATE_TS, eventModel.getUpdateTS());
        Map<String, Object> firstUserMap = new HashMap<>();
        firstUserMap.put(DOCUMENT_USER_ID, eventModel.getHostId());
        firstUserMap.put(DOCUMENT_USER_FULL_NAME, eventModel.getHostName());
        firstUserMap.put(DOCUMENT_USER_PROFILE_IMG, eventModel.getHostProfileImg());
        firstUserMap.put(DOCUMENT_USER_LINK_FACEBOOK, mainUserProfileModel.getLinkFacebook());
        firstUserMap.put(DOCUMENT_USER_LINK_INSTAGRAM, mainUserProfileModel.getLinkInstagram());
        firstUserMap.put(DOCUMENT_USER_LINK_TELEGRAM, mainUserProfileModel.getLinkTelegram());
        firstUserMap.put(DOCUMENT_USER_LINK_TIKTOK, mainUserProfileModel.getLinkTikTok());

        Task<Void> task1 = null, task2 = null, task3 = null;
        if (eventAccessType == AccessType.PUBLIC || eventAccessType == AccessType.SELECTIVE) {
            task1 = getPublicEventDocument(eventId).set(event);
            task2 = getPublicEventJoinedUserDocument(eventId, eventModel.getHostId()).set(firstUserMap);
            task3 = getUserProfileDocument(eventModel.getHostId()).update(DOCUMENT_USER_HOSTED_PUBLIC_EVENTS, FieldValue.arrayUnion(eventId));
        } else if (eventAccessType == AccessType.PRIVATE) {
            task1 = getPrivateEventDocument(eventId).set(event);
            task2 = getPrivateEventJoinedUserDocument(eventId, eventModel.getHostId()).set(firstUserMap);
            task3 = getUserProfileDocument(eventModel.getHostId()).update(DOCUMENT_USER_HOSTED_PRIVATE_EVENTS, FieldValue.arrayUnion(eventId));
        }
        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1, task2, task3);
        allTasks.addOnSuccessListener(querySnapshots -> {
            instanceCreateEvent = false;
            Log.d("DEBUG", "New event created! EventId: " + finalEventId);
            CustomToastUtil.showSuccessToast(context, context.getResources().getString(R.string.new_event_event_created_notification));
        }).addOnFailureListener(e -> {
            instanceCreateEvent = false;
            Log.d("DEBUG", "Failed to create new event!");
            CustomToastUtil.showFailToast(context, context.getResources().getString(R.string.new_event_event_failed_create_notification));
        });
    }

    public void setUserJoinEvent(SetUserInteractEventCallback setUserInteractEventCallback,
                                 Context context, EventModel event, MainUserProfileModel mainUserProfileModel) {
        if (instanceJoinLeave) {
            return;
        }
        instanceJoinLeave = true;
        String eventTitle = event.getEventTitle();
        String eventId = event.getEventId();
        String userId = mainUserProfileModel.getUserId();
        String userFullName = mainUserProfileModel.getUserFullName();
        String userProfileImg = mainUserProfileModel.getUserProfileImg();
        String userLinkFacebook = mainUserProfileModel.getLinkFacebook();
        String userLinkInstagram = mainUserProfileModel.getLinkInstagram();
        String userLinkTelegram = mainUserProfileModel.getLinkTelegram();
        String userLinkTikTok = mainUserProfileModel.getLinkTikTok();
        AccessType accessType = event.getAccessType();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put(DOCUMENT_USER_ID, userId);
        userMap.put(DOCUMENT_USER_FULL_NAME, userFullName);
        userMap.put(DOCUMENT_USER_PROFILE_IMG, userProfileImg);
        userMap.put(DOCUMENT_USER_LINK_FACEBOOK, userLinkFacebook);
        userMap.put(DOCUMENT_USER_LINK_INSTAGRAM, userLinkInstagram);
        userMap.put(DOCUMENT_USER_LINK_TELEGRAM, userLinkTelegram);
        userMap.put(DOCUMENT_USER_LINK_TIKTOK, userLinkTikTok);

        Task<Void> task1 = null, task2 = null;
        if (accessType == AccessType.PUBLIC || accessType == AccessType.SELECTIVE) {
            task1 = getPublicEventJoinedUserDocument(eventId, userId).set(userMap);
            task2 = getPublicEventDocument(eventId).update(DOCUMENT_EVENT_PARTICIPANTS, FieldValue.increment(1));
        } else if (accessType == AccessType.PRIVATE) {
            task1 = getPrivateEventJoinedUserDocument(eventId, userId).set(userMap);
            task2 = getPrivateEventDocument(eventId).update(DOCUMENT_EVENT_PARTICIPANTS, FieldValue.increment(1));
        }
        Task<Void> task3 = getUserProfileDocument(userId).update(DOCUMENT_USER_JOINED_EVENTS, FieldValue.arrayUnion(eventId));
        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1, task2, task3);
        allTasks.addOnSuccessListener(querySnapshots -> joinUserReturnSuccess(context, eventTitle, setUserInteractEventCallback))
                .addOnFailureListener(e -> joinUserReturnFail(context, eventTitle, setUserInteractEventCallback));
    }

    private void joinUserReturnFail(Context context, String eventTitle, SetUserInteractEventCallback setUserInteractEventCallback) {
        Log.d("DEBUG", "Failed to join event: " + eventTitle + "!");
        CustomToastUtil.showFailToast(context, context.getResources().getString(R.string.event_detailed_join_fail) + eventTitle + "!");
        instanceJoinLeave = false;
        setUserInteractEventCallback.onResponse(false);
    }

    private void joinUserReturnSuccess(Context context, String eventTitle, SetUserInteractEventCallback setUserInteractEventCallback) {
        Log.d("DEBUG", "You joined event: " + eventTitle + "!");
        CustomToastUtil.showSuccessToast(context, context.getResources().getString(R.string.event_detailed_join_success) + eventTitle + "!");
        instanceJoinLeave = false;
        setUserInteractEventCallback.onResponse(true);
    }

    public void setUserLeaveEvent(SetUserInteractEventCallback setUserInteractEventCallback, Context context, EventModel event, MainUserProfileModel mainUserProfileModel) {
        if (instanceJoinLeave) {
            return;
        }
        instanceJoinLeave = true;
        String eventTitle = event.getEventTitle();
        String eventId = event.getEventId();
        String userId = mainUserProfileModel.getUserId();
        AccessType accessType = event.getAccessType();

        Task<Void> task1 = null, task2 = null;
        if (accessType == AccessType.PUBLIC || accessType == AccessType.SELECTIVE) {
            task1 = getPublicEventJoinedUserDocument(eventId, userId).delete();
            task2 = getPublicEventDocument(eventId).update(DOCUMENT_EVENT_PARTICIPANTS, FieldValue.increment(-1));
        } else if (accessType == AccessType.PRIVATE) {
            task1 = getPrivateEventJoinedUserDocument(eventId, userId).delete();
            task2 = getPrivateEventDocument(eventId).update(DOCUMENT_EVENT_PARTICIPANTS, FieldValue.increment(-1));
        }
        Task<Void> task3 = getUserProfileDocument(userId).update(DOCUMENT_USER_JOINED_EVENTS, FieldValue.arrayRemove(eventId));
        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1, task2, task3);
        allTasks.addOnSuccessListener(querySnapshots -> leaveUserReturnSuccess(context, eventTitle, setUserInteractEventCallback))
                .addOnFailureListener(e -> leaveUserReturnFail(context, eventTitle, setUserInteractEventCallback));
    }

    private void leaveUserReturnFail(Context context, String eventTitle, SetUserInteractEventCallback setUserInteractEventCallback) {
        Log.d("DEBUG", "Failed to leave event: " + eventTitle + "!");
        CustomToastUtil.showFailToast(context, context.getResources().getString(R.string.event_detailed_leave_fail) + eventTitle + "!");
        instanceJoinLeave = false;
        setUserInteractEventCallback.onResponse(false);
    }

    private void leaveUserReturnSuccess(Context context, String eventTitle, SetUserInteractEventCallback setUserInteractEventCallback) {
        Log.d("DEBUG", "You left event: " + eventTitle + "!");
        CustomToastUtil.showSuccessToast(context, context.getResources().getString(R.string.event_detailed_leave_success) + eventTitle + "!");
        instanceJoinLeave = false;
        setUserInteractEventCallback.onResponse(true);
    }

    public void isUserRegistered(GetUserCallback getUserCallback, String userId){
        getUserProfileDocument(userId).get(Source.SERVER).addOnCompleteListener(task -> {
            DocumentSnapshot document = task.getResult();
            getUserCallback.onResponse(document.exists());
        });
    }

    public void getUserProfile(GetUserProfileCallback getUserProfileCallback, String userId) {
        getUserProfileDocument(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.getData() != null) {
                getUserProfileCallback.onResponse(convertUserProfileDocumentToModel(documentSnapshot.getData()));
            } else {
                getUserProfileCallback.onResponse(null);
            }
        }).addOnFailureListener(e -> getUserProfileCallback.onResponse(null));
    }

    public void getEventList(GetEventListCallback getEventListCallback) {
        getPublicEventsCollection().get().addOnSuccessListener(queryDocumentSnapshots -> {
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
        eventListListener = getPublicEventsCollection().addSnapshotListener((value, error) -> {
            Log.d("DEBUG", "ADDED SNAPSHOT LISTENER");
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
            Log.d("DEBUG", "REMOVED SNAPSHOT LISTENER");
            eventListListener.remove();
        }
    }

    public void getEventParticipants(GetEventParticipantsCallback getEventParticipantsCallback, EventModel eventModel) {
        String eventId = eventModel.getEventId();
        Task<QuerySnapshot> task = null;

        if (eventModel.getAccessType() == AccessType.PUBLIC || eventModel.getAccessType() == AccessType.SELECTIVE) {
            task = getPublicEventParticipantsCollection(eventId).get();
        } else {
            task = getPrivateEventParticipantsCollection(eventId).get();
        }

        task.addOnSuccessListener(queryDocumentSnapshots -> {
            List<CommonUserModel> usersList = new ArrayList<>();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                if (document != null) {
                    CommonUserModel userProfile = convertCommonUserProfileDocumentToModel(document.getData());
                    usersList.add(userProfile);
                }
            }
            getEventParticipantsCallback.onResponse(usersList);
        }).addOnFailureListener(e -> getEventParticipantsCallback.onResponse(null));
    }

    public void setEventAsLikedByUser(SetUserInteractEventCallback setUserInteractEventCallback, String userId, String eventId) {
        if (instanceLike) {
            return;
        }
        instanceLike = true;
        Task<Void> task1 = getUserProfileDocument(userId).update(DOCUMENT_USER_LIKED_EVENTS, FieldValue.arrayUnion(eventId));
        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1);
        allTasks.addOnSuccessListener(querySnapshots -> {
            instanceLike = false;
            setUserInteractEventCallback.onResponse(true);
        }).addOnFailureListener(e -> {
            instanceLike = false;
            setUserInteractEventCallback.onResponse(false);
        });
    }

    private EventModel convertEventDocumentToModel(Map<String, Object> map) {
        return new EventModel(map.get(DOCUMENT_EVENT_HOST_ID).toString(), map.get(DOCUMENT_EVENT_HOST_NAME).toString(),
                convertObjectToString(map.get(DOCUMENT_EVENT_HOST_PROFILE_IMG)), map.get(DOCUMENT_EVENT_ID).toString(),
                map.get(DOCUMENT_EVENT_TITLE).toString(), map.get(DOCUMENT_EVENT_LOCATION).toString(),
                convertMapToLatLng((HashMap <String, Number>) map.get(DOCUMENT_EVENT_GEOLOCATION)),
                ((Timestamp) map.get(DOCUMENT_EVENT_START_TIME)).toDate(),
                ((Timestamp) map.get(DOCUMENT_EVENT_END_TIME)).toDate(),
                AccessType.valueOf(map.get(DOCUMENT_EVENT_ACCESS_TYPE).toString()),
                map.get(DOCUMENT_EVENT_DESCRIPTION).toString(),
                convertObjectToInteger(map.get(DOCUMENT_EVENT_MIN_AGE)), convertObjectToInteger(map.get(DOCUMENT_EVENT_MAX_AGE)),
                convertObjectToInteger(map.get(DOCUMENT_EVENT_PARTICIPANTS)), null, convertObjectToInteger(map.get(DOCUMENT_EVENT_MAX_PERSON)),
                convertObjectToString(map.get(DOCUMENT_EVENT_AVATAR)), convertEventThemes((ArrayList<Object[]>) map.get(DOCUMENT_EVENT_THEMES)),
                ((Timestamp) map.get(DOCUMENT_EVENT_CREATE_TS)), ((Timestamp) map.get(DOCUMENT_EVENT_UPDATE_TS)));
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

    private MainUserProfileModel convertUserProfileDocumentToModel(Map<String, Object> map) {
        return new MainUserProfileModel(map.get(DOCUMENT_USER_ID).toString(), convertObjectToString(map.get(DOCUMENT_USER_EMAIL)),
                convertObjectToString(map.get(DOCUMENT_USER_PHONE)), map.get(DOCUMENT_USER_FULL_NAME).toString(),
                convertObjectToString(map.get(DOCUMENT_USER_PROFILE_IMG)),
                convertUserProfileEvents((ArrayList<Object[]>) map.get(DOCUMENT_USER_JOINED_EVENTS)),
                convertUserProfileEvents((ArrayList<Object[]>) map.get(DOCUMENT_USER_HOSTED_PUBLIC_EVENTS)),
                convertUserProfileEvents((ArrayList<Object[]>) map.get(DOCUMENT_USER_HOSTED_PRIVATE_EVENTS)),
                convertUserProfileEvents((ArrayList<Object[]>) map.get(DOCUMENT_USER_LIKED_EVENTS)),
                convertObjectToString(map.get(DOCUMENT_USER_LINK_TELEGRAM)),
                convertObjectToString(map.get(DOCUMENT_USER_LINK_TIKTOK)),
                convertObjectToString(map.get(DOCUMENT_USER_LINK_INSTAGRAM)),
                convertObjectToString(map.get(DOCUMENT_USER_LINK_FACEBOOK)));
    }

    private CommonUserModel convertCommonUserProfileDocumentToModel(Map<String, Object> map) {
        return new CommonUserModel(map.get(DOCUMENT_USER_ID).toString(), map.get(DOCUMENT_USER_FULL_NAME).toString(),
                convertObjectToString(map.get(DOCUMENT_USER_PROFILE_IMG)),
                convertUserProfileEvents((ArrayList<Object[]>) map.get(DOCUMENT_USER_JOINED_EVENTS)),
                convertUserProfileEvents((ArrayList<Object[]>) map.get(DOCUMENT_USER_HOSTED_PUBLIC_EVENTS)),
                convertUserProfileEvents((ArrayList<Object[]>) map.get(DOCUMENT_USER_LIKED_EVENTS)),
                convertObjectToString(map.get(DOCUMENT_USER_LINK_TELEGRAM)),
                convertObjectToString(map.get(DOCUMENT_USER_LINK_TIKTOK)),
                convertObjectToString(map.get(DOCUMENT_USER_LINK_INSTAGRAM)),
                convertObjectToString(map.get(DOCUMENT_USER_LINK_FACEBOOK)));
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
