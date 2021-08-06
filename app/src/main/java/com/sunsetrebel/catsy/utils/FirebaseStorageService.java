package com.sunsetrebel.catsy.utils;

import android.net.Uri;
import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import java.util.UUID;


public class FirebaseStorageService {
    private FirebaseStorage fStorage = null;

    public interface GetAvatarStorageReference {
        void onResponse(String downloadUrl);
    }

    private FirebaseStorage getInstance() {
        return fStorage = FirebaseStorage.getInstance();
    }


    public void getAvatarStorageReference(GetAvatarStorageReference getAvatarStorageReference, String userId, Uri uri) {
        fStorage = getInstance();
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
