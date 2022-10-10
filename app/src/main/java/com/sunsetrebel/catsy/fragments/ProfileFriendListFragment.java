package com.sunsetrebel.catsy.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.adapters.FriendListAdapter;
import com.sunsetrebel.catsy.viewmodel.ProfileViewModel;

public class ProfileFriendListFragment extends Fragment {
    private ProfileViewModel profileViewModel;
    private FriendListAdapter notificationsAdapter;

    public ProfileFriendListFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.item_profile_friend_list, container, false);
        profileViewModel = new ViewModelProvider(requireActivity()).get(ProfileViewModel.class);
        RecyclerView recyclerFriendList = v.findViewById(R.id.recycler_friend_list);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        recyclerFriendList.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerFriendList.getContext(),
                layoutManager.getOrientation());
        recyclerFriendList.addItemDecoration(dividerItemDecoration);

        profileViewModel.getFriendListLiveData().observe(getViewLifecycleOwner(), friendList -> {
//TO DO: add label for no friends list
//            if (friendList.size() == 0) {
//                tvNoNotifications.setVisibility(View.VISIBLE);
//                tvNoNotifications.setEnabled(true);
//            } else {
//                tvNoNotifications.setVisibility(View.INVISIBLE);
//                tvNoNotifications.setEnabled(false);
//            }
            notificationsAdapter = new FriendListAdapter(this, friendList);
            recyclerFriendList.setAdapter(notificationsAdapter);
            notificationsAdapter.notifyDataSetChanged();
        });
        return v;
    }
}
