package com.sunsetrebel.catsy.adapters;

import android.content.Context;
import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.imageview.ShapeableImageView;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.fragments.EventListDetailedFragment;
import com.sunsetrebel.catsy.models.CommonUserModel;
import com.sunsetrebel.catsy.models.EventModel;
import com.sunsetrebel.catsy.models.MainUserProfileModel;
import com.sunsetrebel.catsy.repositories.FirebaseFirestoreService;
import com.sunsetrebel.catsy.repositories.UserProfileService;
import com.sunsetrebel.catsy.utils.CustomToastUtil;
import com.sunsetrebel.catsy.utils.EventThemesUtil;
import com.sunsetrebel.catsy.utils.ExternalSocialsUtil;
import com.sunsetrebel.catsy.utils.ImageUtils;
import com.sunsetrebel.catsy.viewmodel.EventListViewModel;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PopupEventParticipantsAdapter extends RecyclerView.Adapter<PopupEventParticipantsAdapter.ViewHolder> {
    private List<CommonUserModel> listOfUsers;
    private Context context;
    private Fragment fragment;
    private FirebaseFirestoreService firebaseFirestoreService;
    private MainUserProfileModel mainUserProfileModel;


    public PopupEventParticipantsAdapter(Fragment fragment, List<CommonUserModel> listOfUsers) {
        this.listOfUsers = listOfUsers;
        this.context = fragment.getContext();
        this.fragment = fragment;
        firebaseFirestoreService = FirebaseFirestoreService.getInstance();
        UserProfileService userProfileService = UserProfileService.getInstance();
        mainUserProfileModel = userProfileService.getUserProfile();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_event_participants, parent, false);
        ViewHolder vh = new ViewHolder(view);
        return vh;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        ImageUtils.loadImageView(context, listOfUsers.get(position).getUserProfileImg(),
                holder.imageUserProfile, R.drawable.im_cat_hearts);
        holder.tvUsername.setText(listOfUsers.get(position).getUserFullName());
        holder.tvUserId.setText(listOfUsers.get(position).getUserId());

        if (mainUserProfileModel.getUserFriends().contains(listOfUsers.get(position).getUserId())
                || mainUserProfileModel.getUserId().equals(listOfUsers.get(position).getUserId())) {
            holder.buttonAddFriend.setEnabled(false);
            holder.buttonAddFriend.setVisibility(View.INVISIBLE);
        } else {
            holder.buttonAddFriend.setEnabled(true);
            holder.buttonAddFriend.setVisibility(View.VISIBLE);
        }

        if (mainUserProfileModel.getBlockedUsers().contains(listOfUsers.get(position).getUserId())
                || mainUserProfileModel.getUserId().equals(listOfUsers.get(position).getUserId())) {
            holder.buttonBlockUser.setEnabled(false);
            holder.buttonBlockUser.setVisibility(View.INVISIBLE);
        } else {
            holder.buttonBlockUser.setEnabled(true);
            holder.buttonBlockUser.setVisibility(View.VISIBLE);
        }

        if (listOfUsers.get(position).getLinkFacebook() != null) {
            setSocialImageButton(R.drawable.im_facebook_link_profile, listOfUsers.get(position).getLinkFacebook(), ExternalSocialsUtil.facebookPackageName,
                    ExternalSocialsUtil.defaultFacebookWeb, ExternalSocialsUtil.defaultFacebookMobile, fragment, holder.llSocialLinks);
        }

        if (listOfUsers.get(position).getLinkInstagram() != null) {
            setSocialImageButton(R.drawable.im_instagram_link_profile, listOfUsers.get(position).getLinkInstagram(), ExternalSocialsUtil.instagramPackageName,
                    ExternalSocialsUtil.defaultInstagramWeb, ExternalSocialsUtil.defaultInstagramMobile, fragment, holder.llSocialLinks);
        }

        if (listOfUsers.get(position).getLinkTikTok() != null) {
            setSocialImageButton(R.drawable.im_tiktok_link_profile, listOfUsers.get(position).getLinkTikTok(), ExternalSocialsUtil.tikTokPackageName,
                    ExternalSocialsUtil.defaultTikTokWeb, ExternalSocialsUtil.defaultTikTokMobile, fragment, holder.llSocialLinks);
        }

        if (listOfUsers.get(position).getLinkTelegram() != null) {
            setSocialImageButton(R.drawable.im_telegram_link_profile, listOfUsers.get(position).getLinkTelegram(), ExternalSocialsUtil.telegramPackageName,
                    ExternalSocialsUtil.defaultTelegramWeb, ExternalSocialsUtil.defaultTelegramMobile, fragment, holder.llSocialLinks);
        }

        holder.buttonAddFriend.setOnClickListener(v -> {
            firebaseFirestoreService.sendFriendRequest(value -> {
                if (value) {
                    holder.buttonAddFriend.setEnabled(false);
                    holder.buttonAddFriend.setVisibility(View.INVISIBLE);
                    CustomToastUtil.showSuccessToast(fragment.getContext(), fragment.getContext().getResources().getText(R.string.user_friend_request_success).toString() + listOfUsers.get(position).getUserId());
                    Log.d("DEBUG", "Friend request sent: " + listOfUsers.get(position).getUserId());
                } else {
                    holder.buttonAddFriend.setEnabled(true);
                    holder.buttonAddFriend.setVisibility(View.VISIBLE);
                    CustomToastUtil.showSuccessToast(fragment.getContext(), fragment.getContext().getResources().getText(R.string.user_friend_request_fail).toString() + listOfUsers.get(position).getUserId());
                    Log.d("DEBUG", "Failed to send friend request: " + listOfUsers.get(position).getUserId());
                }
            }, mainUserProfileModel.getUserId(), listOfUsers.get(position).getUserId());
        });

        holder.buttonBlockUser.setOnClickListener(v -> {
            firebaseFirestoreService.setUserToBlocked(value -> {
                if (value) {
                    holder.buttonBlockUser.setEnabled(false);
                    holder.buttonBlockUser.setVisibility(View.INVISIBLE);
                    CustomToastUtil.showSuccessToast(fragment.getContext(), fragment.getContext().getResources().getText(R.string.user_blocked_success).toString() + listOfUsers.get(position).getUserId());
                    Log.d("DEBUG", "Success block user: " + listOfUsers.get(position).getUserId());
                } else {
                    holder.buttonBlockUser.setEnabled(true);
                    holder.buttonBlockUser.setVisibility(View.VISIBLE);
                    CustomToastUtil.showFailToast(fragment.getContext(), fragment.getContext().getResources().getText(R.string.user_blocked_fail).toString() + listOfUsers.get(position).getUserId());
                    Log.d("DEBUG", "Fail block user: " + listOfUsers.get(position).getUserId());
                }
            }, mainUserProfileModel.getUserId(), listOfUsers.get(position).getUserId());
        });
    }

    @Override
    public int getItemCount() {
        if (listOfUsers != null) {
            return listOfUsers.size();
        } else {
            return 0;
        }
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        private ShapeableImageView imageUserProfile;
        private TextView tvUsername, tvUserId;
        private LinearLayout llSocialLinks;
        private AppCompatButton buttonAddFriend, buttonBlockUser;

        public ViewHolder(View itemView) {
            super(itemView);
            imageUserProfile = itemView.findViewById(R.id.profile_image_user_item);
            tvUsername = itemView.findViewById(R.id.profile_username_item);
            tvUserId = itemView.findViewById(R.id.profile_userid_item);
            llSocialLinks = itemView.findViewById(R.id.ll_user_socials_item);
            buttonAddFriend = itemView.findViewById(R.id.button_add_friend_item);
            buttonBlockUser = itemView.findViewById(R.id.button_block_user_item);
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
