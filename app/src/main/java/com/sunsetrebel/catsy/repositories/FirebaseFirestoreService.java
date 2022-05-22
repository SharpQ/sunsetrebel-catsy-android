package com.sunsetrebel.catsy.repositories;

import android.content.Context;
import android.util.Log;

import androidx.lifecycle.MutableLiveData;

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
import com.sunsetrebel.catsy.models.InviteToEventModel;
import com.sunsetrebel.catsy.models.InviteToFriendsListModel;
import com.sunsetrebel.catsy.models.MainUserProfileModel;
import com.sunsetrebel.catsy.enums.AccessType;
import com.sunsetrebel.catsy.utils.CustomToastUtil;

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
    private static MutableLiveData<List<Object>> notificationsMutableLiveData = new MutableLiveData<>();
    private ListenerRegistration notificationsListener = null;
    private static boolean instanceJoinLeave = false;
    private static boolean instanceLike = false;
    private static boolean instanceCreateEvent = false;
    private static boolean instanceBlockUser = false;
    private static boolean instanceFriendRequest = false;

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

    //COLLECTION REFS
    private CollectionReference getPublicEventsCollection() {
        return fStore.collection(FirestoreKeys.Collections.COLLECTION_PUBLIC_EVENTS);
    }

    private CollectionReference getPublicEventParticipantsCollection(String eventId) {
        return getPublicEventDocument(eventId).collection(FirestoreKeys.Collections.COLLECTION_EVENT_USERS_JOINED);
    }

    private CollectionReference getPrivateEventParticipantsCollection(String eventId) {
        return getPrivateEventDocument(eventId).collection(FirestoreKeys.Collections.COLLECTION_EVENT_USERS_JOINED);
    }

    private CollectionReference getIncomeInvitesCollection(String userId) {
        return getUserProfileDocument(userId).collection(FirestoreKeys.Collections.COLLECTION_USER_INCOME_REQUESTS);
    }

    //DOCUMENTS REFS
    private DocumentReference getUserProfileDocument(String userId) {
        return fStore.collection(FirestoreKeys.Collections.COLLECTION_USER_PROFILES).document(userId);
    }

    private DocumentReference getUserProfileOutcomeRequestDocument(String userId,
                                                                   String anotherUserId) {
        return getUserProfileDocument(userId).collection(FirestoreKeys.Collections.COLLECTION_USER_OUTCOME_REQUESTS).document(anotherUserId);
    }

    private DocumentReference getUserProfileIncomeRequestDocument(String userId, String inviteId) {
        return getIncomeInvitesCollection(userId).document(inviteId);
    }

    private DocumentReference getPublicEventDocument(String eventId) {
        return getPublicEventsCollection().document(eventId);
    }

    private DocumentReference getPublicEventJoinedUserDocument(String eventId, String userId) {
        return getPublicEventParticipantsCollection(eventId).document(userId);
    }

    private DocumentReference getPrivateEventDocument(String eventId) {
        return fStore.collection(FirestoreKeys.Collections.COLLECTION_PRIVATE_EVENTS).document(eventId);
    }

    private DocumentReference getPrivateEventJoinedUserDocument(String eventId, String userId) {
        return getPrivateEventParticipantsCollection(eventId).document(userId);
    }

    private String getIdForEventDocument(AccessType eventAccessType) {
        if (eventAccessType == AccessType.PUBLIC || eventAccessType == AccessType.SELECTIVE) {
            return fStore.collection(FirestoreKeys.Collections.COLLECTION_PUBLIC_EVENTS).document().getId();
        } else {
            return fStore.collection(FirestoreKeys.Collections.COLLECTION_PRIVATE_EVENTS).document().getId();
        }
    }

    //FIRESTORE METHODS
    public void createNewUser(String userId, String fullName, String email, String phone, String profileUrl) {
        Map<String, Object> user = new HashMap<>();
        if (fullName == null) {
            fullName = "userName";
        }
        user.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_ID, userId);
        user.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_FULL_NAME, fullName);
        user.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_EMAIL, email);
        user.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_PHONE, phone);
        user.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_PROFILE_IMG, profileUrl);
        user.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_FRIENDS, null);
        user.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_BLOCKED_USERS, null);
        user.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_JOINED_PUBLIC_EVENTS, null);
        user.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_JOINED_PRIVATE_EVENTS, null);
        user.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_LIKED_EVENTS, null);
        user.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_HOSTED_PUBLIC_EVENTS, null);
        user.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_HOSTED_PRIVATE_EVENTS, null);
        user.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_LINK_TELEGRAM, null);
        user.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_LINK_TIKTOK, null);
        user.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_LINK_INSTAGRAM, null);
        user.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_LINK_FACEBOOK, null);
        getUserProfileDocument(userId).set(user).addOnSuccessListener(aVoid -> Log.d("DEBUG", "User profile created! UserID: " + userId));
    }

    public void createNewEvent(Context context, EventModel eventModel,
                               MainUserProfileModel mainUserProfileModel) {
        if (instanceCreateEvent) {
            return;
        }
        instanceCreateEvent = true;
        Timestamp createTS = new Timestamp(new Date());
        eventModel.setCreateTS(createTS);
        eventModel.setUpdateTS(createTS);
        String eventId = getIdForEventDocument(eventModel.getAccessType());
        Map<String, Object> event = new HashMap<>();
        event.put(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_ID, eventId);
        event.put(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_TITLE, eventModel.getEventTitle());
        event.put(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_LOCATION, eventModel.getEventLocation());
        event.put(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_GEOLOCATION, eventModel.getEventGeoLocation());
        event.put(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_START_TIME, eventModel.getEventStartTime());
        event.put(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_END_TIME, eventModel.getEventEndTime());
        event.put(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_ACCESS_TYPE, eventModel.getAccessType());
        event.put(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_DESCRIPTION, eventModel.getEventDescr());
        event.put(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_MIN_AGE, eventModel.getEventMinAge());
        event.put(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_MAX_AGE, eventModel.getEventMaxAge());
        event.put(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_MAX_PERSON, eventModel.getEventMaxPerson());
        event.put(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_AVATAR, eventModel.getEventAvatar());
        event.put(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_THEMES, eventModel.getEventThemes());
        event.put(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_PARTICIPANTS, 0);
        event.put(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_HOST_ID, eventModel.getHostId());
        event.put(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_HOST_NAME, eventModel.getHostName());
        event.put(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_HOST_PROFILE_IMG, eventModel.getHostProfileImg());
        event.put(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_CREATE_TS, eventModel.getCreateTS());
        event.put(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_UPDATE_TS, eventModel.getUpdateTS());
        if (eventModel.getAccessType() == AccessType.PRIVATE) {
            event.put(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_INVITED_USERS, eventModel.getInvitedUsers());
        }
        Map<String, Object> firstUserMap = new HashMap<>();
        firstUserMap.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_ID, eventModel.getHostId());
        firstUserMap.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_FULL_NAME, eventModel.getHostName());
        firstUserMap.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_PROFILE_IMG, eventModel.getHostProfileImg());
        firstUserMap.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_LINK_FACEBOOK, mainUserProfileModel.getLinkFacebook());
        firstUserMap.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_LINK_INSTAGRAM, mainUserProfileModel.getLinkInstagram());
        firstUserMap.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_LINK_TELEGRAM, mainUserProfileModel.getLinkTelegram());
        firstUserMap.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_LINK_TIKTOK, mainUserProfileModel.getLinkTikTok());

        List<Task<Void>> tasks = new ArrayList<>();
        if (eventModel.getAccessType() == AccessType.PUBLIC || eventModel.getAccessType() == AccessType.SELECTIVE) {
            tasks.add(getPublicEventDocument(eventId).set(event));
            tasks.add(getPublicEventJoinedUserDocument(eventId, eventModel.getHostId()).set(firstUserMap));
            tasks.add(getUserProfileDocument(eventModel.getHostId()).update(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_HOSTED_PUBLIC_EVENTS, FieldValue.arrayUnion(eventId)));
        } else if (eventModel.getAccessType() == AccessType.PRIVATE) {
            tasks.add(getPrivateEventDocument(eventId).set(event));
            tasks.add(getPrivateEventJoinedUserDocument(eventId, eventModel.getHostId()).set(firstUserMap));
            tasks.add(getUserProfileDocument(eventModel.getHostId()).update(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_HOSTED_PRIVATE_EVENTS, FieldValue.arrayUnion(eventId)));
        }
        if (eventModel.getInvitedUsers().size() > 0) {
            for (String userId : eventModel.getInvitedUsers()) {
                Map<String, Object> inviteRequest = new HashMap<>();
                inviteRequest.put(FirestoreKeys.Documents.EventInvite.DOCUMENT_INVITE_REQUEST_ACTION, "EVENT_INVITE");
                inviteRequest.put(FirestoreKeys.Documents.EventInvite.DOCUMENT_INVITE_REQUEST_SENDER_ID, eventModel.getHostId());
                inviteRequest.put(FirestoreKeys.Documents.EventInvite.DOCUMENT_INVITE_REQUEST_SENDER_NAME, eventModel.getHostName());
                inviteRequest.put(FirestoreKeys.Documents.EventInvite.DOCUMENT_INVITE_REQUEST_SENDER_PROFILE_IMG, eventModel.getHostProfileImg());
                inviteRequest.put(FirestoreKeys.Documents.EventInvite.DOCUMENT_INVITE_REQUEST_EVENT_ID, eventId);
                inviteRequest.put(FirestoreKeys.Documents.EventInvite.DOCUMENT_INVITE_REQUEST_EVENT_TITLE, eventModel.getEventTitle());
                inviteRequest.put(FirestoreKeys.Documents.EventInvite.DOCUMENT_INVITE_REQUEST_EVENT_DESCR, eventModel.getEventDescr());
                inviteRequest.put(FirestoreKeys.Documents.EventInvite.DOCUMENT_INVITE_REQUEST_EVENT_LOCATION, eventModel.getEventLocation());
                inviteRequest.put(FirestoreKeys.Documents.EventInvite.DOCUMENT_INVITE_REQUEST_EVENT_START_TIME, eventModel.getEventStartTime());
                inviteRequest.put(FirestoreKeys.Documents.EventInvite.DOCUMENT_INVITE_REQUEST_EVENT_AVATAR, eventModel.getEventAvatar());
                inviteRequest.put(FirestoreKeys.Documents.EventInvite.DOCUMENT_INVITE_REQUEST_EVENT_ACCESS_TYPE, eventModel.getAccessType());
                inviteRequest.put(FirestoreKeys.Documents.EventInvite.DOCUMENT_INVITE_REQUEST_RECIPIENT, userId);
                inviteRequest.put(FirestoreKeys.Documents.EventInvite.DOCUMENT_INVITE_REQUEST_CREATE_TS, eventModel.getCreateTS());
                tasks.add(getUserProfileIncomeRequestDocument(userId, eventId).set(inviteRequest));
            }
        }
        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(tasks);
        allTasks.addOnSuccessListener(querySnapshots -> {
            instanceCreateEvent = false;
            Log.d("DEBUG", "New event created! EventId: " + eventId);
            CustomToastUtil.showSuccessToast(context, context.getResources().getString(R.string.new_event_event_created_notification));
        }).addOnFailureListener(e -> {
            instanceCreateEvent = false;
            Log.d("DEBUG", "Failed to create new event!" + eventId);
            CustomToastUtil.showFailToast(context, context.getResources().getString(R.string.new_event_event_failed_create_notification));
        });
    }

    public void setUserJoinEvent(SetUserInteractEventCallback setUserInteractEventCallback,
                                 EventModel event, MainUserProfileModel mainUserProfileModel) {
        if (instanceJoinLeave) {
            return;
        }
        instanceJoinLeave = true;
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
        userMap.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_ID, userId);
        userMap.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_FULL_NAME, userFullName);
        userMap.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_PROFILE_IMG, userProfileImg);
        userMap.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_LINK_FACEBOOK, userLinkFacebook);
        userMap.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_LINK_INSTAGRAM, userLinkInstagram);
        userMap.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_LINK_TELEGRAM, userLinkTelegram);
        userMap.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_LINK_TIKTOK, userLinkTikTok);

        Task<Void> task1 = null, task2 = null;
        if (accessType == AccessType.PUBLIC || accessType == AccessType.SELECTIVE) {
            task1 = getPublicEventJoinedUserDocument(eventId, userId).set(userMap);
            task2 = getUserProfileDocument(userId).update(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_JOINED_PUBLIC_EVENTS, FieldValue.arrayUnion(eventId));
        } else if (accessType == AccessType.PRIVATE) {
            task1 = getPrivateEventJoinedUserDocument(eventId, userId).set(userMap);
            task2 = getUserProfileDocument(userId).update(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_JOINED_PRIVATE_EVENTS, FieldValue.arrayUnion(eventId));
        }
        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1, task2);
        allTasks.addOnSuccessListener(querySnapshots -> {
            instanceJoinLeave = false;
            setUserInteractEventCallback.onResponse(true);
        }).addOnFailureListener(e -> {
            instanceJoinLeave = false;
            setUserInteractEventCallback.onResponse(false);
        });
    }

    public void setUserLeaveEvent(SetUserInteractEventCallback setUserInteractEventCallback,
                                  EventModel event, MainUserProfileModel mainUserProfileModel) {
        if (instanceJoinLeave) {
            return;
        }
        instanceJoinLeave = true;
        String eventId = event.getEventId();
        String userId = mainUserProfileModel.getUserId();
        AccessType accessType = event.getAccessType();

        Task<Void> task1 = null, task2 = null;
        if (accessType == AccessType.PUBLIC || accessType == AccessType.SELECTIVE) {
            task1 = getPublicEventJoinedUserDocument(eventId, userId).delete();
            task2 = getUserProfileDocument(userId).update(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_JOINED_PUBLIC_EVENTS, FieldValue.arrayRemove(eventId));
        } else if (accessType == AccessType.PRIVATE) {
            task1 = getPrivateEventJoinedUserDocument(eventId, userId).delete();
            task2 = getUserProfileDocument(userId).update(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_JOINED_PRIVATE_EVENTS, FieldValue.arrayRemove(eventId));
        }
        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1, task2);
        allTasks.addOnSuccessListener(querySnapshots -> {
            instanceJoinLeave = false;
            setUserInteractEventCallback.onResponse(true);
        }).addOnFailureListener(e -> {
            instanceJoinLeave = false;
            setUserInteractEventCallback.onResponse(false);
        });
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
                getUserProfileCallback.onResponse(FirestoreToModelConverter.convertUserProfileDocumentToModel(documentSnapshot.getData()));
            } else {
                getUserProfileCallback.onResponse(null);
            }
        }).addOnFailureListener(e -> getUserProfileCallback.onResponse(null));
    }

    public void getMultipleUsersProfile(GetEventParticipantsCallback getEventParticipantsCallback,
                                        List<String> multipleUserId) {
        List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
        for (String userId : multipleUserId) {
            tasks.add(getUserProfileDocument(userId).get());
        }
        Task<List<DocumentSnapshot>> allTasks = Tasks.whenAllSuccess(tasks);
        allTasks.addOnSuccessListener(documentSnapshots -> {
            List<CommonUserModel> usersList = new ArrayList<>();
            for (DocumentSnapshot document : documentSnapshots) {
                if (document.getData() != null) {
                    usersList.add(FirestoreToModelConverter.convertCommonUserProfileDocumentToModel(document.getData()));
                }
            }
            getEventParticipantsCallback.onResponse(usersList);
        }).addOnFailureListener(e -> getEventParticipantsCallback.onResponse(null));
    }

    public void getEventList(GetEventListCallback getEventListCallback) {
        getPublicEventsCollection().get().addOnSuccessListener(queryDocumentSnapshots -> {
            List<EventModel> eventList = new ArrayList<>();
            for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                if (document != null) {
                    EventModel event = FirestoreToModelConverter.convertEventDocumentToModel(document.getData());
                    eventList.add(event);
                }
            }
            getEventListCallback.onResponse(eventList);
        }).addOnFailureListener(e -> getEventListCallback.onResponse(null));
    }

    public MutableLiveData<List<EventModel>> getEventListMutableLiveData() {
        eventListListener = getPublicEventsCollection().addSnapshotListener((value, error) -> {
            Log.d("DEBUG", "Added public event list snapshot listener");
            List<EventModel> eventList = new ArrayList<>();
            for (QueryDocumentSnapshot document : value) {
                if (document != null) {
                    EventModel event = FirestoreToModelConverter.convertEventDocumentToModel(document.getData());
                    eventList.add(event);
                }
            }
            eventListMutableLiveData.postValue(eventList);
        });
        return eventListMutableLiveData;
    }

    public void removeEventListListener() {
        if (eventListListener != null) {
            Log.d("DEBUG", "Removed public event list snapshot listener");
            eventListListener.remove();
        }
    }

    public MutableLiveData<List<Object>> getNotificationsMutableLiveData(String userId) {
        notificationsListener = getIncomeInvitesCollection(userId).addSnapshotListener((value, error) -> {
            Log.d("DEBUG", "Added income notifications snapshot listener");
            List<Object> notificationsList = new ArrayList<>();
            if (value != null) {
                for (QueryDocumentSnapshot document : value) {
                    if (document != null) {
                        Object notification = FirestoreToModelConverter.convertNotificationDocumentToModel(document.getData());
                        if (notification != null) {
                            notificationsList.add(notification);
                        }
                    }
                }
            }
            notificationsMutableLiveData.postValue(notificationsList);
        });
        return notificationsMutableLiveData;
    }

    public void removeNotificationsListener() {
        if (notificationsListener != null) {
            Log.d("DEBUG", "Removed income notifications listener");
            notificationsListener.remove();
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
                    CommonUserModel userProfile = FirestoreToModelConverter.convertCommonUserProfileDocumentToModel(document.getData());
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
        Task<Void> task1 = getUserProfileDocument(userId).update(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_LIKED_EVENTS, FieldValue.arrayUnion(eventId));
        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1);
        allTasks.addOnSuccessListener(querySnapshots -> {
            instanceLike = false;
            setUserInteractEventCallback.onResponse(true);
        }).addOnFailureListener(e -> {
            instanceLike = false;
            setUserInteractEventCallback.onResponse(false);
        });
    }

    public void sendFriendRequest(SetUserInteractEventCallback setUserInteractEventCallback,
                                  MainUserProfileModel currentUserProfile, String anotherUserId) {
        if (instanceFriendRequest) {
            return;
        }
        instanceFriendRequest = true;
        Timestamp createTS = new Timestamp(new Date());
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put(FirestoreKeys.Documents.FriendInvite.DOCUMENT_FRIEND_REQUEST_ACTION, "ADD_FRIEND");
        requestBody.put(FirestoreKeys.Documents.FriendInvite.DOCUMENT_FRIEND_REQUEST_SENDER_ID, currentUserProfile.getUserId());
        requestBody.put(FirestoreKeys.Documents.FriendInvite.DOCUMENT_FRIEND_REQUEST_SENDER_NAME, currentUserProfile.getUserFullName());
        requestBody.put(FirestoreKeys.Documents.FriendInvite.DOCUMENT_FRIEND_REQUEST_SENDER_PROFILE_IMG, currentUserProfile.getUserProfileImg());
        requestBody.put(FirestoreKeys.Documents.FriendInvite.DOCUMENT_FRIEND_REQUEST_RECIPIENT, anotherUserId);
        requestBody.put(FirestoreKeys.Documents.FriendInvite.DOCUMENT_FRIEND_REQUEST_CREATE_TS, createTS);

        Task<Void> task = getUserProfileOutcomeRequestDocument(currentUserProfile.getUserId(), anotherUserId).set(requestBody);
        task.addOnSuccessListener(querySnapshots -> {
            instanceFriendRequest = false;
            setUserInteractEventCallback.onResponse(true);
        }).addOnFailureListener(e -> {
            instanceFriendRequest = false;
            setUserInteractEventCallback.onResponse(false);
        });
    }

    public void removeFriendRequest(SetUserInteractEventCallback setUserInteractEventCallback,
                                  String userId, String anotherUserId) {
        if (instanceFriendRequest) {
            return;
        }
        instanceFriendRequest = true;
        Timestamp createTS = new Timestamp(new Date());
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put(FirestoreKeys.Documents.FriendInvite.DOCUMENT_FRIEND_REQUEST_SENDER_ID, userId);
        requestBody.put(FirestoreKeys.Documents.FriendInvite.DOCUMENT_FRIEND_REQUEST_RECIPIENT, anotherUserId);
        requestBody.put(FirestoreKeys.Documents.FriendInvite.DOCUMENT_FRIEND_REQUEST_ACTION, "REMOVE_FRIEND");
        requestBody.put(FirestoreKeys.Documents.FriendInvite.DOCUMENT_FRIEND_REQUEST_CREATE_TS, createTS);
        Task<Void> task1 = getUserProfileDocument(userId).update(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_FRIENDS, FieldValue.arrayRemove(anotherUserId));
        Task<Void> task2 = getUserProfileOutcomeRequestDocument(userId, anotherUserId).set(requestBody);
        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1, task2);
        allTasks.addOnSuccessListener(querySnapshots -> {
            instanceFriendRequest = false;
            setUserInteractEventCallback.onResponse(true);
        }).addOnFailureListener(e -> {
            instanceFriendRequest = false;
            setUserInteractEventCallback.onResponse(false);
        });
    }

    public void acceptFriendInvite(SetUserInteractEventCallback setUserInteractEventCallback,
                                   InviteToFriendsListModel inviteToFriendsList) {
        if (instanceFriendRequest) {
            return;
        }
        instanceFriendRequest = true;
        String senderId = inviteToFriendsList.getSenderId();
        String recipientId = inviteToFriendsList.getRecipientId();
        Task<Void> task1 = getUserProfileDocument(recipientId).update(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_FRIENDS, FieldValue.arrayUnion(senderId));
        Task<Void> task2 = getUserProfileIncomeRequestDocument(recipientId, senderId).delete();
        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1, task2);
        allTasks.addOnSuccessListener(querySnapshots -> {
            instanceFriendRequest = false;
            setUserInteractEventCallback.onResponse(true);
        }).addOnFailureListener(e -> {
            instanceFriendRequest = false;
            setUserInteractEventCallback.onResponse(false);
        });
    }

    public void declineFriendInvite(SetUserInteractEventCallback setUserInteractEventCallback,
                                    InviteToFriendsListModel inviteToFriendsList) {
        if (instanceFriendRequest) {
            return;
        }
        instanceFriendRequest = true;
        String senderId = inviteToFriendsList.getSenderId();
        String recipientId = inviteToFriendsList.getRecipientId();
        Task<Void> task1 = getUserProfileIncomeRequestDocument(recipientId, senderId).delete();
        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1);
        allTasks.addOnSuccessListener(querySnapshots -> {
            instanceFriendRequest = false;
            setUserInteractEventCallback.onResponse(true);
        }).addOnFailureListener(e -> {
            instanceFriendRequest = false;
            setUserInteractEventCallback.onResponse(false);
        });
    }

    public void acceptEventInvite(SetUserInteractEventCallback setUserInteractEventCallback,
                                  MainUserProfileModel mainUserProfileModel,
                                  InviteToEventModel inviteToEventModel) {
        if (instanceJoinLeave) {
            return;
        }
        instanceJoinLeave = true;
        String eventId = inviteToEventModel.getEventId();
        String userId = mainUserProfileModel.getUserId();
        String userFullName = mainUserProfileModel.getUserFullName();
        String userProfileImg = mainUserProfileModel.getUserProfileImg();
        String userLinkFacebook = mainUserProfileModel.getLinkFacebook();
        String userLinkInstagram = mainUserProfileModel.getLinkInstagram();
        String userLinkTelegram = mainUserProfileModel.getLinkTelegram();
        String userLinkTikTok = mainUserProfileModel.getLinkTikTok();
        AccessType accessType = inviteToEventModel.getAccessType();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_ID, userId);
        userMap.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_FULL_NAME, userFullName);
        userMap.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_PROFILE_IMG, userProfileImg);
        userMap.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_LINK_FACEBOOK, userLinkFacebook);
        userMap.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_LINK_INSTAGRAM, userLinkInstagram);
        userMap.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_LINK_TELEGRAM, userLinkTelegram);
        userMap.put(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_LINK_TIKTOK, userLinkTikTok);

        Task<Void> task1 = null, task2 = null;
        if (accessType == AccessType.PUBLIC || accessType == AccessType.SELECTIVE) {
            task1 = getPublicEventJoinedUserDocument(eventId, userId).set(userMap);
            task2 = getUserProfileDocument(userId).update(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_JOINED_PUBLIC_EVENTS, FieldValue.arrayUnion(eventId));
        } else if (accessType == AccessType.PRIVATE) {
            task1 = getPrivateEventJoinedUserDocument(eventId, userId).set(userMap);
            task2 = getUserProfileDocument(userId).update(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_JOINED_PRIVATE_EVENTS, FieldValue.arrayUnion(eventId));
        }
        Task<Void> task3 = getUserProfileIncomeRequestDocument(userId, eventId).delete();
        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1, task2, task3);
        allTasks.addOnSuccessListener(querySnapshots -> {
            instanceJoinLeave = false;
            setUserInteractEventCallback.onResponse(true);
        }).addOnFailureListener(e -> {
            instanceJoinLeave = false;
            setUserInteractEventCallback.onResponse(false);
        });
    }

    public void declineEventInvite(SetUserInteractEventCallback setUserInteractEventCallback,
                                  InviteToEventModel inviteToEventModel) {
        if (instanceJoinLeave) {
            return;
        }
        instanceJoinLeave = true;
        String eventId = inviteToEventModel.getEventId();
        String recipientId = inviteToEventModel.getRecipientId();

        Task<Void> task = getUserProfileIncomeRequestDocument(recipientId, eventId).delete();
        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task);
        allTasks.addOnSuccessListener(querySnapshots -> {
            instanceJoinLeave = false;
            setUserInteractEventCallback.onResponse(true);
        }).addOnFailureListener(e -> {
            instanceJoinLeave = false;
            setUserInteractEventCallback.onResponse(false);
        });
    }

    public void setUserToBlocked(SetUserInteractEventCallback setUserInteractEventCallback, String userId, String userBlockedId) {
        if (instanceBlockUser) {
            return;
        }
        instanceBlockUser = true;
        Task<Void> task = getUserProfileDocument(userId).update(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_BLOCKED_USERS,
                FieldValue.arrayUnion(userBlockedId));
        task.addOnSuccessListener(querySnapshots -> {
            instanceBlockUser = false;
            setUserInteractEventCallback.onResponse(true);
        }).addOnFailureListener(e -> {
            instanceBlockUser = false;
            setUserInteractEventCallback.onResponse(false);
        });
    }

    public void removeUserFromBlocked(SetUserInteractEventCallback setUserInteractEventCallback, String userId, String userBlockedId) {
        if (instanceBlockUser) {
            return;
        }
        instanceBlockUser = true;
        Task<Void> task = getUserProfileDocument(userId).update(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_BLOCKED_USERS,
                FieldValue.arrayRemove(userBlockedId));
        task.addOnSuccessListener(querySnapshots -> {
            instanceBlockUser = false;
            setUserInteractEventCallback.onResponse(true);
        }).addOnFailureListener(e -> {
            instanceBlockUser = false;
            setUserInteractEventCallback.onResponse(false);
        });
    }
}
