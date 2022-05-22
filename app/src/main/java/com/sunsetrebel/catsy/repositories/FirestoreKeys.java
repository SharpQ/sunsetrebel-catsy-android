package com.sunsetrebel.catsy.repositories;

public class FirestoreKeys {
    static class Collections {
        //COLLECTIONS NAMES
        public static final String COLLECTION_USER_PROFILES = "userProfiles";
        public static final String COLLECTION_PUBLIC_EVENTS = "publicEvents";
        public static final String COLLECTION_EVENT_USERS_JOINED = "usersJoined";
        public static final String COLLECTION_PRIVATE_EVENTS = "privateEvents";
        public static final String COLLECTION_USER_OUTCOME_REQUESTS = "outcomeRequests";
        public static final String COLLECTION_USER_INCOME_REQUESTS = "incomeRequests";
    }

    static class Documents {
        //DOCUMENTS PROPERTIES NAMES
        static class UserProfile {
            //PROPERTIES USER PROFILE
            public static final String DOCUMENT_USER_ID = "userId";
            public static final String DOCUMENT_USER_FULL_NAME = "userFullName";
            public static final String DOCUMENT_USER_EMAIL = "userEmail";
            public static final String DOCUMENT_USER_PHONE = "userPhone";
            public static final String DOCUMENT_USER_PROFILE_IMG = "userProfileImg";
            public static final String DOCUMENT_USER_JOINED_PUBLIC_EVENTS = "joinedPublicEvents";
            public static final String DOCUMENT_USER_JOINED_PRIVATE_EVENTS = "joinedPrivateEvents";
            public static final String DOCUMENT_USER_LIKED_EVENTS = "likedEvents";
            public static final String DOCUMENT_USER_HOSTED_PUBLIC_EVENTS = "hostedPublicEvents";
            public static final String DOCUMENT_USER_HOSTED_PRIVATE_EVENTS = "hostedPrivateEvents";
            public static final String DOCUMENT_USER_FRIENDS = "userFriends";
            public static final String DOCUMENT_USER_BLOCKED_USERS = "blockedUsers";
            public static final String DOCUMENT_USER_LINK_TELEGRAM = "userLinkTelegram";
            public static final String DOCUMENT_USER_LINK_TIKTOK = "userLinkTikTok";
            public static final String DOCUMENT_USER_LINK_INSTAGRAM = "userLinkInstagram";
            public static final String DOCUMENT_USER_LINK_FACEBOOK = "userLinkFacebook";
        }

        static class Event {
            //PROPERTIES EVENT
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
            public static final String DOCUMENT_EVENT_INVITED_USERS = "invitedUsers";
        }

        static class FriendInvite {
            //PROPERTIES FRIEND REQUEST
            public static final String DOCUMENT_FRIEND_REQUEST_ACTION = "action";
            public static final String DOCUMENT_FRIEND_REQUEST_SENDER_ID = "senderId";
            public static final String DOCUMENT_FRIEND_REQUEST_SENDER_NAME = "senderName";
            public static final String DOCUMENT_FRIEND_REQUEST_SENDER_PROFILE_IMG = "senderProfileImg";
            public static final String DOCUMENT_FRIEND_REQUEST_RECIPIENT = "recipientId";
            public static final String DOCUMENT_FRIEND_REQUEST_CREATE_TS = "createTS";
        }

        static class EventInvite {
            //PROPERTIES INVITE EVENT
            public static final String DOCUMENT_INVITE_REQUEST_ACTION = "action";
            public static final String DOCUMENT_INVITE_REQUEST_EVENT_ID = "eventId";
            public static final String DOCUMENT_INVITE_REQUEST_EVENT_TITLE = "eventTitle";
            public static final String DOCUMENT_INVITE_REQUEST_EVENT_DESCR = "eventDescription";
            public static final String DOCUMENT_INVITE_REQUEST_EVENT_LOCATION = "eventLocation";
            public static final String DOCUMENT_INVITE_REQUEST_EVENT_START_TIME = "eventStartTime";
            public static final String DOCUMENT_INVITE_REQUEST_EVENT_AVATAR = "eventAvatar";
            public static final String DOCUMENT_INVITE_REQUEST_EVENT_ACCESS_TYPE = "eventAccessType";
            public static final String DOCUMENT_INVITE_REQUEST_SENDER_ID = "senderId";
            public static final String DOCUMENT_INVITE_REQUEST_SENDER_NAME = "senderName";
            public static final String DOCUMENT_INVITE_REQUEST_SENDER_PROFILE_IMG = "senderProfileImg";
            public static final String DOCUMENT_INVITE_REQUEST_RECIPIENT = "recipientId";
            public static final String DOCUMENT_INVITE_REQUEST_CREATE_TS = "createTS";
        }
    }
}
