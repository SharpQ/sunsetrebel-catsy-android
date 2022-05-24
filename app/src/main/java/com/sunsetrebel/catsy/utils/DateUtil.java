package com.sunsetrebel.catsy.utils;

import android.text.format.DateUtils;

import com.google.firebase.Timestamp;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DateUtil {
    public static String dateToString(Date date) {
       return new SimpleDateFormat("HH:mm \u2022 d MMM ''yy", Locale.getDefault()).format(date);
    }

    public static String timestampToString(Timestamp timestamp) {
        if (DateUtils.isToday(timestamp.toDate().getTime())
                || DateUtils.isToday(timestamp.toDate().getTime() + DateUtils.DAY_IN_MILLIS)) {
            //if today
            return DateUtils.getRelativeTimeSpanString(timestamp.toDate().getTime()).toString();
        } else {
            return new SimpleDateFormat("d MMM", Locale.getDefault()).format(timestamp.toDate());
        }
    }
}
