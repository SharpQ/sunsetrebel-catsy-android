package com.sunsetrebel.catsy.utils;

import android.content.Context;
import android.media.MediaPlayer;
import com.sunsetrebel.catsy.R;
import java.util.HashSet;
import java.util.Set;

public class MediaPlayerService {
    private static Set<MediaPlayer> activePlayers = new HashSet<MediaPlayer>();
    public static void playIntro(Context context) {
        final MediaPlayer mp = MediaPlayer.create(context, R.raw.catsy_intro);
        activePlayers.add(mp);
        mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                releaseMP(mp);
                activePlayers.remove(mp);
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
