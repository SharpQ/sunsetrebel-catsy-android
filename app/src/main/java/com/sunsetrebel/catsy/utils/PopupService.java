package com.sunsetrebel.catsy.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.fragment.app.Fragment;

import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.enums.PopupType;
import com.sunsetrebel.catsy.models.EventModel;

import java.text.SimpleDateFormat;
import java.util.Locale;

public class PopupService {
    private static PopupService instance;
    private PopupWindow infoPopup;
    private SimpleDateFormat simpleDateFormat;
    private EventThemesUtil eventThemesUtil;

    public PopupService(Context context) {
        simpleDateFormat = new SimpleDateFormat("HH:mm d MMM ''yy", Locale.getDefault());
        eventThemesUtil = EventThemesUtil.getInstance(context.getResources());
    }

    public static PopupService getInstance(Context context) {
        if (instance == null) {
            instance = new PopupService(context);
        }
        return instance;
    }

    public void showPopupMapFragment(Fragment fragment, EventModel eventModel, PopupType popupType,
                                     Integer customWidth, Integer customHeight, int animationStyle, int gravity) {
        closePopup();
        View popupView = null;
        switch (popupType) {
            case EVENT_MAPS:
                popupView = setupViewMapsFragment(fragment, eventModel);
                break;
            case USER_EVENT_DETAILED:
                popupView = setupViewUserEventDetailed(fragment);
                break;
            default:
                break;
        }

        infoPopup = new PopupWindow(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        if (customWidth != null) {
            infoPopup.setWidth(customWidth);
        }
        if (customHeight != null) {
            infoPopup.setHeight(customHeight);
        }
        infoPopup.setAnimationStyle(animationStyle);
        infoPopup.showAtLocation(fragment.getView(), gravity, 0, 0);
    }

    private View setupViewMapsFragment(Fragment fragment, EventModel eventModel) {
        LayoutInflater layoutInflater = LayoutInflater.from(fragment.getActivity());
        View popupView = layoutInflater.inflate(R.layout.item_event_map_fragment, null);
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

    public View setupViewUserEventDetailed(Fragment fragment) {
        LayoutInflater layoutInflater = LayoutInflater.from(fragment.getActivity());
        View popupView = layoutInflater.inflate(R.layout.item_event_map_fragment, null);
        return popupView;
    }

    public void closePopup() {
        if (infoPopup != null) {
            infoPopup.dismiss();
        }
    }
}
