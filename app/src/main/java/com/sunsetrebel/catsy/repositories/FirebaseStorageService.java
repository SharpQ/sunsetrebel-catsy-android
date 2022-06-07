package com.sunsetrebel.catsy.repositories;

import android.net.Uri;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.sunsetrebel.catsy.models.EventModel;

import java.util.UUID;


public class FirebaseStorageService {
    private static FirebaseStorageService instance;
    private FirebaseStorage fStorage;

    public FirebaseStorageService() {
        fStorage = FirebaseStorage.getInstance();
    }

    public static FirebaseStorageService getInstance() {
        if (instance == null) {
            instance = new FirebaseStorageService();
        }
        return instance;
    }

    private FirebaseStorage getFirebaseStorageClient() {
        if (fStorage == null) {
            fStorage = FirebaseStorage.getInstance();
        }
        return fStorage;
    }

    public interface LoadEventAvatarCallback {
        void onResponse(String downloadUrl);
    }

    private StorageReference getEventAvatarStorageRef(String hostId, String eventId) {
        if (hostId != null && eventId != null && !hostId.isEmpty() && !eventId.isEmpty()) {
            return fStorage.getReference("userProfiles/" + hostId + "/" + eventId + "/" + "main_avatar.jpg");
        }
        return null;
    }

    public void loadEventAvatar(LoadEventAvatarCallback loadEventAvatarCallback, EventModel eventModel, Uri uri) {
        fStorage = getFirebaseStorageClient();
        StorageReference avatarStorageRef = getEventAvatarStorageRef(eventModel.getHostId(), eventModel.getEventId());
        avatarStorageRef.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            avatarStorageRef.getDownloadUrl().addOnSuccessListener(uri1 -> {
                String downloadUrl = uri1.toString();
                loadEventAvatarCallback.onResponse(downloadUrl);
            });
            avatarStorageRef.getDownloadUrl().addOnFailureListener(e -> loadEventAvatarCallback.onResponse(null));
        }).addOnFailureListener(e -> loadEventAvatarCallback.onResponse(null));
    }
}
