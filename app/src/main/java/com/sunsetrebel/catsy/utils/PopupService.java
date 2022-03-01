package com.sunsetrebel.catsy.utils;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.models.EventModel;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

public class PopupService {
    private static PopupService instance;
    private PopupWindow infoPopup;
    private SimpleDateFormat simpleDateFormat;
    private Map<Enum<?>, String> eventThemesEnumList;

    public PopupService(Context context) {
        simpleDateFormat = new SimpleDateFormat("HH:mm d MMM ''yy", Locale.getDefault());
        eventThemesEnumList = EventThemesUtil.getEventThemesList(context.getResources());
    }

    public static PopupService getInstance(Context context) {
        if (instance == null) {
            instance = new PopupService(context);
        }
        return instance;
    }

    public void showPopupMapFragment(View view, EventModel eventModel, Fragment fragment) {
        closePopup();
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

        List<EventThemes> eventThemes = eventModel.getEventThemes();
        if (eventThemes != null) {
            Random rand = new Random();
            for (EventThemes theme : eventThemes) {
                TextView tv = new TextView(fragment.getContext());
                tv.setText("#" + eventThemesEnumList.get(theme));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                params.setMargins(0,0,(int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
                        fragment.getContext().getResources().getDisplayMetrics()),0);
                tv.setLayoutParams(params);
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                tv.setTextSize(14);
                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    tv.setTextColor(Color.rgb(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()));
                } else {
                    tv.setTextColor(fragment.getContext().getResources().getColor(R.color.primaryTextColor));
                }
                Typeface typeface = ResourcesCompat.getFont(fragment.getContext(), R.font.audiowide);
                tv.setTypeface(typeface);
                linearLayout.addView(tv);
            }
        }

        infoPopup = new PopupWindow(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        Display display = fragment.getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        infoPopup.setWidth(width - (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5,
                fragment.getContext().getResources().getDisplayMetrics()));
        infoPopup.setAnimationStyle(R.style.popup_window_animation);
        infoPopup.showAtLocation(view, Gravity.TOP, 0, 0);
    }

    public void closePopup() {
        if (infoPopup != null) {
            infoPopup.dismiss();
        }
    }
}
