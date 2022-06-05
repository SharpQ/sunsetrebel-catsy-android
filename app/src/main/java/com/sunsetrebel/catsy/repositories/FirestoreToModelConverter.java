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
        return new EventModel(map.get(FirestoreKeys.Documents.Event.EVENT_HOST_ID).toString(),
                map.get(FirestoreKeys.Documents.Event.EVENT_HOST_NAME).toString(),
                convertObjectToString(map.get(FirestoreKeys.Documents.Event.EVENT_HOST_PROFILE_IMG)),
                map.get(FirestoreKeys.Documents.Event.EVENT_ID).toString(),
                map.get(FirestoreKeys.Documents.Event.EVENT_TITLE).toString(),
                map.get(FirestoreKeys.Documents.Event.EVENT_LOCATION).toString(),
                convertMapToLatLng((HashMap<String, Number>) map.get(FirestoreKeys.Documents.Event.EVENT_GEOLOCATION)),
                ((Timestamp) map.get(FirestoreKeys.Documents.Event.EVENT_START_TIME)).toDate(),
                ((Timestamp) map.get(FirestoreKeys.Documents.Event.EVENT_END_TIME)).toDate(),
                AccessType.valueOf(map.get(FirestoreKeys.Documents.Event.EVENT_ACCESS_TYPE).toString()),
                map.get(FirestoreKeys.Documents.Event.EVENT_DESCRIPTION).toString(),
                convertObjectToInteger(map.get(FirestoreKeys.Documents.Event.EVENT_MIN_AGE)),
                convertObjectToInteger(map.get(FirestoreKeys.Documents.Event.EVENT_MAX_AGE)),
                convertObjectToInteger(map.get(FirestoreKeys.Documents.Event.EVENT_PARTICIPANTS)),
                null, convertObjectToInteger(map.get(FirestoreKeys.Documents.Event.EVENT_MAX_PERSON)),
                convertObjectToString(map.get(FirestoreKeys.Documents.Event.EVENT_AVATAR)),
                convertEventThemes((ArrayList<Object[]>) map.get(FirestoreKeys.Documents.Event.EVENT_THEMES)),
                ((Timestamp) map.get(FirestoreKeys.Documents.Event.EVENT_CREATE_TS)),
                ((Timestamp) map.get(FirestoreKeys.Documents.Event.EVENT_UPDATE_TS)), null);
    }

    public static MainUserProfileModel convertUserProfileDocumentToModel(Map<String, Object> map) {
        return new MainUserProfileModel(map.get(FirestoreKeys.Documents.UserProfileDetailed.USER_ID).toString(),
                convertObjectToString(map.get(FirestoreKeys.Documents.UserProfileDetailed.USER_EMAIL)),
                convertObjectToString(map.get(FirestoreKeys.Documents.UserProfileDetailed.USER_PHONE)),
                map.get(FirestoreKeys.Documents.UserProfileDetailed.USER_FULL_NAME).toString(),
                convertObjectToString(map.get(FirestoreKeys.Documents.UserProfileDetailed.USER_PROFILE_IMG)),
                convertToMapList((ArrayList<Object[]>) map.get(FirestoreKeys.Documents.UserProfileDetailed.USER_JOINED_EVENTS)),
                convertToMapList((ArrayList<Object[]>) map.get(FirestoreKeys.Documents.UserProfileDetailed.USER_HOSTED_EVENTS)),
                convertToMapList((ArrayList<Object[]>) map.get(FirestoreKeys.Documents.UserProfileDetailed.USER_LIKED_EVENTS)),
                (Map<String, Object>) map.get(FirestoreKeys.Documents.UserProfileDetailed.USER_SOCIAL_LINKS),
                convertToStringList((ArrayList<Object[]>) map.get(FirestoreKeys.Documents.UserProfileDetailed.USER_FRIENDS)),
                convertToStringList((ArrayList<Object[]>) map.get(FirestoreKeys.Documents.UserProfileDetailed.BLOCKED_USERS)),
                convertObjectToString(map.get(FirestoreKeys.Documents.UserProfileDetailed.USER_STATUS)),
                convertObjectToString(map.get(FirestoreKeys.Documents.UserProfileDetailed.USER_DOB)),
                convertObjectToString(map.get(FirestoreKeys.Documents.UserProfileDetailed.USER_COUNTRY_ISO)));
    }

    public static CommonUserModel convertCommonUserProfileDocumentToModel(Map<String, Object> map) {
        return new CommonUserModel(map.get(FirestoreKeys.Documents.UserProfile.USER_ID).toString(),
                map.get(FirestoreKeys.Documents.UserProfile.USER_FULL_NAME).toString(),
                convertObjectToString(map.get(FirestoreKeys.Documents.UserProfile.USER_PROFILE_IMG)),
                (Map<String, Object>) map.get(FirestoreKeys.Documents.UserProfile.USER_SOCIAL_LINKS),
                convertObjectToString(map.get(FirestoreKeys.Documents.UserProfileDetailed.USER_STATUS)),
                convertObjectToString(map.get(FirestoreKeys.Documents.UserProfileDetailed.USER_DOB)),
                convertObjectToString(map.get(FirestoreKeys.Documents.UserProfileDetailed.USER_COUNTRY_ISO)));
    }

    public static Object convertNotificationDocumentToModel(Map<String, Object> map) {
        if (map.get(FirestoreKeys.Documents.FriendInvite.ACTION_TYPE).toString().equals("ADD_FRIEND")) {
            return new InviteToFriendsListModel(map.get(FirestoreKeys.Documents.FriendInvite.ACTION_TYPE).toString(),
                    map.get(FirestoreKeys.Documents.FriendInvite.SENDER_ID).toString(),
                    map.get(FirestoreKeys.Documents.FriendInvite.SENDER_NAME).toString(),
                    convertObjectToString(map.get(FirestoreKeys.Documents.FriendInvite.SENDER_PROFILE_IMG)),
                    map.get(FirestoreKeys.Documents.FriendInvite.RECIPIENT_ID).toString(),
                    ((Timestamp) map.get(FirestoreKeys.Documents.FriendInvite.CREATE_TS)));
        } else if (map.get(FirestoreKeys.Documents.EventInvite.ACTION_TYPE).toString().equals("EVENT_INVITE")) {
            return new InviteToEventModel(map.get(FirestoreKeys.Documents.EventInvite.ACTION_TYPE).toString(),
                    map.get(FirestoreKeys.Documents.EventInvite.SENDER_ID).toString(),
                    map.get(FirestoreKeys.Documents.EventInvite.SENDER_NAME).toString(),
                    convertObjectToString(map.get(FirestoreKeys.Documents.EventInvite.SENDER_PROFILE_IMG)),
                    map.get(FirestoreKeys.Documents.EventInvite.EVENT_ID).toString(),
                    map.get(FirestoreKeys.Documents.EventInvite.EVENT_TITLE).toString(),
                    map.get(FirestoreKeys.Documents.EventInvite.EVENT_DESCR).toString(),
                    map.get(FirestoreKeys.Documents.EventInvite.EVENT_LOCATION).toString(),
                    ((Timestamp) map.get(FirestoreKeys.Documents.EventInvite.EVENT_START_TIME)).toDate(),
                    convertObjectToString(map.get(FirestoreKeys.Documents.EventInvite.EVENT_AVATAR)),
                    AccessType.valueOf(map.get(FirestoreKeys.Documents.EventInvite.EVENT_ACCESS_TYPE).toString()),
                    map.get(FirestoreKeys.Documents.EventInvite.RECIPIENT_ID).toString(),
                    ((Timestamp) map.get(FirestoreKeys.Documents.EventInvite.CREATE_TS)));
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

    private static List<Map<String, Object>> convertToMapList(ArrayList<Object[]> objectList) {
        List<Map<String, Object>> mapList = new ArrayList<>();
        if (objectList != null) {
            for (Object object : objectList) {
                mapList.add((Map<String, Object>) object);
            }
        }
        return mapList;
    }
}
