package com.sunsetrebel.catsy.utils;

import android.util.Log;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class FirebaseFirestoreService {
    private FirebaseFirestore fStore = null;
    private DocumentReference documentReference = null;

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
        documentReference.set(user).addOnSuccessListener(aVoid -> {
            Log.d("INFO", "User profile created! UserID: " + userID);
        });
    }
}
