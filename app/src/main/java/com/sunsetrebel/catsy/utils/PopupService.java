package com.sunsetrebel.catsy.utils;

import android.util.Log;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.adapters.EventListAdapter;
import com.sunsetrebel.catsy.adapters.PopupEventParticipantsAdapter;
import com.sunsetrebel.catsy.enums.PopupType;
import com.sunsetrebel.catsy.models.CommonUserModel;
import com.sunsetrebel.catsy.models.EventModel;
import com.sunsetrebel.catsy.models.MainUserProfileModel;
import com.sunsetrebel.catsy.repositories.FirebaseFirestoreService;
import com.sunsetrebel.catsy.repositories.UserProfileService;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class PopupService {
    private static PopupWindow infoPopup;

    public static void showPopup(PopupWindow infoPopup, Fragment fragment, int gravity) {
        closePopup();
        PopupService.infoPopup = infoPopup;
        infoPopup.showAtLocation(fragment.getView(), gravity, 0, 0);
    }

    public static void closePopup() {
        if (infoPopup != null) {
            infoPopup.dismiss();
        }
    }

    public static class PopupBuilder
    {
        private final SimpleDateFormat simpleDateFormat;
        private final EventThemesUtil eventThemesUtil;
        private final Fragment fragment;
        public PopupWindow infoPopup;
        private View popupView;


        public PopupBuilder(Fragment fragment, Object dataModel, PopupType popupType, int width,
                            int height) {
            this.fragment = fragment;
            this.simpleDateFormat = new SimpleDateFormat("HH:mm d MMM ''yy", Locale.getDefault());
            this.eventThemesUtil = EventThemesUtil.getInstance(fragment.getContext().getResources());

            switch (popupType) {
                case EVENT_MAPS:
                    popupView = setupViewMapsFragment(fragment, (EventModel) dataModel);
                    break;
                case USER_EVENT_DETAILED:
                    popupView = setupViewUserEventDetailed(fragment, (CommonUserModel) dataModel);
                    break;
                case EVENT_PARTICIPANTS:
                    popupView = setupViewEventParticipants(fragment, (List<CommonUserModel>) dataModel);
                    break;
                default:
                    popupView = null;
                    break;
            }
            this.infoPopup = new PopupWindow(popupView,
                    width,
                    height);
        }

        public PopupBuilder width(int width) {
            infoPopup.setWidth(width);
            return this;
        }

        public PopupBuilder height(int height) {
            infoPopup.setHeight(height);
            return this;
        }

        public PopupBuilder animationStyle(int animationStyle) {
            infoPopup.setAnimationStyle(animationStyle);
            return this;
        }

        public PopupBuilder setFocusable(boolean isFocusable) {
            infoPopup.setFocusable(isFocusable);
            return this;
        }

        public PopupBuilder setForeground() {
            FrameLayout frameLayout = fragment.getActivity().findViewById(R.id.frameLayoutMain);
            frameLayout.getForeground().setAlpha(180);
            infoPopup.setOnDismissListener(() -> frameLayout.getForeground().setAlpha(0));
            return this;
        }

        public PopupWindow build() {
            return infoPopup;
        }

        private View setupViewMapsFragment(Fragment fragment, EventModel eventModel) {
            View popupView = LayoutInflater.from(fragment.getContext()).inflate(R.layout.popup_event_map_fragment, null, false);
            TextView tvEventTitle = popupView.findViewById(R.id.textViewEventTitle);
            TextView tvHostName = popupView.findViewById(R.id.textViewHostName);
            TextView tvStartTime = popupView.findViewById(R.id.til_start_time);
            TextView tvEventLocation = popupView.findViewById(R.id.textViewLocation);
            TextView tvEventDescription = popupView.findViewById(R.id.tiet_event_description);
            TextView tvEventParticipants = popupView.findViewById(R.id.textViewParticipants);
            LinearLayout linearLayout = popupView.findViewById(R.id.ll_tags);
            ImageButton likeButton = popupView.findViewById(R.id.imageButtonLike);
            ImageButton shareButton = popupView.findViewById(R.id.imageButtonShare);
            ImageView ivEventAvatar = popupView.findViewById(R.id.imageViewEventAvatar);
            ImageView ivHostAvatar = popupView.findViewById(R.id.imageViewHostAvatar);

            //Set event avatar
            ImageUtils.loadImageView(fragment.getContext(), eventModel.getEventAvatar(), ivEventAvatar, R.drawable.im_event_avatar_placeholder_64);
            //Set host avatar
            ImageUtils.loadImageView(fragment.getContext(), eventModel.getHostProfileImg(), ivHostAvatar, R.drawable.im_cat_hearts);
            tvEventTitle.setText(eventModel.getEventTitle());
            tvHostName.setText(eventModel.getHostName());
            tvStartTime.setText(simpleDateFormat.format(eventModel.getEventStartTime()));
            tvEventParticipants.setText(String.format(Locale.getDefault(), "%d", eventModel.getEventParticipants()));
            tvEventDescription.setText(eventModel.getEventDescr());
            tvEventLocation.setText(eventModel.getEventLocation());
            likeButton.setEnabled(false);
            likeButton.setVisibility(View.INVISIBLE);
            shareButton.setEnabled(false);
            shareButton.setVisibility(View.INVISIBLE);
            eventThemesUtil.setEventThemesUI(eventModel.getEventThemes(), fragment, linearLayout, null);
            return popupView;
        }

        private View setupViewUserEventDetailed(Fragment fragment, CommonUserModel userProfile) {
            View popupView = LayoutInflater.from(fragment.getContext()).inflate(R.layout.popup_common_user, null, false);
            TextView tvUserName = popupView.findViewById(R.id.profile_username);
            TextView tvUserId = popupView.findViewById(R.id.profile_userid);
            AppCompatButton btnAddToFriends = popupView.findViewById(R.id.button_add_friend);
            AppCompatButton btnBlockUser = popupView.findViewById(R.id.button_block_user);
            ImageView ivUserAvatar = popupView.findViewById(R.id.profile_image_user);
            LinearLayout linearUserSocials = popupView.findViewById(R.id.ll_user_socials);
            FirebaseFirestoreService firebaseFirestoreService = FirebaseFirestoreService.getInstance();
            UserProfileService userProfileService = UserProfileService.getInstance();
            MainUserProfileModel mainUserProfileModel = userProfileService.getUserProfile();

            if (mainUserProfileModel.getUserFriends().contains(userProfile.getUserId()) || mainUserProfileModel.getUserId().equals(userProfile.getUserId())) {
                btnAddToFriends.setEnabled(false);
                btnAddToFriends.setVisibility(View.INVISIBLE);
            } else {
                btnAddToFriends.setEnabled(true);
                btnAddToFriends.setVisibility(View.VISIBLE);
            }

            if (mainUserProfileModel.getBlockedUsers().contains(userProfile.getUserId()) || mainUserProfileModel.getUserId().equals(userProfile.getUserId())) {
                btnBlockUser.setEnabled(false);
                btnBlockUser.setVisibility(View.INVISIBLE);
            } else {
                btnBlockUser.setEnabled(true);
                btnBlockUser.setVisibility(View.VISIBLE);
            }

            tvUserName.setText(userProfile.getUserFullName());
            tvUserId.setText(userProfile.getUserId());
            ImageUtils.loadImageView(fragment.getContext(), userProfile.getUserProfileImg(), ivUserAvatar, R.drawable.im_cat_hearts);

            btnAddToFriends.setOnClickListener(v -> {
                firebaseFirestoreService.sendFriendRequest(value -> {
                    if (value) {
                        btnAddToFriends.setEnabled(false);
                        btnAddToFriends.setVisibility(View.INVISIBLE);
                        CustomToastUtil.showSuccessToast(fragment.getContext(), fragment.getContext().getResources().getText(R.string.user_friend_request_success).toString() + userProfile.getUserId());
                        Log.d("DEBUG", "Friend request sent: " + userProfile.getUserId());
                    } else {
                        btnAddToFriends.setEnabled(true);
                        btnAddToFriends.setVisibility(View.VISIBLE);
                        CustomToastUtil.showSuccessToast(fragment.getContext(), fragment.getContext().getResources().getText(R.string.user_friend_request_fail).toString() + userProfile.getUserId());
                        Log.d("DEBUG", "Failed to send friend request: " + userProfile.getUserId());
                    }
                }, mainUserProfileModel, userProfile.getUserId());
            });

            btnBlockUser.setOnClickListener(v -> {
                firebaseFirestoreService.setUserToBlocked(value -> {
                    if (value) {
                        btnBlockUser.setEnabled(false);
                        btnBlockUser.setVisibility(View.INVISIBLE);
                        CustomToastUtil.showSuccessToast(fragment.getContext(), fragment.getContext().getResources().getText(R.string.user_blocked_success).toString() + userProfile.getUserId());
                        Log.d("DEBUG", "Success block user: " + userProfile.getUserId());
                    } else {
                        btnBlockUser.setEnabled(true);
                        btnBlockUser.setVisibility(View.VISIBLE);
                        CustomToastUtil.showFailToast(fragment.getContext(), fragment.getContext().getResources().getText(R.string.user_blocked_fail).toString() + userProfile.getUserId());
                        Log.d("DEBUG", "Fail block user: " + userProfile.getUserId());
                    }
                }, mainUserProfileModel.getUserId(), userProfile.getUserId());
            });

            if (userProfile.getLinkFacebook() != null) {
                setSocialImageButton(R.drawable.im_facebook_link_profile, userProfile.getLinkFacebook(), ExternalSocialsUtil.facebookPackageName,
                        ExternalSocialsUtil.defaultFacebookWeb, ExternalSocialsUtil.defaultFacebookMobile, fragment, linearUserSocials);
            }

            if (userProfile.getLinkInstagram() != null) {
                setSocialImageButton(R.drawable.im_instagram_link_profile, userProfile.getLinkInstagram(), ExternalSocialsUtil.instagramPackageName,
                        ExternalSocialsUtil.defaultInstagramWeb, ExternalSocialsUtil.defaultInstagramMobile, fragment, linearUserSocials);
            }

            if (userProfile.getLinkTikTok() != null) {
                setSocialImageButton(R.drawable.im_tiktok_link_profile, userProfile.getLinkTikTok(), ExternalSocialsUtil.tikTokPackageName,
                        ExternalSocialsUtil.defaultTikTokWeb, ExternalSocialsUtil.defaultTikTokMobile, fragment, linearUserSocials);
            }

            if (userProfile.getLinkTelegram() != null) {
                setSocialImageButton(R.drawable.im_telegram_link_profile, userProfile.getLinkTelegram(), ExternalSocialsUtil.telegramPackageName,
                        ExternalSocialsUtil.defaultTelegramWeb, ExternalSocialsUtil.defaultTelegramMobile, fragment, linearUserSocials);
            }
            return popupView;
        }

        private View setupViewEventParticipants(Fragment fragment, List<CommonUserModel> userProfiles) {
            View popupView = LayoutInflater.from(fragment.getContext()).inflate(R.layout.popup_event_participants, null, false);
            RecyclerView recyclerParticipants = popupView.findViewById(R.id.recycler_event_participants);
            LinearLayoutManager layoutManager = new LinearLayoutManager(fragment.getContext());
            recyclerParticipants.setLayoutManager(layoutManager);
            PopupEventParticipantsAdapter participantsAdapter = new PopupEventParticipantsAdapter(fragment, userProfiles);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(recyclerParticipants.getContext(),
                    layoutManager.getOrientation());
            recyclerParticipants.addItemDecoration(dividerItemDecoration);
            recyclerParticipants.setAdapter(participantsAdapter);
            return popupView;
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
}
