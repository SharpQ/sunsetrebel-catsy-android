package com.sunsetrebel.catsy.fragments;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.material.tabs.TabLayout;
import com.jwang123.flagkit.FlagKit;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.activities.LoginActivity;
import com.sunsetrebel.catsy.utils.ImageUtil;
import com.sunsetrebel.catsy.viewmodel.ProfileViewModel;

public class ProfileMainFragment extends Fragment {
    private Fragment personalInfoFragment;
    private ImageView profileImage, countryFlagImage;
    private TextView profileStatus, profileUserName;
    private AppCompatButton logoutButton;
    private TabLayout tabLayout;
    private ImageButton notificationsBtn, circleBtn;
    private ProfileViewModel profileViewModel;
    private boolean isRemoveListener = true;

    public ProfileMainFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_main, container, false);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        profileViewModel.init();

        profileImage = v.findViewById(R.id.profile_image);
        profileUserName = v.findViewById(R.id.profile_username);
        profileStatus = v.findViewById(R.id.profile_user_status);
        tabLayout = v.findViewById(R.id.tl_profile);
        logoutButton = v.findViewById(R.id.button_logout);
        notificationsBtn = v.findViewById(R.id.ib_profile_notification);
        circleBtn = v.findViewById(R.id.ib_circle);
        countryFlagImage = v.findViewById(R.id.iv_country_flag);

        ImageUtil.loadImageView(getContext(), profileViewModel.getUserProfile().getUserProfileImg(), profileImage, R.drawable.im_cat_hearts);
        profileUserName.setText(profileViewModel.getUserProfile().getUserFullName());
        if (profileViewModel.getUserProfile().getCountryISO() != null && !profileViewModel.getUserProfile().getCountryISO().isEmpty()) {
            Drawable countryFlag = FlagKit.drawableWithFlag(getContext(), profileViewModel.getUserProfile().getCountryISO().toLowerCase());
            if (countryFlag != null) {
                countryFlagImage.setVisibility(View.VISIBLE);
                countryFlagImage.setImageDrawable(countryFlag);
            }
        } else {
            countryFlagImage.setVisibility(View.GONE);
        }
        if (profileViewModel.getUserProfile().getUserStatus() != null && !profileViewModel.getUserProfile().getUserStatus().isEmpty()) {
            profileStatus.setText(profileViewModel.getUserProfile().getUserStatus());
        } else {
            profileStatus.setText(getContext().getString(R.string.profile_status_default));
        }
        personalInfoFragment = new ProfilePersonalInfoFragment();
        getChildFragmentManager().beginTransaction().replace(R.id.fl_profile, personalInfoFragment).commit();

        profileViewModel.getNotificationsLiveData().observe(getViewLifecycleOwner(), notificationList -> {
            if (notificationList != null && notificationList.size() > 0) {
                circleBtn.setVisibility(View.VISIBLE);
            } else {
                circleBtn.setVisibility(View.INVISIBLE);
            }
        });

        logoutButton.setOnClickListener(v1 -> {
            profileViewModel.logoutUser();
            startActivity(new Intent(getContext(), LoginActivity.class));
            Animatoo.animateFade(getContext());  //fire the zoom animation
            getActivity().finish();
        });

        notificationsBtn.setOnClickListener(v12 -> {
            isRemoveListener = false;
            getParentFragmentManager().beginTransaction().addToBackStack("ProfileMainFragment")
                    .replace(R.id.frameLayoutMain, new ProfileNotificationsFragment()).commit();
        });

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
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        return v;
    }

    @Override
    public void onStop() {
        super.onStop();
        if (isRemoveListener) {
            profileViewModel.removeNotificationsListener();
        }
    }
}
