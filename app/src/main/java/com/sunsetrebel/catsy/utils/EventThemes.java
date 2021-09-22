package com.sunsetrebel.catsy.utils;
import android.app.Application;
import android.content.Context;
import android.content.res.Resources;

import androidx.fragment.app.Fragment;

import com.sunsetrebel.catsy.R;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;

public class EventThemes extends Application {
    Resources resources;
    Map<Enum<?>, String> eventThemesList;

    public EventThemes(Resources resources) {
        this.resources = resources;
    }

    public enum eventThemes {
        SPORT, MUSIC, ART, QUEST, HOOKAH, SHOPPING, FILM, BIRTHDAY, WALKING, JOB, JOGGING,
        WORKOUT, TATTOO, HEALTH, PETS, FISHING, CARS, TRAVEL, EXCURSION, GAMES, ALCOHOL,
        ROCKNROLL, EXHIBITION, FAIR, COMEDY, PARTY, ROMANCE, KIDS, FASHION, PHOTO, TECHNOLOGY,
        ANIME, RELIGION, DANCE, LGBTQ, STUDY, BOOKS
    }

    public Map<Enum<?>, String> getEventThemesList() {
        if (eventThemesList == null) {
            Map<Enum<?>, String> aMap = new HashMap<>();
            aMap.put(eventThemes.SPORT, resources.getString(R.string.event_theme_sport));
            aMap.put(eventThemes.MUSIC, resources.getString(R.string.event_theme_music));
            aMap.put(eventThemes.ART, resources.getString(R.string.event_theme_art));
            aMap.put(eventThemes.QUEST, resources.getString(R.string.event_theme_quest));
            aMap.put(eventThemes.HOOKAH, resources.getString(R.string.event_theme_hookah));
            aMap.put(eventThemes.SHOPPING, resources.getString(R.string.event_theme_shopping));
            aMap.put(eventThemes.FILM, resources.getString(R.string.event_theme_film));
            aMap.put(eventThemes.BIRTHDAY, resources.getString(R.string.event_theme_birthday));
            aMap.put(eventThemes.WALKING, resources.getString(R.string.event_theme_walking));
            aMap.put(eventThemes.JOB, resources.getString(R.string.event_theme_job));
            aMap.put(eventThemes.JOGGING, resources.getString(R.string.event_theme_jogging));
            aMap.put(eventThemes.WORKOUT, resources.getString(R.string.event_theme_workout));
            aMap.put(eventThemes.TATTOO, resources.getString(R.string.event_theme_tattoo));
            aMap.put(eventThemes.HEALTH, resources.getString(R.string.event_theme_health));
            aMap.put(eventThemes.PETS, resources.getString(R.string.event_theme_pets));
            aMap.put(eventThemes.FISHING, resources.getString(R.string.event_theme_fishing));
            aMap.put(eventThemes.CARS, resources.getString(R.string.event_theme_cars));
            aMap.put(eventThemes.TRAVEL, resources.getString(R.string.event_theme_travel));
            aMap.put(eventThemes.EXCURSION, resources.getString(R.string.event_theme_excursion));
            aMap.put(eventThemes.GAMES, resources.getString(R.string.event_theme_games));
            aMap.put(eventThemes.ALCOHOL, resources.getString(R.string.event_theme_alcohol));
            aMap.put(eventThemes.ROCKNROLL, resources.getString(R.string.event_theme_rocknroll));
            aMap.put(eventThemes.EXHIBITION, resources.getString(R.string.event_theme_exhibition));
            aMap.put(eventThemes.FAIR, resources.getString(R.string.event_theme_fair));
            aMap.put(eventThemes.COMEDY, resources.getString(R.string.event_theme_comedy));
            aMap.put(eventThemes.PARTY, resources.getString(R.string.event_theme_party));
            aMap.put(eventThemes.ROMANCE, resources.getString(R.string.event_theme_romance));
            aMap.put(eventThemes.KIDS, resources.getString(R.string.event_theme_kids));
            aMap.put(eventThemes.FASHION, resources.getString(R.string.event_theme_fashion));
            aMap.put(eventThemes.PHOTO, resources.getString(R.string.event_theme_photo));
            aMap.put(eventThemes.TECHNOLOGY, resources.getString(R.string.event_theme_technology));
            aMap.put(eventThemes.ANIME, resources.getString(R.string.event_theme_anime));
            aMap.put(eventThemes.RELIGION, resources.getString(R.string.event_theme_religion));
            aMap.put(eventThemes.DANCE, resources.getString(R.string.event_theme_dance));
            aMap.put(eventThemes.LGBTQ, resources.getString(R.string.event_theme_lgbtq));
            aMap.put(eventThemes.STUDY, resources.getString(R.string.event_theme_study));
            aMap.put(eventThemes.BOOKS, resources.getString(R.string.event_theme_books));
            eventThemesList = Collections.unmodifiableMap(aMap);
        }
        return eventThemesList;
    }

    public static <T, E> T getKeyByValue(Map<T, E> map, E value) {
        for (Map.Entry<T, E> entry : map.entrySet()) {
            if (Objects.equals(value, entry.getValue())) {
                return entry.getKey();
            }
        }
        return null;
    }
}
