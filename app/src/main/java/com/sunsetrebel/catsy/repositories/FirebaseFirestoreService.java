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
import com.sunsetrebel.catsy.enums.AccessType;
import com.sunsetrebel.catsy.models.CommonUserModel;
import com.sunsetrebel.catsy.models.EventModel;
import com.sunsetrebel.catsy.models.InviteToEventModel;
import com.sunsetrebel.catsy.models.InviteToFriendsListModel;
import com.sunsetrebel.catsy.models.MainUserProfileModel;
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
    private static MutableLiveData<List<CommonUserModel>> friendListMutableLiveData = new MutableLiveData<>();
    private ListenerRegistration friendListListener = null;
    private static MutableLiveData<MainUserProfileModel> mainUserProfileMutableLiveData = new MutableLiveData<>();
    private ListenerRegistration mainUserProfileListener = null;
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

    //INTERFACES
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

    private CollectionReference getUserProfilesCollection() {
        return fStore.collection(FirestoreKeys.Collections.COLLECTION_USER_PROFILES);
    }

    private CollectionReference getUserProfileInfoDetailedCollection(String mainUserId) {
        return fStore.collection(FirestoreKeys.Collections.COLLECTION_USER_PROFILES)
                .document(mainUserId)
                .collection(FirestoreKeys.Collections.COLLECTION_USER_DETAILED_INFO);
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

    private DocumentReference getUserDetailedProfileDocument(String userId) {
        return fStore.collection(FirestoreKeys.Collections.COLLECTION_USER_PROFILES)
                .document(userId)
                .collection(FirestoreKeys.Collections.COLLECTION_USER_DETAILED_INFO)
                .document(userId);
    }

    private DocumentReference getUserProfileOutcomeRequestDocument(String userId,
                                                                   String anotherUserId) {
        return getUserProfileDocument(userId)
                .collection(FirestoreKeys.Collections.COLLECTION_USER_OUTCOME_REQUESTS)
                .document(anotherUserId);
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

    public String getIdForEventDocument(AccessType eventAccessType) {
        if (eventAccessType == AccessType.PUBLIC || eventAccessType == AccessType.SELECTIVE) {
            return fStore.collection(FirestoreKeys.Collections.COLLECTION_PUBLIC_EVENTS).document().getId();
        } else {
            return fStore.collection(FirestoreKeys.Collections.COLLECTION_PRIVATE_EVENTS).document().getId();
        }
    }

    //FIRESTORE METHODS
    public void createNewUser(String userId, String fullName, String email, String phone,
                              String profileUrl, String countryCodeValue) {
        if (fullName == null) {
            fullName = "userName";
        }
        Map<String, Object> userSocialLinks = new HashMap<>();
        userSocialLinks.put(FirestoreKeys.Documents.UserSocialLinks.LINK_TELEGRAM, null);
        userSocialLinks.put(FirestoreKeys.Documents.UserSocialLinks.LINK_TIKTOK, null);
        userSocialLinks.put(FirestoreKeys.Documents.UserSocialLinks.LINK_INSTAGRAM, null);
        userSocialLinks.put(FirestoreKeys.Documents.UserSocialLinks.LINK_FACEBOOK, null);

        Map<String, Object> userPrivateInfo = new HashMap<>();
        userPrivateInfo.put(FirestoreKeys.Documents.UserProfileDetailed.USER_ID, userId);
        userPrivateInfo.put(FirestoreKeys.Documents.UserProfileDetailed.USER_FULL_NAME, fullName);
        userPrivateInfo.put(FirestoreKeys.Documents.UserProfileDetailed.USER_PROFILE_IMG, profileUrl);
        userPrivateInfo.put(FirestoreKeys.Documents.UserProfileDetailed.USER_DOB, null);
        userPrivateInfo.put(FirestoreKeys.Documents.UserProfileDetailed.USER_COUNTRY_ISO, countryCodeValue);
        userPrivateInfo.put(FirestoreKeys.Documents.UserProfileDetailed.USER_STATUS, null);
        userPrivateInfo.put(FirestoreKeys.Documents.UserProfileDetailed.USER_EMAIL, email);
        userPrivateInfo.put(FirestoreKeys.Documents.UserProfileDetailed.USER_PHONE, phone);
        userPrivateInfo.put(FirestoreKeys.Documents.UserProfileDetailed.USER_FRIENDS, null);
        userPrivateInfo.put(FirestoreKeys.Documents.UserProfileDetailed.BLOCKED_USERS, null);
        userPrivateInfo.put(FirestoreKeys.Documents.UserProfileDetailed.USER_JOINED_EVENTS, null);
        userPrivateInfo.put(FirestoreKeys.Documents.UserProfileDetailed.USER_LIKED_EVENTS, null);
        userPrivateInfo.put(FirestoreKeys.Documents.UserProfileDetailed.USER_HOSTED_EVENTS, null);
        userPrivateInfo.put(FirestoreKeys.Documents.UserProfileDetailed.USER_SOCIAL_LINKS, userSocialLinks);

        Map<String, Object> userPublicInfo = new HashMap<>();
        userPublicInfo.put(FirestoreKeys.Documents.UserProfile.USER_ID, userId);
        userPublicInfo.put(FirestoreKeys.Documents.UserProfile.USER_FULL_NAME, fullName);
        userPublicInfo.put(FirestoreKeys.Documents.UserProfile.USER_PROFILE_IMG, profileUrl);
        userPublicInfo.put(FirestoreKeys.Documents.UserProfile.USER_FRIENDS, null);
        userPublicInfo.put(FirestoreKeys.Documents.UserProfile.USER_DOB, null);
        userPublicInfo.put(FirestoreKeys.Documents.UserProfile.USER_COUNTRY_ISO, countryCodeValue);
        userPublicInfo.put(FirestoreKeys.Documents.UserProfile.USER_STATUS, null);
        userPublicInfo.put(FirestoreKeys.Documents.UserProfile.USER_SOCIAL_LINKS, userSocialLinks);

        Task<Void> task1 = getUserDetailedProfileDocument(userId).set(userPrivateInfo);
        Task<Void> task2 = getUserProfileDocument(userId).set(userPublicInfo);
        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(task1, task2);
        allTasks.addOnSuccessListener(aVoid -> {
            Log.d("DEBUG", "User profile created! UserID: " + userId);
        }).addOnFailureListener(e -> {
            Log.d("DEBUG", "User profile failed to create! UserID: " + userId +
                    "\nError:" + e);
        });
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
        Map<String, Object> event = new HashMap<>();
        event.put(FirestoreKeys.Documents.Event.EVENT_ID, eventModel.getEventId());
        event.put(FirestoreKeys.Documents.Event.EVENT_TITLE, eventModel.getEventTitle());
        event.put(FirestoreKeys.Documents.Event.EVENT_LOCATION, eventModel.getEventLocation());
        event.put(FirestoreKeys.Documents.Event.EVENT_GEOLOCATION, eventModel.getEventGeoLocation());
        event.put(FirestoreKeys.Documents.Event.EVENT_START_TIME, eventModel.getEventStartTime());
        event.put(FirestoreKeys.Documents.Event.EVENT_END_TIME, eventModel.getEventEndTime());
        event.put(FirestoreKeys.Documents.Event.EVENT_ACCESS_TYPE, eventModel.getAccessType());
        event.put(FirestoreKeys.Documents.Event.EVENT_DESCRIPTION, eventModel.getEventDescr());
        event.put(FirestoreKeys.Documents.Event.EVENT_MIN_AGE, eventModel.getEventMinAge());
        event.put(FirestoreKeys.Documents.Event.EVENT_MAX_AGE, eventModel.getEventMaxAge());
        event.put(FirestoreKeys.Documents.Event.EVENT_MAX_PERSON, eventModel.getEventMaxPerson());
        event.put(FirestoreKeys.Documents.Event.EVENT_AVATAR, eventModel.getEventAvatar());
        event.put(FirestoreKeys.Documents.Event.EVENT_THEMES, eventModel.getEventThemes());
        event.put(FirestoreKeys.Documents.Event.EVENT_PARTICIPANTS, 0);
        event.put(FirestoreKeys.Documents.Event.EVENT_HOST_ID, eventModel.getHostId());
        event.put(FirestoreKeys.Documents.Event.EVENT_HOST_NAME, eventModel.getHostName());
        event.put(FirestoreKeys.Documents.Event.EVENT_HOST_PROFILE_IMG, eventModel.getHostProfileImg());
        event.put(FirestoreKeys.Documents.Event.EVENT_CREATE_TS, eventModel.getCreateTS());
        event.put(FirestoreKeys.Documents.Event.EVENT_UPDATE_TS, eventModel.getUpdateTS());
        if (eventModel.getAccessType() == AccessType.PRIVATE) {
            event.put(FirestoreKeys.Documents.Event.EVENT_INVITED_USERS, eventModel.getInvitedUsers());
        }

        Map<String, Object> firstUserMap = new HashMap<>();
        firstUserMap.put(FirestoreKeys.Documents.UserProfile.USER_ID, mainUserProfileModel.getUserId());
        firstUserMap.put(FirestoreKeys.Documents.UserProfile.USER_FULL_NAME, mainUserProfileModel.getUserFullName());
        firstUserMap.put(FirestoreKeys.Documents.UserProfile.USER_PROFILE_IMG, mainUserProfileModel.getUserProfileImg());
        firstUserMap.put(FirestoreKeys.Documents.UserProfile.USER_FRIENDS, mainUserProfileModel.getUserFriends());
        firstUserMap.put(FirestoreKeys.Documents.UserProfile.USER_DOB, mainUserProfileModel.getDateOfBirth());
        firstUserMap.put(FirestoreKeys.Documents.UserProfile.USER_COUNTRY_ISO, mainUserProfileModel.getCountryISO());
        firstUserMap.put(FirestoreKeys.Documents.UserProfile.USER_STATUS, mainUserProfileModel.getUserStatus());
        firstUserMap.put(FirestoreKeys.Documents.UserProfile.USER_SOCIAL_LINKS, mainUserProfileModel.getSocialLinksMap());

        Map<String, Object> profileHostedEventMap = new HashMap<>();
        profileHostedEventMap.put(FirestoreKeys.Documents.UserHostedEvents.EVENT_ID, eventModel.getEventId());
        profileHostedEventMap.put(FirestoreKeys.Documents.UserHostedEvents.EVENT_ACCESS_TYPE, eventModel.getAccessType());

        List<Task<Void>> tasks = new ArrayList<>();
        List<Task<Void>> tasksFriendInvite = new ArrayList<>();
        if (eventModel.getAccessType() == AccessType.PUBLIC || eventModel.getAccessType() == AccessType.SELECTIVE) {
            tasks.add(getPublicEventDocument(eventModel.getEventId()).set(event));
            tasks.add(getPublicEventJoinedUserDocument(eventModel.getEventId(), eventModel.getHostId()).set(firstUserMap));
        } else if (eventModel.getAccessType() == AccessType.PRIVATE) {
            tasks.add(getPrivateEventDocument(eventModel.getEventId()).set(event));
            tasks.add(getPrivateEventJoinedUserDocument(eventModel.getEventId(), eventModel.getHostId()).set(firstUserMap));
        }
        tasks.add(getUserDetailedProfileDocument(eventModel.getHostId()).update(FirestoreKeys.Documents.UserProfileDetailed.USER_HOSTED_EVENTS, FieldValue.arrayUnion(profileHostedEventMap)));

        if (eventModel.getInvitedUsers().size() > 0) {
            for (String userId : eventModel.getInvitedUsers()) {
                Map<String, Object> inviteRequest = new HashMap<>();
                inviteRequest.put(FirestoreKeys.Documents.EventInvite.ACTION_TYPE, "EVENT_INVITE");
                inviteRequest.put(FirestoreKeys.Documents.EventInvite.SENDER_ID, eventModel.getHostId());
                inviteRequest.put(FirestoreKeys.Documents.EventInvite.SENDER_NAME, eventModel.getHostName());
                inviteRequest.put(FirestoreKeys.Documents.EventInvite.SENDER_PROFILE_IMG, eventModel.getHostProfileImg());
                inviteRequest.put(FirestoreKeys.Documents.EventInvite.EVENT_ID, eventModel.getEventId());
                inviteRequest.put(FirestoreKeys.Documents.EventInvite.EVENT_TITLE, eventModel.getEventTitle());
                inviteRequest.put(FirestoreKeys.Documents.EventInvite.EVENT_DESCR, eventModel.getEventDescr());
                inviteRequest.put(FirestoreKeys.Documents.EventInvite.EVENT_LOCATION, eventModel.getEventLocation());
                inviteRequest.put(FirestoreKeys.Documents.EventInvite.EVENT_START_TIME, eventModel.getEventStartTime());
                inviteRequest.put(FirestoreKeys.Documents.EventInvite.EVENT_AVATAR, eventModel.getEventAvatar());
                inviteRequest.put(FirestoreKeys.Documents.EventInvite.EVENT_ACCESS_TYPE, eventModel.getAccessType());
                inviteRequest.put(FirestoreKeys.Documents.EventInvite.RECIPIENT_ID, userId);
                inviteRequest.put(FirestoreKeys.Documents.EventInvite.CREATE_TS, eventModel.getCreateTS());
                tasksFriendInvite.add(getUserProfileIncomeRequestDocument(userId, eventModel.getEventId()).set(inviteRequest));
            }
        }
        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(tasks);
        Task<List<QuerySnapshot>> inviteTasks = Tasks.whenAllSuccess(tasksFriendInvite);
        allTasks.addOnSuccessListener(querySnapshots -> {
            Log.d("DEBUG", "New event created! EventId: " + eventModel.getEventId());
            CustomToastUtil.showSuccessToast(context, context.getResources().getString(R.string.new_event_event_created_notification));
            if (tasksFriendInvite != null && tasksFriendInvite.size() > 0) {
                inviteTasks.addOnSuccessListener(querySnapshots1 -> {
                    instanceCreateEvent = false;
                    CustomToastUtil.showSuccessToast(context, context.getResources().getString(R.string.new_event_event_success_sent_event_notifications));
                    Log.d("DEBUG", "All event invites successfully sent! EventId: " + eventModel.getEventId());
                }).addOnFailureListener(error -> {
                    instanceCreateEvent = false;
                    CustomToastUtil.showFailToast(context, context.getResources().getString(R.string.new_event_event_failed_sent_notifications));
                    Log.d("DEBUG", "Failed to sent event invites! EventId: " + error.toString());
                });
            } else {
                instanceCreateEvent = false;
            }
        }).addOnFailureListener(e -> {
            instanceCreateEvent = false;
            Log.d("DEBUG", "Failed to create new event!" + e.toString());
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
        AccessType accessType = event.getAccessType();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put(FirestoreKeys.Documents.UserProfile.USER_ID, userId);
        userMap.put(FirestoreKeys.Documents.UserProfile.USER_FULL_NAME, mainUserProfileModel.getUserFullName());
        userMap.put(FirestoreKeys.Documents.UserProfile.USER_PROFILE_IMG, mainUserProfileModel.getUserProfileImg());
        userMap.put(FirestoreKeys.Documents.UserProfile.USER_FRIENDS, mainUserProfileModel.getUserFriends());
        userMap.put(FirestoreKeys.Documents.UserProfile.USER_DOB, mainUserProfileModel.getDateOfBirth());
        userMap.put(FirestoreKeys.Documents.UserProfile.USER_COUNTRY_ISO, mainUserProfileModel.getCountryISO());
        userMap.put(FirestoreKeys.Documents.UserProfile.USER_STATUS, mainUserProfileModel.getUserStatus());
        userMap.put(FirestoreKeys.Documents.UserProfile.USER_SOCIAL_LINKS, mainUserProfileModel.getSocialLinksMap());

        Map<String, Object> profileJoinedEventMap = new HashMap<>();
        profileJoinedEventMap.put(FirestoreKeys.Documents.UserJoinedEvents.EVENT_ID, eventId);
        profileJoinedEventMap.put(FirestoreKeys.Documents.UserJoinedEvents.EVENT_ACCESS_TYPE, event.getAccessType());

        Task<Void> task1 = null, task2 = null;
        if (accessType == AccessType.PUBLIC || accessType == AccessType.SELECTIVE) {
            task1 = getPublicEventJoinedUserDocument(eventId, userId).set(userMap);
        } else if (accessType == AccessType.PRIVATE) {
            task1 = getPrivateEventJoinedUserDocument(eventId, userId).set(userMap);
        }
        task2 = getUserDetailedProfileDocument(userId).update(FirestoreKeys.Documents.UserProfileDetailed.USER_JOINED_EVENTS, FieldValue.arrayUnion(profileJoinedEventMap));
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

        Map<String, Object> profileJoinedEventMap = new HashMap<>();
        profileJoinedEventMap.put(FirestoreKeys.Documents.UserJoinedEvents.EVENT_ID, eventId);
        profileJoinedEventMap.put(FirestoreKeys.Documents.UserJoinedEvents.EVENT_ACCESS_TYPE, event.getAccessType());

        Task<Void> task1 = null, task2 = null;
        if (accessType == AccessType.PUBLIC || accessType == AccessType.SELECTIVE) {
            task1 = getPublicEventJoinedUserDocument(eventId, userId).delete();
        } else if (accessType == AccessType.PRIVATE) {
            task1 = getPrivateEventJoinedUserDocument(eventId, userId).delete();
        }
        task2 = getUserDetailedProfileDocument(userId).update(FirestoreKeys.Documents.UserProfileDetailed.USER_JOINED_EVENTS, FieldValue.arrayRemove(profileJoinedEventMap));
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
        getUserDetailedProfileDocument(userId).get().addOnSuccessListener(documentSnapshot -> {
            if (documentSnapshot.getData() != null) {
                getUserProfileCallback.onResponse(FirestoreToModelConverter.convertUserProfileDocumentToModel(documentSnapshot.getData()));
            } else {
                getUserProfileCallback.onResponse(null);
            }
        }).addOnFailureListener(e -> getUserProfileCallback.onResponse(null));
    }

    public void getMultipleUsersProfile(GetEventParticipantsCallback getEventParticipantsCallback,
                                        List<String> multipleUserId) {
        List<CommonUserModel> usersList = new ArrayList<>();
        if (multipleUserId == null || multipleUserId.isEmpty()) {
            getEventParticipantsCallback.onResponse(usersList);
        } else {
            List<Task<DocumentSnapshot>> tasks = new ArrayList<>();
            for (String userId : multipleUserId) {
                tasks.add(getUserProfileDocument(userId).get());
            }
            Task<List<DocumentSnapshot>> allTasks = Tasks.whenAllSuccess(tasks);
            allTasks.addOnSuccessListener(documentSnapshots -> {
                for (DocumentSnapshot document : documentSnapshots) {
                    if (document.getData() != null) {
                        usersList.add(FirestoreToModelConverter.convertCommonUserProfileDocumentToModel(document.getData()));
                    }
                }
                getEventParticipantsCallback.onResponse(usersList);
            }).addOnFailureListener(e -> getEventParticipantsCallback.onResponse(usersList));
        }
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

//    public MutableLiveData<List<CommonUserModel>> getFriendListMutableLiveData(List<String> friendsIds) {
//        if (friendsIds != null && friendsIds.size() > 0) {
//            friendListListener = getUserProfilesCollection()
//                    .whereIn(FirestoreKeys.Documents.UserProfile.USER_ID, friendsIds)
//                    .addSnapshotListener((value, error) -> {
//                        Log.d("DEBUG", "Added friend list snapshot listener");
//                        List<CommonUserModel> friendList = new ArrayList<>();
//                        for (QueryDocumentSnapshot document : value) {
//                            if (document != null) {
//                                CommonUserModel userProfile = FirestoreToModelConverter.convertCommonUserProfileDocumentToModel(document.getData());
//                                friendList.add(userProfile);
//                            }
//                        }
//                        friendListMutableLiveData.postValue(friendList);
//                    });
//        } else {
//            friendListMutableLiveData.postValue(null);
//        }
//        return friendListMutableLiveData;
//    }

    public MutableLiveData<MainUserProfileModel> getMainUserProfileMutableLiveData(String mainUserId) {
        friendListListener = getUserProfileInfoDetailedCollection(mainUserId)
                .addSnapshotListener((value, error) -> {
                    MainUserProfileModel mainUserProfileModel = null;
                    if (value != null && !value.getDocuments().isEmpty() && value.getDocuments().get(0) != null && !value.getDocuments().get(0).getData().isEmpty()) {
                        mainUserProfileModel = FirestoreToModelConverter.convertUserProfileDocumentToModel(value.getDocuments().get(0).getData());
                    }
                    mainUserProfileMutableLiveData.postValue(mainUserProfileModel);
                });
        return mainUserProfileMutableLiveData;
    }

    public void removeEventListListener() {
        if (eventListListener != null) {
            Log.d("DEBUG", "Removed public event list snapshot listener");
            eventListListener.remove();
        }
    }

    public void removeNotificationsListener() {
        if (notificationsListener != null) {
            Log.d("DEBUG", "Removed income notifications listener");
            notificationsListener.remove();
        }
    }

    public void removeMainUserProfileListener() {
        if (mainUserProfileListener != null) {
            Log.d("DEBUG", "Removed main user profile listener");
            mainUserProfileListener.remove();
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

    public void addToUserLikedEvents(SetUserInteractEventCallback setUserInteractEventCallback, String userId, EventModel event) {
        if (instanceLike) {
            return;
        }
        instanceLike = true;

        Map<String, Object> profileLikedEventMap = new HashMap<>();
        profileLikedEventMap.put(FirestoreKeys.Documents.UserLikedEvents.EVENT_ID, event.getEventId());
        profileLikedEventMap.put(FirestoreKeys.Documents.UserLikedEvents.EVENT_ACCESS_TYPE, event.getAccessType());

        Task<Void> task1 = getUserDetailedProfileDocument(userId).update(FirestoreKeys.Documents.UserProfileDetailed.USER_LIKED_EVENTS, FieldValue.arrayUnion(profileLikedEventMap));
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
        requestBody.put(FirestoreKeys.Documents.FriendInvite.ACTION_TYPE, "ADD_FRIEND");
        requestBody.put(FirestoreKeys.Documents.FriendInvite.SENDER_ID, currentUserProfile.getUserId());
        requestBody.put(FirestoreKeys.Documents.FriendInvite.SENDER_NAME, currentUserProfile.getUserFullName());
        requestBody.put(FirestoreKeys.Documents.FriendInvite.SENDER_PROFILE_IMG, currentUserProfile.getUserProfileImg());
        requestBody.put(FirestoreKeys.Documents.FriendInvite.RECIPIENT_ID, anotherUserId);
        requestBody.put(FirestoreKeys.Documents.FriendInvite.CREATE_TS, createTS);

        Task<Void> task = getUserProfileOutcomeRequestDocument(currentUserProfile.getUserId(), anotherUserId).set(requestBody);
        task.addOnSuccessListener(querySnapshots -> {
            instanceFriendRequest = false;
            setUserInteractEventCallback.onResponse(true);
        }).addOnFailureListener(e -> {
            instanceFriendRequest = false;
            setUserInteractEventCallback.onResponse(false);
        });
    }

    public void removeFriend(SetUserInteractEventCallback setUserInteractEventCallback,
                             String userId, String anotherUserId) {
        if (instanceFriendRequest) {
            return;
        }
        instanceFriendRequest = true;
        Timestamp createTS = new Timestamp(new Date());
        Map<String, Object> requestBody = new HashMap<>();
        requestBody.put(FirestoreKeys.Documents.FriendInvite.SENDER_ID, userId);
        requestBody.put(FirestoreKeys.Documents.FriendInvite.RECIPIENT_ID, anotherUserId);
        requestBody.put(FirestoreKeys.Documents.FriendInvite.ACTION_TYPE, "REMOVE_FRIEND");
        requestBody.put(FirestoreKeys.Documents.FriendInvite.CREATE_TS, createTS);
        Task<Void> task1 = getUserDetailedProfileDocument(userId).update(FirestoreKeys.Documents.UserProfileDetailed.USER_FRIENDS, FieldValue.arrayRemove(anotherUserId));
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

    public void setUserToBlocked(SetUserInteractEventCallback setUserInteractEventCallback,
                                 MainUserProfileModel mainUserProfileModel, String userBlockedId) {
        if (instanceBlockUser) {
            return;
        }
        instanceBlockUser = true;
        List<Task<Void>> tasks = new ArrayList<>();
        tasks.add(getUserDetailedProfileDocument(mainUserProfileModel.getUserId()).update(FirestoreKeys.Documents.UserProfileDetailed.BLOCKED_USERS,
                FieldValue.arrayUnion(userBlockedId)));
        if (mainUserProfileModel.getUserFriends().contains(userBlockedId)) {
            Timestamp createTS = new Timestamp(new Date());
            Map<String, Object> requestBody = new HashMap<>();
            requestBody.put(FirestoreKeys.Documents.FriendInvite.SENDER_ID, mainUserProfileModel.getUserId());
            requestBody.put(FirestoreKeys.Documents.FriendInvite.RECIPIENT_ID, userBlockedId);
            requestBody.put(FirestoreKeys.Documents.FriendInvite.ACTION_TYPE, "REMOVE_FRIEND");
            requestBody.put(FirestoreKeys.Documents.FriendInvite.CREATE_TS, createTS);

            tasks.add(getUserDetailedProfileDocument(mainUserProfileModel.getUserId())
                    .update(FirestoreKeys.Documents.UserProfileDetailed.USER_FRIENDS, FieldValue.arrayRemove(userBlockedId)));
            tasks.add(getUserProfileOutcomeRequestDocument(mainUserProfileModel.getUserId(), userBlockedId).set(requestBody));
        }
        Task<List<QuerySnapshot>> allTasks = Tasks.whenAllSuccess(tasks);
        allTasks.addOnSuccessListener(querySnapshots -> {
            instanceBlockUser = false;
            setUserInteractEventCallback.onResponse(true);
        }).addOnFailureListener(e -> {
            instanceBlockUser = false;
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
        Task<Void> task1 = getUserDetailedProfileDocument(recipientId).update(FirestoreKeys.Documents.UserProfileDetailed.USER_FRIENDS, FieldValue.arrayUnion(senderId));
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
        AccessType accessType = inviteToEventModel.getAccessType();

        Map<String, Object> userMap = new HashMap<>();
        userMap.put(FirestoreKeys.Documents.UserProfile.USER_ID, userId);
        userMap.put(FirestoreKeys.Documents.UserProfile.USER_FULL_NAME, userFullName);
        userMap.put(FirestoreKeys.Documents.UserProfile.USER_PROFILE_IMG, userProfileImg);
        userMap.put(FirestoreKeys.Documents.UserProfile.USER_SOCIAL_LINKS,
                mainUserProfileModel.getSocialLinksMap());

        Map<String, Object> profileJoinedEventMap = new HashMap<>();
        profileJoinedEventMap.put(FirestoreKeys.Documents.UserJoinedEvents.EVENT_ID, eventId);
        profileJoinedEventMap.put(FirestoreKeys.Documents.UserJoinedEvents.EVENT_ACCESS_TYPE, inviteToEventModel.getAccessType());

        Task<Void> task1 = null, task2 = null, task3 = null;
        if (accessType == AccessType.PUBLIC || accessType == AccessType.SELECTIVE) {
            task1 = getPublicEventJoinedUserDocument(eventId, userId).set(userMap);
        } else if (accessType == AccessType.PRIVATE) {
            task1 = getPrivateEventJoinedUserDocument(eventId, userId).set(userMap);
        }
        task2 = getUserProfileDocument(userId).update(FirestoreKeys.Documents.UserProfileDetailed.USER_JOINED_EVENTS, FieldValue.arrayUnion(profileJoinedEventMap));
        task3 = getUserProfileIncomeRequestDocument(userId, eventId).delete();

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

    public void removeUserFromBlocked(SetUserInteractEventCallback setUserInteractEventCallback, String userId, String userBlockedId) {
        if (instanceBlockUser) {
            return;
        }
        instanceBlockUser = true;
        Task<Void> task = getUserDetailedProfileDocument(userId).update(FirestoreKeys.Documents.UserProfileDetailed.BLOCKED_USERS,
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
