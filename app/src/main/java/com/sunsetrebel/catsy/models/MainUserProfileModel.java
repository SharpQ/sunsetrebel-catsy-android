package com.sunsetrebel.catsy.models;


import com.sunsetrebel.catsy.enums.AccessType;
import com.sunsetrebel.catsy.repositories.FirestoreKeys;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


public class MainUserProfileModel {
    private String userId;
    private String userEmail;
    private String userPhone;
    private String userFullName;
    private String userProfileImg;
    private List<Map<String, Object>> joinedEvents;
    private List<Map<String, Object>> hostedEvents;
    private List<Map<String, Object>> likedEvents;
    private Map<String, Object> socialLinks;
    private List<String> userFriends;
    private List<String> blockedUsers;
    private String userStatus;
    private String dateOfBirth;
    private String countryISO;

    public MainUserProfileModel() {
    }

    public MainUserProfileModel(String userId, String userEmail, String userPhone,
                                String userFullName, String userProfileImg,
                                List<Map<String, Object>> joinedEvents,
                                List<Map<String, Object>> hostedEvents,
                                List<Map<String, Object>> likedEvents,
                                Map<String, Object> socialLinks,
                                List<String> userFriends, List<String> blockedUsers,
                                String userStatus, String dateOfBirth, String countryISO) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.userFullName = userFullName;
        this.userProfileImg = userProfileImg;
        this.joinedEvents = joinedEvents;
        this.hostedEvents = hostedEvents;
        this.likedEvents = likedEvents;
        this.socialLinks = socialLinks;
        this.userFriends = userFriends;
        this.blockedUsers = blockedUsers;
        this.userStatus = userStatus;
        this.dateOfBirth = dateOfBirth;
        this.countryISO = countryISO;
    }

    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; }

    public String getUserEmail() { return userEmail; }

    public void setUserEmail(String userEmail) { this.userEmail = userEmail; }

    public String getUserPhone() { return userPhone; }

    public void setUserPhone(String userPhone) { this.userPhone = userPhone; }

    public String getUserFullName() { return userFullName; }

    public void setUserFullName(String userFullName) { this.userFullName = userFullName; }

    public String getUserProfileImg() { return userProfileImg; }

    public void setUserProfileImg(String userProfileImg) { this.userProfileImg = userProfileImg; }

    public List<String> getJoinedEvents(AccessType accessType) {
        if (joinedEvents != null && joinedEvents.size() > 0) {
            List<String> joinedEventsList = new ArrayList<>();
            for (Map<String, Object> eventMap : joinedEvents) {
                AccessType eventAccessType = AccessType.valueOf(eventMap.get(FirestoreKeys.Documents.UserJoinedEvents.EVENT_ACCESS_TYPE).toString());
                if ((eventAccessType == AccessType.PUBLIC || eventAccessType == AccessType.SELECTIVE &&
                        (accessType == AccessType.PUBLIC || eventAccessType == AccessType.SELECTIVE)) ||
                        (accessType == AccessType.PRIVATE && eventAccessType == AccessType.PRIVATE)) {
                    joinedEventsList.add(eventMap.get(FirestoreKeys.Documents.UserJoinedEvents.EVENT_ID).toString());
                }
            }
            return joinedEventsList;
        }
        return null;
    }

    public void setJoinedEvents(List<Map<String, Object>> joinedEvents) { this.joinedEvents = joinedEvents; }

    public List<String> getHostedEvents(AccessType accessType) {
        if (hostedEvents != null && hostedEvents.size() > 0) {
            List<String> hostedEventsList = new ArrayList<>();
            for (Map<String, Object> eventMap : hostedEvents) {
                AccessType eventAccessType = AccessType.valueOf(eventMap.get(FirestoreKeys.Documents.UserHostedEvents.EVENT_ACCESS_TYPE).toString());
                if ((eventAccessType == AccessType.PUBLIC || eventAccessType == AccessType.SELECTIVE &&
                        (accessType == AccessType.PUBLIC || eventAccessType == AccessType.SELECTIVE)) ||
                        (accessType == AccessType.PRIVATE && eventAccessType == AccessType.PRIVATE)) {
                    hostedEventsList.add(eventMap.get(FirestoreKeys.Documents.UserHostedEvents.EVENT_ID).toString());
                }
            }
            return hostedEventsList;
        }
        return null;
    }

    public void setHostedEvents(List<Map<String, Object>> hostedEvents) { this.hostedEvents = hostedEvents; }

    public List<String> getLikedEvents(AccessType accessType) {
        if (likedEvents != null && likedEvents.size() > 0) {
            List<String> likedEventsList = new ArrayList<>();
            for (Map<String, Object> eventMap : likedEvents) {
                AccessType eventAccessType = AccessType.valueOf(eventMap.get(FirestoreKeys.Documents.UserLikedEvents.EVENT_ACCESS_TYPE).toString());
                if ((eventAccessType == AccessType.PUBLIC || eventAccessType == AccessType.SELECTIVE &&
                        (accessType == AccessType.PUBLIC || eventAccessType == AccessType.SELECTIVE)) ||
                        (accessType == AccessType.PRIVATE && eventAccessType == AccessType.PRIVATE)) {
                    likedEventsList.add(eventMap.get(FirestoreKeys.Documents.UserLikedEvents.EVENT_ID).toString());
                }
            }
            return likedEventsList;
        }
        return null;
    }

    public void setLikedEvents(List<Map<String, Object>> likedEvents) { this.likedEvents = likedEvents; }

    public void addLikedEvents(EventModel event) {
        Map<String, Object> profileLikedEventMap = new HashMap<>();
        profileLikedEventMap.put(FirestoreKeys.Documents.UserLikedEvents.EVENT_ID, event.getEventId());
        profileLikedEventMap.put(FirestoreKeys.Documents.UserLikedEvents.EVENT_ACCESS_TYPE, event.getAccessType());
        this.likedEvents.add(profileLikedEventMap);
    }

    public Map<String, Object> getSocialLinksMap() { return socialLinks; }

    public void setSocialLinksMap(Map<String, Object> socialLinks) { this.socialLinks = socialLinks; }

    public String getLinkTelegram() {
        if (socialLinks != null) {
            return (String) socialLinks.get(FirestoreKeys.Documents.UserSocialLinks.LINK_TELEGRAM);
        }
        return null;
    }

    public void setLinkTelegram(String linkTelegram) { socialLinks.put(FirestoreKeys.Documents.UserSocialLinks.LINK_TELEGRAM, linkTelegram); }

    public String getLinkTikTok() {
        if (socialLinks != null) {
            return (String) socialLinks.get(FirestoreKeys.Documents.UserSocialLinks.LINK_TIKTOK);
        }
        return null;
    }

    public void setLinkTikTok(String linkTikTok) { socialLinks.put(FirestoreKeys.Documents.UserSocialLinks.LINK_TIKTOK, linkTikTok); }

    public String getLinkInstagram() {
        if (socialLinks != null) {
            return (String) socialLinks.get(FirestoreKeys.Documents.UserSocialLinks.LINK_INSTAGRAM);
        }
        return null;
    }

    public void setLinkInstagram(String linkInstagram) { socialLinks.put(FirestoreKeys.Documents.UserSocialLinks.LINK_INSTAGRAM, linkInstagram); }

    public String getLinkFacebook() {
        if (socialLinks != null) {
            return (String) socialLinks.get(FirestoreKeys.Documents.UserSocialLinks.LINK_FACEBOOK);
        }
        return null;
    }

    public void setLinkFacebook(String linkFacebook) { socialLinks.put(FirestoreKeys.Documents.UserSocialLinks.LINK_FACEBOOK, linkFacebook); }

    public List<String> getUserFriends() { return userFriends; }

    public void setUserFriends(List<String> userFriends) { this.userFriends = userFriends; }

    public List<String> getBlockedUsers() { return blockedUsers; }

    public void setBlockedUsers(List<String> blockedUsers) { this.blockedUsers = blockedUsers; }

    public String getUserStatus() { return userStatus; }

    public void setUserStatus(String userStatus) { this.userStatus = userStatus; }

    public String getDateOfBirth() { return dateOfBirth; }

    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getCountryISO() { return countryISO; }

    public void setCountryISO(String countryISO) { this.countryISO = countryISO; }
}
