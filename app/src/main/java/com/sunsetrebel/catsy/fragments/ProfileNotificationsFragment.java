package com.sunsetrebel.catsy.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.tabs.TabLayout;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.models.MainUserProfileModel;
import com.sunsetrebel.catsy.repositories.FirebaseAuthService;
import com.sunsetrebel.catsy.repositories.UserProfileService;
import com.sunsetrebel.catsy.utils.ImageUtils;
import com.sunsetrebel.catsy.viewmodel.ProfileViewModel;

public class ProfileNotificationsFragment extends Fragment {
    private ImageView profileImage;
    private AppCompatButton backToProfileButton;
    private static MainUserProfileModel mainUserProfileModel;
    private TabLayout tabLayout;
    private ProfileViewModel profileViewModel;

    public ProfileNotificationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_notifications, container, false);
        profileViewModel = new ViewModelProvider(this).get(ProfileViewModel.class);
        mainUserProfileModel = profileViewModel.getUserProfile();

        profileImage = v.findViewById(R.id.profile_image_notifications);
        tabLayout = v.findViewById(R.id.tl_profile);
        backToProfileButton = v.findViewById(R.id.button_back_to_main);
        v.findViewById(R.id.recycler_notifications);

        ImageUtils.loadImageView(getContext(), mainUserProfileModel.getUserProfileImg(), profileImage, R.drawable.im_cat_hearts);

        backToProfileButton.setOnClickListener(v1 -> getParentFragmentManager().popBackStack());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });
        return v;
    }
}
