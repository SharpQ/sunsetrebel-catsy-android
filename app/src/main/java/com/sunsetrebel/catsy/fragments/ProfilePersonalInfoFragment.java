package com.sunsetrebel.catsy.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.models.UserProfileModel;
import com.sunsetrebel.catsy.repositories.UserProfileService;

public class ProfilePersonalInfoFragment extends Fragment {
    private TextView profileEmail, profilePhone;
    private static UserProfileModel userProfileModel;

    public ProfilePersonalInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_personal_info, container, false);
        profileEmail = v.findViewById(R.id.tv_email_value);
        profilePhone = v.findViewById(R.id.tv_phone_value);

        UserProfileService userProfileService = UserProfileService.getInstance();
        userProfileModel = userProfileService.getUserProfile();
        profileEmail.setText(userProfileModel.getUserEmail());
        profilePhone.setText(userProfileModel.getUserPhone());
        return v;
    }
}
