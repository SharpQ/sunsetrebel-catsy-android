package com.sunsetrebel.catsy.utils;

import android.util.Log;

import androidx.annotation.NonNull;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Source;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;


public class FirebaseFirestoreService {
    private FirebaseFirestore fStore = null;
    private DocumentReference documentReference = null;
    private boolean isUserExists;

//    public enum AuthTypes {
//        PHONE,
//        EMAIL,
//        GOOGLE_AUTH,
//        FACEBOOK_AUTH
//    }

    public interface FirebaseCallback {
        void onResponse(Boolean value);
    }

    private FirebaseFirestore getInstance() {
        return fStore = FirebaseFirestore.getInstance();
    }

    public void createNewUserByEmail(String userID, String fullName, String email){
        fStore = getInstance();
        documentReference = fStore.collection("userProfiles").document(userID);
        Map<String, Object> user = new HashMap<>();
        user.put("fullName", fullName);
        user.put("email", email);
        user.put("phone", null);
        user.put("profileImg", null);
        documentReference.set(user).addOnSuccessListener(aVoid -> {
            Log.d("INFO", "User profile created! UserID: " + userID);
        });
    }

    public void createNewUserByPhone(String userID, String fullName, String phone){
        fStore = getInstance();
        documentReference = fStore.collection("userProfiles").document(userID);
        Map<String, Object> user = new HashMap<>();
        user.put("fullName", fullName);
        user.put("email", null);
        user.put("phone", phone);
        user.put("profileImg", null);
        documentReference.set(user).addOnSuccessListener(aVoid -> {
            Log.d("INFO", "User profile created! UserID: " + userID);
        });
    }

    public void createNewUserByFacebook(String userID, String fullName, String email, String phone, String profileUrl){
        fStore = getInstance();
        documentReference = fStore.collection("userProfiles").document(userID);
        Map<String, Object> user = new HashMap<>();
        user.put("fullName", fullName);
        user.put("email", email);
        user.put("phone", phone);
        user.put("profileImg", profileUrl);
        documentReference.set(user).addOnSuccessListener(aVoid -> {
            Log.d("INFO", "User profile created! UserID: " + userID);
        });
    }

    public void createNewUserByGoogle(String userID, String fullName, String email, String phone, String profileUrl){
        fStore = getInstance();
        documentReference = fStore.collection("userProfiles").document(userID);
        Map<String, Object> user = new HashMap<>();
        user.put("fullName", fullName);
        user.put("email", email);
        user.put("phone", phone);
        user.put("profileImg", profileUrl);
        documentReference.set(user).addOnSuccessListener(aVoid -> {
            Log.d("INFO", "User profile created! UserID: " + userID);
        });
    }

    public void getUserInFirestore(FirebaseCallback firebaseCallback, String userId){
        fStore = getInstance();
        DocumentReference existingUser = fStore.collection("userProfiles").document(userId);
        existingUser.get(Source.SERVER).addOnCompleteListener(task -> {
            DocumentSnapshot document;
            document = task.getResult();
            firebaseCallback.onResponse(document.exists());
        });
    }
}
