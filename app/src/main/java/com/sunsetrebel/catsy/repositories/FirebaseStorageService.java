package com.sunsetrebel.catsy.repositories;

import android.net.Uri;
import android.util.Log;

import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
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

    public interface GetAvatarStorageReference {
        void onResponse(String downloadUrl);
    }

    public void getAvatarStorageReference(GetAvatarStorageReference getAvatarStorageReference, String userId, Uri uri) {
        fStorage = getFirebaseStorageClient();
        StorageReference storageReference = fStorage.getReference("events/" + userId + "/" + UUID.randomUUID().toString() + ".jpg");
        storageReference.putFile(uri).addOnCompleteListener(task -> {
            if (task.isSuccessful()) {
                String downloadUrl = task.getResult().getUploadSessionUri().toString();
                getAvatarStorageReference.onResponse(downloadUrl);
            } else {
                Log.d("INFO", String.valueOf(task.getException()));
            }
        });
    }
}
