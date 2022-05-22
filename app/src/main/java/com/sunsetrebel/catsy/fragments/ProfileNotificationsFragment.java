package com.sunsetrebel.catsy.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.tabs.TabLayout;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.adapters.EventListAdapter;
import com.sunsetrebel.catsy.adapters.NotificationsAdapter;
import com.sunsetrebel.catsy.enums.NotificationType;
import com.sunsetrebel.catsy.models.MainUserProfileModel;
import com.sunsetrebel.catsy.repositories.FirebaseAuthService;
import com.sunsetrebel.catsy.repositories.UserProfileService;
import com.sunsetrebel.catsy.utils.ImageUtils;
import com.sunsetrebel.catsy.viewmodel.ProfileViewModel;

public class ProfileNotificationsFragment extends Fragment {
    private ImageView profileImage;
    private AppCompatButton backToProfileButton;
    private TabLayout tabLayout;
    private RecyclerView recyclerNotifications;
    private ProfileViewModel profileViewModel;
    private NotificationsAdapter notificationsAdapter;
    private boolean isRemoveListener = true;

    public ProfileNotificationsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_notifications, container, false);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);

        profileImage = v.findViewById(R.id.profile_image_notifications);
        tabLayout = v.findViewById(R.id.tl_profile);
        backToProfileButton = v.findViewById(R.id.button_back_to_main);
        recyclerNotifications = v.findViewById(R.id.recycler_notifications);

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerNotifications.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerNotifications.getContext(),
                layoutManager.getOrientation());
        recyclerNotifications.addItemDecoration(dividerItemDecoration);
        ImageUtils.loadImageView(getContext(), profileViewModel.getUserProfile().getUserProfileImg(), profileImage, R.drawable.im_cat_hearts);
        backToProfileButton.setOnClickListener(v1 -> {
            isRemoveListener = false;
            getParentFragmentManager().popBackStack();
        });

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                switch (tab.getPosition()) {
                    case 0:
                        profileViewModel.setNotificationTypeToDisplay(NotificationType.ALL);
                        break;
                    case 1:
                        profileViewModel.setNotificationTypeToDisplay(NotificationType.EVENT_INVITE);
                        break;
                    case 2:
                        profileViewModel.setNotificationTypeToDisplay(NotificationType.ADD_FRIEND);
                        break;
                }
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {}

            @Override
            public void onTabReselected(TabLayout.Tab tab) {}
        });

        profileViewModel.getNotificationsLiveData().observe(getViewLifecycleOwner(), notificationList -> {
            notificationsAdapter = new NotificationsAdapter(this, notificationList);
            recyclerNotifications.setAdapter(notificationsAdapter);
            notificationsAdapter.notifyDataSetChanged();
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
