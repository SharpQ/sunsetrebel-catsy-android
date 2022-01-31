package com.sunsetrebel.catsy.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.models.UserProfileModel;
import com.sunsetrebel.catsy.repositories.UserProfileService;
import com.sunsetrebel.catsy.utils.ImageUtils;

public class ProfileMainFragment extends Fragment {
    private Fragment personalInfoFragment;
    private ImageView profileImage;
    private TextView profileId, profileUserName;
    private static UserProfileModel userProfileModel;
    private TabLayout tabLayout;

    public ProfileMainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile, container, false);
        profileImage = v.findViewById(R.id.profile_image);
        profileUserName = v.findViewById(R.id.profile_username);
        profileId = v.findViewById(R.id.profile_userid);
        tabLayout = v.findViewById(R.id.tl_profile);

        personalInfoFragment = new ProfilePersonalInfoFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.fl_profile, personalInfoFragment).commit();

        UserProfileService userProfileService = UserProfileService.getInstance();
        userProfileModel = userProfileService.getUserProfile();
        ImageUtils.loadImageView(getContext(), userProfileModel.getUserProfileImg(), profileImage, R.drawable.im_cat_hearts);
        profileUserName.setText(userProfileModel.getUserFullName());
        profileId.setText(getContext().getString(R.string.profile_id_placeholder) + userProfileModel.getUserId());

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        getChildFragmentManager().beginTransaction().replace(R.id.fl_profile, personalInfoFragment).commit();
                        break;
                    case 1:
                        break;
                    case 2:
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        return v;
    }
}
