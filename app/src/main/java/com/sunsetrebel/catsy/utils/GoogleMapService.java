package com.sunsetrebel.catsy.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.bumptech.glide.request.target.CustomViewTarget;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.sunsetrebel.catsy.R;

import java.util.Locale;

public class GoogleMapService {
    private static Geocoder geocoder = null;
    private static FusedLocationProviderClient fusedLocationProviderClient = null;

    public static Geocoder getGeocoderInstance(Context context) {
        return geocoder = new Geocoder(context, Locale.getDefault());
    }

    private static FusedLocationProviderClient getFusedLocationProviderInstance(Context context) {
        return fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
    }

    @SuppressLint("MissingPermission")
    public static void setupMap(GoogleMap googleMap, Context context, Fragment fragment) {
        //MAP STYLE AND BUTTONS SETUP
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                context, R.raw.google_style));
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);
//        mMap.getUiSettings().setRotateGesturesEnabled(false);

        if (PermissionUtils.isLocationPermissionEnabled(context)) {
            zoomToUserLocation(context, googleMap);
        } else {
            PermissionUtils.requestLocationPermissionsFragment(fragment);
        }
    }

    @SuppressLint("MissingPermission")
    public static void setupMapActivity(GoogleMap googleMap, Context context, Activity activity) {
        //MAP STYLE AND BUTTONS SETUP
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                context, R.raw.google_style));
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);

        if (PermissionUtils.isLocationPermissionEnabled(context)) {
            zoomToUserLocation(context, googleMap);
        } else {
            PermissionUtils.requestLocationPermissionsActivity(activity);
        }
    }

    @SuppressLint("MissingPermission")
    public static void zoomToUserLocation(Context context, GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        getFusedLocationProviderInstance(context);
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null) {
                    LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                    googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
                }
            }
        });
    }

    public static void clearAndSetMarker(GoogleMap googleMap, LatLng eventLatLng, float zoom, String marketTitle) {
        googleMap.clear();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLatLng, zoom));
//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(eventLatLng, zoom));
        googleMap.addMarker(new MarkerOptions().position(eventLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.im_cat_location_sample_35)).title(marketTitle)).showInfoWindow();
    }

    public static void setEventMarker(GoogleMap googleMap, LatLng eventLatLng, String marketTitle, String marketSubTitle, String userAvatarURL, Context context) {
       Glide.with(context)
                .asBitmap()
                .load(userAvatarURL)
                .apply(RequestOptions.circleCropTransform())
                .into(new SimpleTarget<Bitmap>() {
                    @Override
                    public void onLoadFailed(@Nullable Drawable errorDrawable) {
                        googleMap.addMarker(new MarkerOptions().position(eventLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.im_cat_location_sample_35)).title(marketTitle).snippet(marketSubTitle));
                    }

                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        final float scale = context.getResources().getDisplayMetrics().density;
                        int pixels = (int) (35 * scale + 0.5f);
                        Bitmap bitmap = Bitmap.createScaledBitmap(resource, pixels, pixels, true);
                        googleMap.addMarker(new MarkerOptions().position(eventLatLng).icon(BitmapDescriptorFactory.fromBitmap(bitmap)).title(marketTitle).snippet(marketSubTitle));
                    }
                });
    }
}
