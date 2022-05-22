package com.sunsetrebel.catsy.repositories;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;
import com.sunsetrebel.catsy.enums.AccessType;
import com.sunsetrebel.catsy.enums.EventThemes;
import com.sunsetrebel.catsy.models.CommonUserModel;
import com.sunsetrebel.catsy.models.EventModel;
import com.sunsetrebel.catsy.models.InviteToEventModel;
import com.sunsetrebel.catsy.models.InviteToFriendsListModel;
import com.sunsetrebel.catsy.models.MainUserProfileModel;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FirestoreToModelConverter {
    public static EventModel convertEventDocumentToModel(Map<String, Object> map) {
        return new EventModel(map.get(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_HOST_ID).toString(),
                map.get(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_HOST_NAME).toString(),
                convertObjectToString(map.get(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_HOST_PROFILE_IMG)),
                map.get(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_ID).toString(),
                map.get(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_TITLE).toString(),
                map.get(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_LOCATION).toString(),
                convertMapToLatLng((HashMap<String, Number>) map.get(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_GEOLOCATION)),
                ((Timestamp) map.get(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_START_TIME)).toDate(),
                ((Timestamp) map.get(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_END_TIME)).toDate(),
                AccessType.valueOf(map.get(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_ACCESS_TYPE).toString()),
                map.get(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_DESCRIPTION).toString(),
                convertObjectToInteger(map.get(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_MIN_AGE)),
                convertObjectToInteger(map.get(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_MAX_AGE)),
                convertObjectToInteger(map.get(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_PARTICIPANTS)),
                null, convertObjectToInteger(map.get(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_MAX_PERSON)),
                convertObjectToString(map.get(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_AVATAR)),
                convertEventThemes((ArrayList<Object[]>) map.get(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_THEMES)),
                ((Timestamp) map.get(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_CREATE_TS)),
                ((Timestamp) map.get(FirestoreKeys.Documents.Event.DOCUMENT_EVENT_UPDATE_TS)), null);
    }

    public static MainUserProfileModel convertUserProfileDocumentToModel(Map<String, Object> map) {
        return new MainUserProfileModel(map.get(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_ID).toString(),
                convertObjectToString(map.get(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_EMAIL)),
                convertObjectToString(map.get(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_PHONE)),
                map.get(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_FULL_NAME).toString(),
                convertObjectToString(map.get(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_PROFILE_IMG)),
                convertToStringList((ArrayList<Object[]>) map.get(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_JOINED_PUBLIC_EVENTS)),
                convertToStringList((ArrayList<Object[]>) map.get(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_JOINED_PRIVATE_EVENTS)),
                convertToStringList((ArrayList<Object[]>) map.get(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_HOSTED_PUBLIC_EVENTS)),
                convertToStringList((ArrayList<Object[]>) map.get(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_HOSTED_PRIVATE_EVENTS)),
                convertToStringList((ArrayList<Object[]>) map.get(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_LIKED_EVENTS)),
                convertObjectToString(map.get(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_LINK_TELEGRAM)),
                convertObjectToString(map.get(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_LINK_TIKTOK)),
                convertObjectToString(map.get(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_LINK_INSTAGRAM)),
                convertObjectToString(map.get(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_LINK_FACEBOOK)),
                convertToStringList((ArrayList<Object[]>) map.get(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_FRIENDS)),
                convertToStringList((ArrayList<Object[]>) map.get(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_BLOCKED_USERS)));
    }

    public static CommonUserModel convertCommonUserProfileDocumentToModel(Map<String, Object> map) {
        return new CommonUserModel(map.get(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_ID).toString(),
                map.get(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_FULL_NAME).toString(),
                convertObjectToString(map.get(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_PROFILE_IMG)),
                convertToStringList((ArrayList<Object[]>) map.get(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_JOINED_PUBLIC_EVENTS)),
                convertToStringList((ArrayList<Object[]>) map.get(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_HOSTED_PUBLIC_EVENTS)),
                convertToStringList((ArrayList<Object[]>) map.get(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_LIKED_EVENTS)),
                convertObjectToString(map.get(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_LINK_TELEGRAM)),
                convertObjectToString(map.get(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_LINK_TIKTOK)),
                convertObjectToString(map.get(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_LINK_INSTAGRAM)),
                convertObjectToString(map.get(FirestoreKeys.Documents.UserProfile.DOCUMENT_USER_LINK_FACEBOOK)));
    }

    public static Object convertNotificationDocumentToModel(Map<String, Object> map) {
        if (map.get(FirestoreKeys.Documents.FriendInvite.DOCUMENT_FRIEND_REQUEST_ACTION).toString().equals("ADD_FRIEND")) {
            return new InviteToFriendsListModel(map.get(FirestoreKeys.Documents.FriendInvite.DOCUMENT_FRIEND_REQUEST_ACTION).toString(),
                    map.get(FirestoreKeys.Documents.FriendInvite.DOCUMENT_FRIEND_REQUEST_SENDER_ID).toString(),
                    map.get(FirestoreKeys.Documents.FriendInvite.DOCUMENT_FRIEND_REQUEST_SENDER_NAME).toString(),
                    convertObjectToString(map.get(FirestoreKeys.Documents.FriendInvite.DOCUMENT_FRIEND_REQUEST_SENDER_PROFILE_IMG)),
                    map.get(FirestoreKeys.Documents.FriendInvite.DOCUMENT_FRIEND_REQUEST_RECIPIENT).toString(),
                    ((Timestamp) map.get(FirestoreKeys.Documents.FriendInvite.DOCUMENT_FRIEND_REQUEST_CREATE_TS)));
        } else if (map.get(FirestoreKeys.Documents.EventInvite.DOCUMENT_INVITE_REQUEST_ACTION).toString().equals("EVENT_INVITE")) {
            return new InviteToEventModel(map.get(FirestoreKeys.Documents.EventInvite.DOCUMENT_INVITE_REQUEST_ACTION).toString(),
                    map.get(FirestoreKeys.Documents.EventInvite.DOCUMENT_INVITE_REQUEST_SENDER_ID).toString(),
                    map.get(FirestoreKeys.Documents.EventInvite.DOCUMENT_INVITE_REQUEST_SENDER_NAME).toString(),
                    convertObjectToString(map.get(FirestoreKeys.Documents.EventInvite.DOCUMENT_INVITE_REQUEST_SENDER_PROFILE_IMG)),
                    map.get(FirestoreKeys.Documents.EventInvite.DOCUMENT_INVITE_REQUEST_EVENT_ID).toString(),
                    map.get(FirestoreKeys.Documents.EventInvite.DOCUMENT_INVITE_REQUEST_EVENT_TITLE).toString(),
                    map.get(FirestoreKeys.Documents.EventInvite.DOCUMENT_INVITE_REQUEST_EVENT_DESCR).toString(),
                    map.get(FirestoreKeys.Documents.EventInvite.DOCUMENT_INVITE_REQUEST_EVENT_LOCATION).toString(),
                    ((Timestamp) map.get(FirestoreKeys.Documents.EventInvite.DOCUMENT_INVITE_REQUEST_EVENT_START_TIME)).toDate(),
                    convertObjectToString(map.get(FirestoreKeys.Documents.EventInvite.DOCUMENT_INVITE_REQUEST_EVENT_AVATAR)),
                    AccessType.valueOf(map.get(FirestoreKeys.Documents.EventInvite.DOCUMENT_INVITE_REQUEST_EVENT_ACCESS_TYPE).toString()),
                    map.get(FirestoreKeys.Documents.EventInvite.DOCUMENT_INVITE_REQUEST_RECIPIENT).toString(),
                    ((Timestamp) map.get(FirestoreKeys.Documents.EventInvite.DOCUMENT_INVITE_REQUEST_CREATE_TS)));
        } else {
            return null;
        }
    }

    private static String convertObjectToString(Object object) {
        if (object != null) {
            return object.toString();
        } else {
            return null;
        }
    }

    private static Integer convertObjectToInteger(Object object) {
        if (object != null) {
            return ((Number) object).intValue();
        } else {
            return null;
        }
    }

    private static LatLng convertMapToLatLng(HashMap <String, Number> mapLatLng) {
        return new LatLng(mapLatLng.get("latitude").doubleValue(), mapLatLng.get("longitude").doubleValue());
    }

    private static List<EventThemes> convertEventThemes(ArrayList<Object[]> eventObjects) {
        List<EventThemes> convertedEventThemeServices = new ArrayList<>();
        if (eventObjects != null) {
            for (Object object : eventObjects) {
                convertedEventThemeServices.add(EventThemes.valueOf(object.toString()));
            }
        }
        return convertedEventThemeServices;
    }

    private static List<String> convertToStringList(ArrayList<Object[]> objectList) {
        List<String> stringList = new ArrayList<>();
        if (objectList != null) {
            for (Object object : objectList) {
                stringList.add(object.toString());
            }
        }
        return stringList;
    }
}
