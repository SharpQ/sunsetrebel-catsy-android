package com.sunsetrebel.catsy.utils;

import android.Manifest;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.location.Geocoder;
import android.location.Location;
import android.os.Looper;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.firestore.FirebaseFirestore;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.fragments.MapsFragment;

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

    @SuppressLint("MissingPermission")
    public static void clearAndSetMarker(LatLng eventLatLng, GoogleMap googleMap, float zoom) {
        googleMap.clear();
        googleMap.moveCamera(CameraUpdateFactory.newLatLngZoom(eventLatLng, zoom));
//        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(eventLatLng, zoom));
        googleMap.addMarker(new MarkerOptions().position(eventLatLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.im_cat_location_sample_35)));
    }
}
