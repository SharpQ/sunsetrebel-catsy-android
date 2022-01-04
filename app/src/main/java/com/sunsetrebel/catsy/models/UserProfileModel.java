package com.sunsetrebel.catsy.models;

import java.util.List;

public class UserProfileModel {
    private String userId;
    private String userEmail;
    private String userPhone;
    private String userFullName;
    private String userProfileImg;
    private List<String> joinedEvents;
    private List<String> hostedPublicEvents;
    private List<String> hostedPrivateEvents;
    private List<String> likedEvents;

    public UserProfileModel() {
    }

    public UserProfileModel(String userId, String userEmail, String userPhone, String userFullName, String userProfileImg,
                            List<String> joinedEvents, List<String> hostedPublicEvents, List<String> hostedPrivateEvents,
                            List<String> likedEvents) {
        this.userId = userId;
        this.userEmail = userEmail;
        this.userPhone = userPhone;
        this.userFullName = userFullName;
        this.userProfileImg = userProfileImg;
        this.joinedEvents = joinedEvents;
        this.hostedPublicEvents = hostedPublicEvents;
        this.hostedPrivateEvents = hostedPrivateEvents;
        this.likedEvents = likedEvents;
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

    public List<String> getJoinedEvents() { return joinedEvents; }

    public void setJoinedEvents(List<String> joinedEvents) { this.joinedEvents = joinedEvents; }

    public List<String> getHostedPublicEvents() { return hostedPublicEvents; }

    public void setHostedPublicEvents(List<String> hostedPublicEvents) { this.hostedPublicEvents = hostedPublicEvents; }

    public List<String> getHostedPrivateEvents() { return hostedPrivateEvents; }

    public void setHostedPrivateEvents(List<String> hostedPrivateEvents) { this.hostedPrivateEvents = hostedPrivateEvents; }

    public List<String> getLikedEvents() { return likedEvents; }

    public void setLikedEvents(List<String> likedEvents) { this.likedEvents = likedEvents; }

    public void addLikedEvents(String eventId) {
        this.likedEvents.add(eventId);
    }
}
