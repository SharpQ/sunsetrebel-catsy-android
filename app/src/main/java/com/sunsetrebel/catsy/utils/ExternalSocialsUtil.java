package com.sunsetrebel.catsy.utils;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.List;

public class ExternalSocialsUtil {
    public static final String defaultInstagramWeb = "http://instagram.com/";
    public static final String defaultInstagramMobile = "http://instagram.com/_u/";
    public static final String defaultFacebookWeb = "https://m.facebook.com/";
    public static final String defaultFacebookMobile = "fb://page/";
    public static final String defaultTelegramWeb = "https://t.me/";
    public static final String defaultTelegramMobile = "https://t.me/";
    public static final String defaultTikTokWeb = "https://www.tiktok.com/@";
    public static final String defaultTikTokMobile = "https://www.tiktok.com/@";
    public static final String facebookPackageName = "com.facebook.katana";
    public static final String telegramPackageName = "org.telegram.messenger";
    public static final String instagramPackageName = "com.instagram.android";
    public static final String tikTokPackageName = "com.zhiliaoapp.musically";

    private static boolean isIntentAvailable(Intent intent, Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return list.size() > 0;
    }

    public static void openLink(Context context, String userId, String packageName, String defaultWebLink, String defaultMobileLink) {
        Uri uri = Uri.parse(defaultMobileLink + userId);
        Intent intent = new Intent(Intent.ACTION_VIEW, uri);
        intent.setPackage(packageName);

        if (isIntentAvailable(intent, context)) {
            context.startActivity(intent);
        } else {
            context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(defaultWebLink + userId)));
        }
    }
}
