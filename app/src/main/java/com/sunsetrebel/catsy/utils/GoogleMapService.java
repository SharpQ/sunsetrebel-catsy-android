package com.sunsetrebel.catsy.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.ImageDecoder;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.location.Geocoder;
import android.location.Location;
import android.media.ThumbnailUtils;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.drawable.RoundedBitmapDrawable;
import androidx.core.graphics.drawable.RoundedBitmapDrawableFactory;
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
import com.sunsetrebel.catsy.models.EventModel;

import java.util.Locale;

public class GoogleMapService {
    private static Geocoder geocoder = null;
    private static FusedLocationProviderClient fusedLocationProviderClient;
    public static Bitmap mapMarkerDefault;

    public static Geocoder getGeocoderInstance(Context context) {
        return geocoder = new Geocoder(context, Locale.getDefault());
    }

    public static FusedLocationProviderClient getFusedLocationProviderInstance(Context context) {
        if (fusedLocationProviderClient == null) {
            fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(context);
        }
        return fusedLocationProviderClient;
    }

    @SuppressLint("MissingPermission")
    public static void setupMap(GoogleMap googleMap, Context context, Boolean shouldZoomToUser, Boolean isActivity, Object screen) {
        //MAP STYLE AND BUTTONS SETUP
        googleMap.setMapStyle(MapStyleOptions.loadRawResourceStyle(
                context, R.raw.google_style));
        googleMap.getUiSettings().setMyLocationButtonEnabled(false);
        googleMap.getUiSettings().setMapToolbarEnabled(false);
        googleMap.getUiSettings().setCompassEnabled(false);
//        mMap.getUiSettings().setRotateGesturesEnabled(false);
        if (shouldZoomToUser) {
            if (PermissionUtils.isLocationPermissionEnabled(context)) {
                zoomToUserLocation(context, googleMap);
            } else {
                PermissionUtils.requestLocationPermissions(isActivity, screen);
            }
        }
    }

    @SuppressLint("MissingPermission")
    public static void zoomToUserLocation(Context context, GoogleMap googleMap) {
        googleMap.setMyLocationEnabled(true);
        getFusedLocationProviderInstance(context);
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
            if (location != null) {
                LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
                googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 12));
            }
        });
    }

    public static void clearAndSetMarker(GoogleMap googleMap, LatLng eventLatLng, float zoom, String marketTitle, Context context) {
        if (mapMarkerDefault == null) {
            initMarkerDrawable(context);
        }
        googleMap.clear();
//        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLatLng, zoom));
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(eventLatLng, zoom));
        googleMap.addMarker(new MarkerOptions().position(eventLatLng).icon(BitmapDescriptorFactory.fromBitmap(mapMarkerDefault)).title(marketTitle)).showInfoWindow();
    }

    public static void setEventMarker(GoogleMap googleMap, EventModel eventModel, Context context) {
        if (context != null) {
            ImageUtils.loadBitmapMapIcons(scaledBitmap -> {
                googleMap.addMarker(new MarkerOptions().position(eventModel.getEventGeoLocation()).icon(BitmapDescriptorFactory.fromBitmap(scaledBitmap))).setTag(eventModel); //.title(eventModel.getEventTitle()).snippet(context.getString(R.string.event_list_host_placeholder) + eventModel.getHostName())
            }, context, eventModel.getEventAvatar());
        }
    }

    private static void initMarkerDrawable(Context context) {
        Bitmap errowDrawable = ((BitmapDrawable) ContextCompat.getDrawable(context,
                R.drawable.im_cat_market_default_512p)).getBitmap();
        mapMarkerDefault = Bitmap.createScaledBitmap(errowDrawable, 100, 100, true);
    }
}
