package com.sunsetrebel.catsy.utils;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.location.Geocoder;

import androidx.core.content.ContextCompat;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
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
            if (PermissionUtil.isLocationPermissionEnabled(context)) {
                zoomToUserLocation(context, googleMap);
            } else {
                PermissionUtil.requestLocationPermissions(isActivity, screen);
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
            ImageUtil.loadBitmapMapIcons(scaledBitmap -> {
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
