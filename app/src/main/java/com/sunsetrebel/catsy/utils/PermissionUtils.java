package com.sunsetrebel.catsy.utils;

import android.Manifest;
import android.app.Activity;
import android.content.pm.PackageManager;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.Fragment;

public class PermissionUtils extends Fragment {
    private final static int ACCESS_LOCATION_REQUEST_CODE = 10001;
    private final static int ACCESS_GALLERY_REQUEST_CODE = 1001;

    public static final int getAccessLocationRequestCode() {
        return ACCESS_LOCATION_REQUEST_CODE;
    }

    public static final int getAccessGalleryRequestCode() {
        return ACCESS_GALLERY_REQUEST_CODE;
    }

    public static boolean isLocationPermissionEnabled(android.content.Context context) {
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED ||
                ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return false;
        } else {
            return true;
        }
    }

    public static boolean isGalleryPermissionEnabled(android.content.Context context) {
        return ActivityCompat.checkSelfPermission(context, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED;
    }

    public static void requestLocationPermissions(Boolean isActivity, Object screen) {
        if (isActivity) {
            ActivityCompat.requestPermissions((Activity) screen, new String[]{Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
        } else {
            Fragment fragment = (Fragment) screen;
            fragment.requestPermissions(new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
        }
    }

    public static void requestGalleryPermissionsFragment(Fragment fragment) {
        fragment.requestPermissions(new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, PermissionUtils.getAccessGalleryRequestCode());
    }
}
