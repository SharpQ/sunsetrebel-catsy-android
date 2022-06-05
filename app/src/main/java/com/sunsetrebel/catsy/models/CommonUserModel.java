package com.sunsetrebel.catsy.models;

import com.sunsetrebel.catsy.repositories.FirestoreKeys;

import java.util.Map;

public class CommonUserModel {
    private String userId;
    private String userFullName;
    private String userProfileImg;
    private Map<String, Object> socialLinks;
    private String userStatus;
    private String dateOfBirth;
    private String countryISO;

    public CommonUserModel() {
    }

    public CommonUserModel(String userId, String userFullName, String userProfileImg,
                           Map<String, Object> socialLinks, String userStatus,
                           String dateOfBirth, String countryISO) {
        this.userId = userId;
        this.userFullName = userFullName;
        this.userProfileImg = userProfileImg;
        this.socialLinks = socialLinks;
        this.userStatus = userStatus;
        this.dateOfBirth = dateOfBirth;
        this.countryISO = countryISO;
    }

    public String getUserId() { return userId; }

    public void setUserId(String userId) { this.userId = userId; }

    public String getUserFullName() { return userFullName; }

    public void setUserFullName(String userFullName) { this.userFullName = userFullName; }

    public String getUserProfileImg() { return userProfileImg; }

    public void setUserProfileImg(String userProfileImg) { this.userProfileImg = userProfileImg; }

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

    public String getUserStatus() { return userStatus; }

    public void setUserStatus(String userStatus) { this.userStatus = userStatus; }

    public String getDateOfBirth() { return dateOfBirth; }

    public void setDateOfBirth(String dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getCountryISO() { return countryISO; }

    public void setCountryISO(String countryISO) { this.countryISO = countryISO; }
}
