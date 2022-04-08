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

    public void loadEventAvatar(LoadEventAvatarCallback loadEventAvatarCallback, EventModel eventModel, Uri uri) {
        fStorage = getFirebaseStorageClient();
        StorageReference storageReference = fStorage.getReference("userProfiles/" + eventModel.getHostId() + "/" + UUID.randomUUID().toString() + ".jpg");
        storageReference.putFile(uri).addOnSuccessListener(taskSnapshot -> {
            storageReference.getDownloadUrl().addOnSuccessListener(uri1 -> {
                String downloadUrl = uri1.toString();
                loadEventAvatarCallback.onResponse(downloadUrl);
            });
            storageReference.getDownloadUrl().addOnFailureListener(e -> loadEventAvatarCallback.onResponse(null));
        });
        storageReference.putFile(uri).addOnFailureListener(e -> loadEventAvatarCallback.onResponse(null));
    }
}
