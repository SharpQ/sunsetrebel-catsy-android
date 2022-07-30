package com.sunsetrebel.catsy.adapters;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.jwang123.flagkit.FlagKit;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.models.CommonUserModel;
import com.sunsetrebel.catsy.models.MainUserProfileModel;
import com.sunsetrebel.catsy.repositories.FirebaseFirestoreService;
import com.sunsetrebel.catsy.repositories.UserProfileService;
import com.sunsetrebel.catsy.utils.CustomToastUtil;
import com.sunsetrebel.catsy.utils.ExternalSocialsUtil;
import com.sunsetrebel.catsy.utils.ImageUtil;
import com.sunsetrebel.catsy.viewmodel.ProfileViewModel;

import java.util.List;

public class FriendListAdapter extends RecyclerView.Adapter<FriendListAdapter.ViewHolder> {
    private List<CommonUserModel> friendList;
    private Context context;
    private Fragment fragment;
    private FirebaseFirestoreService firebaseFirestoreService;
    private MainUserProfileModel mainUserProfileModel;
    private ProfileViewModel profileViewModel;


    public FriendListAdapter(Fragment fragment, List<CommonUserModel> friendList) {
        this.friendList = friendList;
        this.context = fragment.getContext();
        this.fragment = fragment;
        firebaseFirestoreService = FirebaseFirestoreService.getInstance();
        UserProfileService userProfileService = UserProfileService.getInstance();
        mainUserProfileModel = userProfileService.getUserProfile();
        profileViewModel = new ViewModelProvider(fragment.requireActivity()).get(ProfileViewModel.class);
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_user_friend_list, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageUtil.loadImageView(context, friendList.get(position).getUserProfileImg(),
                holder.imageUserProfile, R.drawable.im_cat_hearts);
        holder.tvUsername.setText(friendList.get(position).getUserFullName());
        if (friendList.get(position).getCountryISO() != null && !friendList.get(position).getCountryISO().isEmpty()) {
            Drawable countryFlag = FlagKit.drawableWithFlag(fragment.getContext(), friendList.get(position).getCountryISO().toLowerCase());
            if (countryFlag != null) {
                holder.ivCountryFlag.setVisibility(View.VISIBLE);
                holder.ivCountryFlag.setImageDrawable(countryFlag);
            }
        } else {
            holder.ivCountryFlag.setVisibility(View.GONE);
        }
        if (friendList.get(position).getUserStatus() != null && !friendList.get(position).getUserStatus().isEmpty()) {
            holder.tvUserStatus.setText(friendList.get(position).getUserStatus());
        } else {
            holder.tvUserStatus.setText(fragment.getContext().getString(R.string.profile_status_default));
        }

        if (mainUserProfileModel.getBlockedUsers().contains(friendList.get(position).getUserId())
                || mainUserProfileModel.getUserId().equals(friendList.get(position).getUserId())) {
            holder.buttonBlockUser.setEnabled(false);
            holder.buttonBlockUser.setVisibility(View.INVISIBLE);
        } else {
            holder.buttonBlockUser.setEnabled(true);
            holder.buttonBlockUser.setVisibility(View.VISIBLE);
        }

        if (friendList.get(position).getLinkFacebook() != null) {
            setSocialImageButton(R.drawable.im_facebook_link_profile, friendList.get(position).getLinkFacebook(), ExternalSocialsUtil.facebookPackageName,
                    ExternalSocialsUtil.defaultFacebookWeb, ExternalSocialsUtil.defaultFacebookMobile, fragment, holder.llSocialLinks);
        }

        if (friendList.get(position).getLinkInstagram() != null) {
            setSocialImageButton(R.drawable.im_instagram_link_profile, friendList.get(position).getLinkInstagram(), ExternalSocialsUtil.instagramPackageName,
                    ExternalSocialsUtil.defaultInstagramWeb, ExternalSocialsUtil.defaultInstagramMobile, fragment, holder.llSocialLinks);
        }

        if (friendList.get(position).getLinkTikTok() != null) {
            setSocialImageButton(R.drawable.im_tiktok_link_profile, friendList.get(position).getLinkTikTok(), ExternalSocialsUtil.tikTokPackageName,
                    ExternalSocialsUtil.defaultTikTokWeb, ExternalSocialsUtil.defaultTikTokMobile, fragment, holder.llSocialLinks);
        }

        if (friendList.get(position).getLinkTelegram() != null) {
            setSocialImageButton(R.drawable.im_telegram_link_profile, friendList.get(position).getLinkTelegram(), ExternalSocialsUtil.telegramPackageName,
                    ExternalSocialsUtil.defaultTelegramWeb, ExternalSocialsUtil.defaultTelegramMobile, fragment, holder.llSocialLinks);
        }

        holder.buttonRemoveFriend.setOnClickListener(v -> {
            firebaseFirestoreService.removeFriend(value -> {
                if (value) {
                    holder.buttonRemoveFriend.setEnabled(false);
                    CustomToastUtil.showSuccessToast(fragment.getContext(), fragment.getContext().getResources().getText(R.string.profile_remove_friend_success).toString() + friendList.get(position).getUserId());
                    Log.d("DEBUG", "Success remove friend from list: " + friendList.get(position).getUserId());
                } else {
                    holder.buttonRemoveFriend.setEnabled(true);
                    CustomToastUtil.showSuccessToast(fragment.getContext(), fragment.getContext().getResources().getText(R.string.profile_remove_friend_fail).toString() + friendList.get(position).getUserId());
                    Log.d("DEBUG", "Failed to remove friend from list: " + friendList.get(position).getUserId());
                }
            }, mainUserProfileModel.getUserId(), friendList.get(position).getUserId());
        });

        holder.buttonBlockUser.setOnClickListener(v -> {
            firebaseFirestoreService.setUserToBlocked(value -> {
                if (value) {
                    holder.buttonBlockUser.setEnabled(false);
                    CustomToastUtil.showSuccessToast(fragment.getContext(), fragment.getContext().getResources().getText(R.string.user_blocked_success).toString() + friendList.get(position).getUserId());
                    Log.d("DEBUG", "Success block user: " + friendList.get(position).getUserId());
                } else {
                    holder.buttonBlockUser.setEnabled(true);
                    CustomToastUtil.showFailToast(fragment.getContext(), fragment.getContext().getResources().getText(R.string.user_blocked_fail).toString() + friendList.get(position).getUserId());
                    Log.d("DEBUG", "Fail block user: " + friendList.get(position).getUserId());
                }
            }, mainUserProfileModel, friendList.get(position).getUserId());
        });
    }

    @Override
    public int getItemCount() {
        if (friendList != null) {
            return friendList.size();
        } else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ShapeableImageView imageUserProfile;
        private ImageView ivCountryFlag;
        private TextView tvUsername, tvUserStatus;
        private LinearLayout llSocialLinks;
        private AppCompatButton buttonRemoveFriend, buttonBlockUser;

        public ViewHolder(View itemView) {
            super(itemView);
            imageUserProfile = itemView.findViewById(R.id.friend_list_image_user_item);
            tvUsername = itemView.findViewById(R.id.friend_list_username_item);
            tvUserStatus = itemView.findViewById(R.id.friend_list_user_status_item);
            llSocialLinks = itemView.findViewById(R.id.ll_user_socials_item);
            buttonRemoveFriend = itemView.findViewById(R.id.button_remove_friend_item);
            buttonBlockUser = itemView.findViewById(R.id.button_block_user_item);
            ivCountryFlag = itemView.findViewById(R.id.iv_country_flag);
        }
    }

    private void setSocialImageButton(int resId, String userId, String packageName, String defaultWebLink,
                                      String defaultMobileLink, Fragment fragment, LinearLayout linearLayout) {
        ImageButton imageButton = new ImageButton(fragment.getContext());
        imageButton.setImageResource(resId);
        imageButton.setBackgroundColor(fragment.getResources().getColor(R.color.primaryDarkColor));
        int imageSizeDP = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 55, fragment.getResources().getDisplayMetrics());
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageSizeDP, imageSizeDP);
        params.setMargins(10,0,10,0);
        imageButton.setLayoutParams(params);
        imageButton.setAdjustViewBounds(true);
        imageButton.setScaleType(ImageView.ScaleType.FIT_XY);
        imageButton.setPadding(0,0,0,0);

        linearLayout.addView(imageButton);

        imageButton.setOnClickListener(v -> ExternalSocialsUtil.openLink(fragment.getContext(), userId, packageName, defaultWebLink, defaultMobileLink));
    }
}
