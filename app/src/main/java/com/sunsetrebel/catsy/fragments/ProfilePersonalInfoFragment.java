package com.sunsetrebel.catsy.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.models.UserProfileModel;
import com.sunsetrebel.catsy.repositories.UserProfileService;
import com.sunsetrebel.catsy.utils.ExternalSocialsUtil;

import java.util.List;

public class ProfilePersonalInfoFragment extends Fragment {
    private TextView profileEmail, profilePhone;
    private LinearLayout linearLayout;
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
        linearLayout = v.findViewById(R.id.ll_socials);

        UserProfileService userProfileService = UserProfileService.getInstance();
        userProfileModel = userProfileService.getUserProfile();

        String userEmail = userProfileModel.getUserEmail();
        String userPhone = userProfileModel.getUserPhone();
        String userIdFacebook = userProfileModel.getLinkFacebook();
        String userIdInstagram = userProfileModel.getLinkInstagram();
        String userIdTikTok = userProfileModel.getLinkTikTok();
        String userIdTelegram = userProfileModel.getLinkTelegram();
        setTextViewInfo(userEmail, profileEmail);
        setTextViewInfo(userPhone, profilePhone);

        if (userIdFacebook != null) {
            setSocialImageButton(R.drawable.im_facebook_link_profile, userIdFacebook, ExternalSocialsUtil.facebookPackageName,
                    ExternalSocialsUtil.defaultFacebookWeb, ExternalSocialsUtil.defaultFacebookMobile);
        }

        if (userIdInstagram != null) {
            setSocialImageButton(R.drawable.im_instagram_link_profile, userIdInstagram, ExternalSocialsUtil.instagramPackageName,
                    ExternalSocialsUtil.defaultInstagramWeb, ExternalSocialsUtil.defaultInstagramMobile);
        }

        if (userIdTikTok != null) {
            setSocialImageButton(R.drawable.im_tiktok_link_profile, userIdTikTok, ExternalSocialsUtil.tikTokPackageName,
                    ExternalSocialsUtil.defaultTikTokWeb, ExternalSocialsUtil.defaultTikTokMobile);
        }

        if (userIdTelegram != null) {
            setSocialImageButton(R.drawable.im_telegram_link_profile, userIdTelegram, ExternalSocialsUtil.telegramPackageName,
                    ExternalSocialsUtil.defaultTelegramWeb, ExternalSocialsUtil.defaultTelegramMobile);
        }
        return v;
    }

    private void setSocialImageButton(int resId, String userId, String packageName, String defaultWebLink, String defaultMobileLink) {
        ImageButton imageButton = new ImageButton(getContext());
        imageButton.setImageResource(resId);
        imageButton.setBackgroundColor(getResources().getColor(R.color.primaryColor));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(160,160);
        params.setMargins(15,0,15,0);
        imageButton.setLayoutParams(params);
        imageButton.setAdjustViewBounds(true);
        imageButton.setScaleType(ImageView.ScaleType.FIT_XY);
        imageButton.setPadding(0,0,0,0);
        linearLayout.addView(imageButton);
        imageButton.setOnClickListener(v -> ExternalSocialsUtil.openLink(getContext(), userId, packageName, defaultWebLink, defaultMobileLink));
    }

    private void setTextViewInfo(String info, TextView tv) {
        if (info != null) {
            tv.setText(info);
        } else {
            tv.setText("N/A");
        }
    }
}
