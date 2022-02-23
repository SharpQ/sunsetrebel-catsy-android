package com.sunsetrebel.catsy.fragments;

import android.os.Bundle;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.constraintlayout.widget.ConstraintSet;
import androidx.fragment.app.Fragment;

import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.models.MainUserProfileModel;
import com.sunsetrebel.catsy.repositories.UserProfileService;
import com.sunsetrebel.catsy.utils.ExternalSocialsUtil;

public class ProfilePersonalInfoFragment extends Fragment {
    private TextView profileEmail, profilePhone;
    private LinearLayout linearLayout;
    private static MainUserProfileModel mainUserProfileModel;
    private LinearLayout linearLayoutExtra;
    private ConstraintLayout rootConstraintLayout;
    private int imageSizeDP;


    public ProfilePersonalInfoFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_profile_personal_info, container, false);
        rootConstraintLayout = v.findViewById(R.id.cl_root);
        profileEmail = v.findViewById(R.id.tv_email_value);
        profilePhone = v.findViewById(R.id.tv_phone_value);
        linearLayout = v.findViewById(R.id.ll_socials);

        UserProfileService userProfileService = UserProfileService.getInstance();
        mainUserProfileModel = userProfileService.getUserProfile();

        String userEmail = mainUserProfileModel.getUserEmail();
        String userPhone = mainUserProfileModel.getUserPhone();
        String userIdFacebook = mainUserProfileModel.getLinkFacebook();
        String userIdInstagram = mainUserProfileModel.getLinkInstagram();
        String userIdTikTok = mainUserProfileModel.getLinkTikTok();
        String userIdTelegram = mainUserProfileModel.getLinkTelegram();
        setTextViewInfo(userEmail, profileEmail);
        setTextViewInfo(userPhone, profilePhone);
        imageSizeDP = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 55, getResources().getDisplayMetrics());

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
        imageButton.setBackgroundColor(getResources().getColor(R.color.primaryDarkColor));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageSizeDP, imageSizeDP);
        params.setMargins(10,0,10,0);
        imageButton.setLayoutParams(params);
        imageButton.setAdjustViewBounds(true);
        imageButton.setScaleType(ImageView.ScaleType.FIT_XY);
        imageButton.setPadding(0,0,0,0);

        if (linearLayout.getChildCount() < 4) {
            linearLayout.addView(imageButton);
        } else {
            addToExtraLinear(imageButton);
        }

        imageButton.setOnClickListener(v -> ExternalSocialsUtil.openLink(getContext(), userId, packageName, defaultWebLink, defaultMobileLink));
    }

    private void addToExtraLinear(ImageButton imageButton) {
        if (linearLayoutExtra == null) {
            setUpNewLinear();
        }
        linearLayoutExtra.addView(imageButton);
    }

    private void setUpNewLinear() {
        linearLayoutExtra = new LinearLayout(getContext());
        LinearLayout.LayoutParams paramsExtraLinear = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.WRAP_CONTENT, imageSizeDP);
        paramsExtraLinear.setMargins(10, 5, 10, 5);
        linearLayoutExtra.setLayoutParams(paramsExtraLinear);
        linearLayoutExtra.setGravity(LinearLayout.HORIZONTAL);
        linearLayoutExtra.setOrientation(LinearLayout.HORIZONTAL);
        linearLayoutExtra.setId(View.generateViewId());
        rootConstraintLayout.addView(linearLayoutExtra);
        ConstraintSet constraintSet = new ConstraintSet();
        constraintSet.clone(rootConstraintLayout);
        constraintSet.connect(linearLayoutExtra.getId(), ConstraintSet.TOP, linearLayout.getId(),
                ConstraintSet.BOTTOM, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 10, getResources().getDisplayMetrics()));
        constraintSet.connect(linearLayoutExtra.getId(), ConstraintSet.BOTTOM, rootConstraintLayout.getId(), ConstraintSet.BOTTOM,5);
        constraintSet.connect(linearLayoutExtra.getId(), ConstraintSet.START, rootConstraintLayout.getId(), ConstraintSet.START,0);
        constraintSet.connect(linearLayoutExtra.getId(), ConstraintSet.END, rootConstraintLayout.getId(), ConstraintSet.END,0);
        constraintSet.setVerticalBias(linearLayoutExtra.getId(), 0f);
        constraintSet.setHorizontalBias(linearLayoutExtra.getId(), 0.5f);
        constraintSet.applyTo(rootConstraintLayout);
    }

    private void setTextViewInfo(String info, TextView tv) {
        if (info != null) {
            tv.setText(info);
        } else {
            tv.setText("N/A");
        }
    }
}
