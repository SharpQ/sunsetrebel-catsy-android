package com.sunsetrebel.catsy.models;

import com.google.firebase.Timestamp;
import com.sunsetrebel.catsy.enums.AccessType;

import java.util.List;

public class InviteToFriendsListModel {
    private String action;
    private String senderId;
    private String senderName;
    private String senderProfileImg;
    private String recipientId;
    private Timestamp createTS;

    public InviteToFriendsListModel() {}

    public InviteToFriendsListModel(String action, String senderId, String senderName,
                                    String senderProfileImg, String recipientId,
                                    Timestamp createTS) {
        this.action = action;
        this.senderId = senderId;
        this.senderName = senderName;
        this.senderProfileImg = senderProfileImg;
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

    public String getRecipientId() { return recipientId; }

    public void setRecipientId(String recipientId) { this.recipientId = recipientId; }

    public Timestamp getCreateTS() { return createTS; }

    public void setCreateTS(Timestamp createTS) { this.createTS = createTS; }
}
