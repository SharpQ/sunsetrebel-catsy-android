package com.sunsetrebel.catsy.models;

import com.google.firebase.Timestamp;
import com.sunsetrebel.catsy.enums.AccessType;

import java.sql.Time;
import java.util.Date;
import java.util.List;

public class InviteToEventModel {
   private String action;
   private String senderId;
   private String senderName;
   private String senderProfileImg;
   private String eventId;
   private String eventTitle;
   private String eventDescription;
   private String eventLocation;
   private Date eventStartTime;
   private String eventAvatar;
   private AccessType accessType;
   private String recipientId;
   private Timestamp createTS;

   public InviteToEventModel() {}

   public InviteToEventModel(String action, String senderId, String senderName,
                             String senderProfileImg, String eventId, String eventTitle,
                             String eventDescription, String eventLocation,
                             Date eventStartTime, String eventAvatar, AccessType accessType,
                             String recipientId, Timestamp createTS) {
       this.action = action;
       this.senderId = senderId;
       this.senderName = senderName;
       this.senderProfileImg = senderProfileImg;
       this.eventId = eventId;
       this.eventTitle = eventTitle;
       this.eventDescription = eventDescription;
       this.eventLocation = eventLocation;
       this.eventStartTime = eventStartTime;
       this.eventAvatar = eventAvatar;
       this.accessType = accessType;
       this.recipientId = recipientId;
       this.createTS = createTS;
   }

   public String getAction() { return action; }

   public void setAction(String action) { this.action = action; }

   public String getSenderId() { return senderId; }

   public void setSenderId(String senderId) { this.senderId = senderId; }

   public String getSenderName() { return senderName; }

   public void setSenderName(String senderName) { this.senderName = senderName; }

   public String getSenderProfileImg() { return senderProfileImg; }

   public void setSenderProfileImg(String senderProfileImg) { this.senderProfileImg = senderProfileImg; }

   public String getEventId() { return eventId; }

   public void setEventId(String eventId) { this.eventId = eventId; }

   public String getEventTitle() { return eventTitle; }

   public void setEventTitle(String eventTitle) { this.eventTitle = eventTitle; }

   public String getEventDescription() { return eventDescription; }

   public void setEventDescription(String eventDescription) { this.eventDescription = eventDescription; }

   public String getEventLocation() { return eventLocation; }

   public void setEventLocation(String eventLocation) { this.eventLocation = eventLocation; }

   public Date getEventStartTime() { return eventStartTime; }

   public void setEventStartTime(Date eventStartTime) { this.eventStartTime = eventStartTime; }

   public String getEventAvatar() { return eventAvatar; }

   public void setEventAvatar(String eventAvatar) { this.eventAvatar = eventAvatar; }

   public AccessType getAccessType() { return accessType; }

   public void setAccessType(AccessType accessType) { this.accessType = accessType; }

   public String getRecipientId() { return recipientId; }

   public void setRecipientId(String recipientId) { this.recipientId = recipientId; }

   public Timestamp getCreateTS() { return createTS; }

   public void setCreateTS(Timestamp createTS) { this.createTS = createTS; }
}

