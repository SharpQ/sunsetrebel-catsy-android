package com.sunsetrebel.catsy.models;


import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.Timestamp;
import com.sunsetrebel.catsy.utils.AccessType;
import com.sunsetrebel.catsy.utils.EventThemes;

import java.util.Date;
import java.util.List;

public class EventModel {
    private String hostId;
    private String hostName;
    private String hostProfileImg;
    private String eventId;
    private String eventTitle;
    private String eventLocation;
    private LatLng eventGeoLocation;
    private Date eventStartTime;
    private Date eventEndTime;
    private AccessType accessType;
    private String eventDescr;
    private Integer eventMinAge;
    private Integer eventMaxAge;
    private Integer eventParticipants;
    private List<CommonUserModel> joinedUsersList;
    private Integer eventMaxPerson;
    private String eventAvatar;
    private List<EventThemes> eventThemes;
    private Timestamp createTS;
    private Timestamp updateTS;

    public EventModel() {
    }

    public EventModel(String hostId, String hostName, String hostProfileImg, String eventId,
                      String eventTitle, String eventLocation, LatLng eventGeoLocation,
                      Date eventStartTime, Date eventEndTime, AccessType accessType,
                      String eventDescr, Integer eventMinAge, Integer eventMaxAge, Integer eventParticipants, List<CommonUserModel> joinedUsersList,
                      Integer eventMaxPerson, String eventAvatar, List<EventThemes> eventThemes, Timestamp createTS, Timestamp updateTS) {
        this.hostId = hostId;
        this.hostName = hostName;
        this.hostProfileImg = hostProfileImg;
        this.eventId = eventId;
        this.eventTitle = eventTitle;
        this.eventLocation = eventLocation;
        this.eventGeoLocation = eventGeoLocation;
        this.eventStartTime = eventStartTime;
        this.eventEndTime = eventEndTime;
        this.accessType = accessType;
        this.eventDescr = eventDescr;
        this.eventMinAge = eventMinAge;
        this.eventMaxAge = eventMaxAge;
        this.eventParticipants = eventParticipants;
        this.joinedUsersList = joinedUsersList;
        this.eventMaxPerson = eventMaxPerson;
        this.eventAvatar = eventAvatar;
        this.eventThemes = eventThemes;
        this.createTS = createTS;
        this.updateTS = updateTS;
    }

    public String getHostId() { return hostId; }

    public void setHostId(String hostId) { this.hostId = hostId; }

    public String getHostName() { return hostName; }

    public void setHostName(String hostName) { this.hostName = hostName; }

    public String getHostProfileImg() { return hostProfileImg; }

    public void setHostProfileImg(String hostProfileImg) { this.hostProfileImg = hostProfileImg; }

    public String getEventId() {
        return eventId;
    }

    public void setEventId(String eventId) {
        this.eventId = eventId;
    }

    public String getEventTitle() {
        return eventTitle;
    }

    public void setEventTitle(String eventTitle) {
        this.eventTitle = eventTitle;
    }

    public String getEventLocation() { return eventLocation; }

    public void setEventLocation(String eventLocation) {
        this.eventLocation = eventLocation;
    }

    public LatLng getEventGeoLocation() {
        return eventGeoLocation;
    }

    public void setEventGeoLocation(LatLng eventGeoLocation) { this.eventGeoLocation = eventGeoLocation; }

    public Date getEventStartTime() {
        return eventStartTime;
    }

    public void setEventStartTime(Date eventStartTime) { this.eventStartTime = eventStartTime; }

    public Date getEventEndTime() {
        return eventEndTime;
    }

    public void setEventEndTime(Date eventEndTime) { this.eventEndTime = eventEndTime; }

    public AccessType getAccessType() {
        return accessType;
    }

    public void setAccessType(AccessType accessType) { this.accessType = accessType; }

    public String getEventDescr() {
        return eventDescr;
    }

    public void setEventDescr(String eventDescr) { this.eventDescr = eventDescr; }

    public Integer getEventMinAge() {
        return eventMinAge;
    }

    public void setEventMinAge(Integer eventMinAge) { this.eventMinAge = eventMinAge; }

    public Integer getEventMaxAge() {
        return eventMaxAge;
    }

    public void setEventMaxAge(Integer eventMaxAge) { this.eventMaxAge = eventMaxAge; }

    public Integer getEventParticipants() { return eventParticipants; }

    public void setEventParticipants(Integer eventParticipants) { this.eventParticipants = eventParticipants; }

    public List<CommonUserModel> getJoinedUsersList() { return joinedUsersList; }

    public void setJoinedUsersList(List<CommonUserModel> joinedUsersList) { this.joinedUsersList = joinedUsersList; }

    public Integer getEventMaxPerson() { return eventMaxPerson; }

    public void setEventMaxPerson(Integer eventMaxPerson) { this.eventMaxPerson = eventMaxPerson; }

    public String getEventAvatar() {
        return eventAvatar;
    }

    public void setEventAvatar(String eventAvatar) { this.eventAvatar = eventAvatar; }

    public List<EventThemes> getEventThemes() {
        return eventThemes;
    }

    public void setEventThemes(List<EventThemes> eventThemes) { this.eventThemes = eventThemes; }

    public Timestamp getCreateTS() {
        return createTS;
    }

    public void setCreateTS(Timestamp createTS) { this.createTS = createTS; }

    public Timestamp getUpdateTS() {
        return updateTS;
    }

    public void setUpdateTS(Timestamp updateTS) { this.updateTS = updateTS; }

}
