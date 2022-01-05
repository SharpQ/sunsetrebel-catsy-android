package com.sunsetrebel.catsy.utils;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.Toast;

import androidx.core.content.res.ResourcesCompat;

import com.sunsetrebel.catsy.R;

import es.dmoral.toasty.Toasty;

public class CustomToastUtil {
    public static CustomToastUtil customToastUtilInstance;

    public CustomToastUtil(Context context) {
        Typeface typeface = ResourcesCompat.getFont(context, R.font.audiowide);
        Toasty.Config.getInstance()
                .tintIcon(false)
                .setToastTypeface(typeface)
                .setTextSize(12)
                .allowQueue(false)
                .apply();
    }

    public static void showSuccessToast(Context context, String toastText) {
        if (customToastUtilInstance == null) {
            customToastUtilInstance = new CustomToastUtil(context);
        }
        Toasty.custom(context, toastText, R.drawable.im_kitty_toast_success_64, R.color.successToastColor,
                Toast.LENGTH_SHORT, true, true).show();
    }

    public static void showFailToast(Context context, String toastText) {
        if (customToastUtilInstance == null) {
            customToastUtilInstance = new CustomToastUtil(context);
        }
        Toasty.custom(context, toastText, R.drawable.im_kitty_toast_fail_64, R.color.redError,
                Toast.LENGTH_SHORT, true, true).show();
    }
}
