package com.sunsetrebel.catsy.adapters;


import android.content.Context;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.button.MaterialButton;
import com.google.android.material.imageview.ShapeableImageView;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.enums.PopupType;
import com.sunsetrebel.catsy.models.CommonUserModel;
import com.sunsetrebel.catsy.models.EventModel;
import com.sunsetrebel.catsy.models.InviteToEventModel;
import com.sunsetrebel.catsy.models.InviteToFriendsListModel;
import com.sunsetrebel.catsy.utils.CustomToastUtil;
import com.sunsetrebel.catsy.utils.ImageUtils;
import com.sunsetrebel.catsy.utils.PopupService;
import com.sunsetrebel.catsy.viewmodel.ProfileViewModel;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class NotificationsAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {
    private List<Object> notificationList;
    private Context context;
    private Fragment fragment;
    private ProfileViewModel profileViewModel;
    private SimpleDateFormat simpleDateFormat;

    public NotificationsAdapter(Fragment fragment, List<Object> notificationList) {
        this.notificationList = notificationList;
        this.context = fragment.getContext();
        this.fragment = fragment;
        profileViewModel = new ViewModelProvider(fragment.requireActivity()).get(ProfileViewModel.class);
        simpleDateFormat = new SimpleDateFormat("HH:mm d MMM ''yy", Locale.getDefault());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        switch (viewType) {
            case 1: {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_notification_friend_invite, parent, false);
                return new ViewHolderInviteToFriends(view);
            }
            case 2: {
                View view = LayoutInflater.from(parent.getContext())
                        .inflate(R.layout.item_notification_event_invite, parent, false);
                return new ViewHolderInviteToEvent(view);
            }
            default: return null;
        }
    }

    @Override
    public void onBindViewHolder(final RecyclerView.ViewHolder holder, int position) {
        switch (holder.getItemViewType()) {
            case 1:
                ViewHolderInviteToFriends viewHolderInviteToFriends = (ViewHolderInviteToFriends) holder;
                InviteToFriendsListModel inviteToFriends = (InviteToFriendsListModel) notificationList.get(position);
                ImageUtils.loadImageView(context, inviteToFriends.getSenderProfileImg(),
                        viewHolderInviteToFriends.ivSenderProfile, R.drawable.im_cat_hearts);
                viewHolderInviteToFriends.tvTimestamp.setText(simpleDateFormat.format(inviteToFriends.getCreateTS().toDate()));
                viewHolderInviteToFriends.tvTitle.setText(inviteToFriends.getSenderName() + context.getResources().getText(R.string.notification_friend_invite_title).toString());
                viewHolderInviteToFriends.acceptBtn.setOnClickListener(v -> profileViewModel.acceptFriendInvite(context, inviteToFriends));
                viewHolderInviteToFriends.declineBtn.setOnClickListener(v -> profileViewModel.declineFriendInvite(inviteToFriends));
                break;
            case 2:
                ViewHolderInviteToEvent viewHolderInviteToEvent = (ViewHolderInviteToEvent) holder;
                InviteToEventModel inviteToEvent = (InviteToEventModel) notificationList.get(position);
                ImageUtils.loadImageView(context, inviteToEvent.getSenderProfileImg(),
                        viewHolderInviteToEvent.ivSenderProfile, R.drawable.im_cat_hearts);
                ImageUtils.loadImageView(context, inviteToEvent.getEventAvatar(),
                        viewHolderInviteToEvent.ivEventAvatar, R.drawable.im_event_avatar_placeholder_64);
                viewHolderInviteToEvent.tvTimestamp.setText(simpleDateFormat.format(inviteToEvent.getCreateTS().toDate()));
                viewHolderInviteToEvent.tvTitle.setText(inviteToEvent.getSenderName() + context.getResources().getText(R.string.notification_event_invite_title).toString() + inviteToEvent.getEventTitle());
                viewHolderInviteToEvent.tvStartTime.setText(simpleDateFormat.format(inviteToEvent.getEventStartTime()));
                viewHolderInviteToEvent.tvLocation.setText(inviteToEvent.getEventLocation());
                viewHolderInviteToEvent.tvDescription.setText(inviteToEvent.getEventDescription());
                viewHolderInviteToEvent.acceptBtn.setOnClickListener(v -> profileViewModel.acceptEventInvite(context, inviteToEvent));
                viewHolderInviteToEvent.declineBtn.setOnClickListener(v -> profileViewModel.declineEventInvite(inviteToEvent));
                break;
            default: break;
        }
    }

    @Override
    public int getItemCount() {
        if (notificationList != null) {
            return notificationList.size();
        } else {
            return 0;
        }
    }

    @Override
    public int getItemViewType(int position) {
        if (notificationList.get(position) instanceof InviteToFriendsListModel) {
            return 1;
        } else if (notificationList.get(position) instanceof InviteToEventModel) {
            return 2;
        }
        return 0;
    }

    public static class ViewHolderInviteToFriends extends RecyclerView.ViewHolder {
        private ShapeableImageView ivSenderProfile;
        private TextView tvTimestamp, tvTitle;
        private MaterialButton acceptBtn, declineBtn;

        public ViewHolderInviteToFriends(View itemView) {
            super(itemView);
            ivSenderProfile = itemView.findViewById(R.id.iv_friend_invite_sender_avatar);
            tvTimestamp = itemView.findViewById(R.id.tv_friend_invite_timestamp);
            tvTitle = itemView.findViewById(R.id.tv_friend_invite_title);
            declineBtn = itemView.findViewById(R.id.button_friend_invite_decline);
            acceptBtn = itemView.findViewById(R.id.button_friend_invite_accept);
        }
    }

    public static class ViewHolderInviteToEvent extends RecyclerView.ViewHolder {
        private ShapeableImageView ivSenderProfile, ivEventAvatar;
        private TextView tvTimestamp, tvTitle, tvStartTime, tvLocation, tvDescription;
        private MaterialButton acceptBtn, declineBtn;

        public ViewHolderInviteToEvent(View itemView) {
            super(itemView);
            ivSenderProfile = itemView.findViewById(R.id.iv_event_invite_sender_avatar);
            ivEventAvatar = itemView.findViewById(R.id.iv_event_invite_event_avatar);
            tvTimestamp = itemView.findViewById(R.id.tv_event_invite_timestamp);
            tvTitle = itemView.findViewById(R.id.tv_event_invite_title);
            tvStartTime = itemView.findViewById(R.id.tv_event_invite_start_time);
            tvLocation = itemView.findViewById(R.id.tv_event_invite_location);
            tvDescription = itemView.findViewById(R.id.tv_event_invite_description);
            declineBtn = itemView.findViewById(R.id.button_event_invite_decline);
            acceptBtn = itemView.findViewById(R.id.button_event_invite_accept);
        }
    }
}

