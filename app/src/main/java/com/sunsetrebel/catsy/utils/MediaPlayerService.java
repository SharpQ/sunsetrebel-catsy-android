package com.sunsetrebel.catsy.utils;

import android.content.Context;
import android.media.MediaPlayer;

import com.sunsetrebel.catsy.R;

public class MediaPlayerService {
    public static void playIntro(Context context) {
        final MediaPlayer mp = MediaPlayer.create(context, R.raw.catsy_intro);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                releaseMP(mp);
            }
        });
        mp.start();
    }

    public static void playNavigation(Context context) {
        final MediaPlayer mp = MediaPlayer.create(context, R.raw.catsy_murr);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                releaseMP(mp);
            }
        });
        mp.start();
    }

    private static void releaseMP(MediaPlayer mp) {
        mp.reset();
        mp.release();
    }
}
