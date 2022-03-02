package com.sunsetrebel.catsy.utils;
import android.app.Application;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.enums.EventThemes;

import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Random;

public class EventThemesUtil extends Application {
    private static EventThemesUtil instance;
    private static Map<Enum<?>, String> eventThemesMap;

    public EventThemesUtil(Resources resources) {
        initEventThemesMap(resources);
    }

    public static EventThemesUtil getInstance(Resources resources) {
        if (instance == null) {
            instance = new EventThemesUtil(resources);
        }
        return instance;
    }

    private void initEventThemesMap(Resources resources) {
        if (eventThemesMap == null) {
            Map<Enum<?>, String> aMap = new HashMap<>();
            aMap.put(EventThemes.SPORT, resources.getString(R.string.event_theme_sport));
            aMap.put(EventThemes.MUSIC, resources.getString(R.string.event_theme_music));
            aMap.put(EventThemes.ART, resources.getString(R.string.event_theme_art));
            aMap.put(EventThemes.QUEST, resources.getString(R.string.event_theme_quest));
            aMap.put(EventThemes.HOOKAH, resources.getString(R.string.event_theme_hookah));
            aMap.put(EventThemes.SHOPPING, resources.getString(R.string.event_theme_shopping));
            aMap.put(EventThemes.FILM, resources.getString(R.string.event_theme_film));
            aMap.put(EventThemes.BIRTHDAY, resources.getString(R.string.event_theme_birthday));
            aMap.put(EventThemes.WALKING, resources.getString(R.string.event_theme_walking));
            aMap.put(EventThemes.JOB, resources.getString(R.string.event_theme_job));
            aMap.put(EventThemes.JOGGING, resources.getString(R.string.event_theme_jogging));
            aMap.put(EventThemes.WORKOUT, resources.getString(R.string.event_theme_workout));
            aMap.put(EventThemes.TATTOO, resources.getString(R.string.event_theme_tattoo));
            aMap.put(EventThemes.HEALTH, resources.getString(R.string.event_theme_health));
            aMap.put(EventThemes.PETS, resources.getString(R.string.event_theme_pets));
            aMap.put(EventThemes.FISHING, resources.getString(R.string.event_theme_fishing));
            aMap.put(EventThemes.CARS, resources.getString(R.string.event_theme_cars));
            aMap.put(EventThemes.TRAVEL, resources.getString(R.string.event_theme_travel));
            aMap.put(EventThemes.EXCURSION, resources.getString(R.string.event_theme_excursion));
            aMap.put(EventThemes.GAMES, resources.getString(R.string.event_theme_games));
            aMap.put(EventThemes.ALCOHOL, resources.getString(R.string.event_theme_alcohol));
            aMap.put(EventThemes.ROCKNROLL, resources.getString(R.string.event_theme_rocknroll));
            aMap.put(EventThemes.EXHIBITION, resources.getString(R.string.event_theme_exhibition));
            aMap.put(EventThemes.FAIR, resources.getString(R.string.event_theme_fair));
            aMap.put(EventThemes.COMEDY, resources.getString(R.string.event_theme_comedy));
            aMap.put(EventThemes.PARTY, resources.getString(R.string.event_theme_party));
            aMap.put(EventThemes.ROMANCE, resources.getString(R.string.event_theme_romance));
            aMap.put(EventThemes.KIDS, resources.getString(R.string.event_theme_kids));
            aMap.put(EventThemes.FASHION, resources.getString(R.string.event_theme_fashion));
            aMap.put(EventThemes.PHOTO, resources.getString(R.string.event_theme_photo));
            aMap.put(EventThemes.TECHNOLOGY, resources.getString(R.string.event_theme_technology));
            aMap.put(EventThemes.ANIME, resources.getString(R.string.event_theme_anime));
            aMap.put(EventThemes.RELIGION, resources.getString(R.string.event_theme_religion));
            aMap.put(EventThemes.DANCE, resources.getString(R.string.event_theme_dance));
            aMap.put(EventThemes.LGBTQ, resources.getString(R.string.event_theme_lgbtq));
            aMap.put(EventThemes.STUDY, resources.getString(R.string.event_theme_study));
            aMap.put(EventThemes.BOOKS, resources.getString(R.string.event_theme_books));
            eventThemesMap = Collections.unmodifiableMap(aMap);
        }
    }

    public Map<Enum<?>, String> getEventThemesMap() {
        return eventThemesMap;
    }

    public <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }

    public void setEventThemesUI(List<EventThemes> eventThemes, Fragment fragment,
                                 LinearLayout linearLayout, Integer textLimit) {
        if (eventThemes != null) {
            Random rand = new Random();
            int tvCurrentSize = 0;
            for (EventThemes theme : eventThemes) {
                tvCurrentSize = tvCurrentSize + theme.toString().length();
                TextView tv = new TextView(fragment.getContext());
                tv.setText("#" + eventThemesMap.get(theme));
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
                if (textLimit == null) {
                    linearLayout.addView(tv);
                } else if (textLimit != null && tvCurrentSize < textLimit) {
                    linearLayout.addView(tv);
                }
            }
        }
    }
}
