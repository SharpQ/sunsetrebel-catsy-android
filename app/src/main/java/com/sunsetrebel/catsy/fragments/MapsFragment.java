package com.sunsetrebel.catsy.fragments;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.utils.GoogleMapService;
import com.sunsetrebel.catsy.utils.PermissionUtils;


public class MapsFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    public MapsFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_maps, container, false);
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.fragmentGoogleMaps);
        mapFragment.getMapAsync(this);
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        GoogleMapService.setupMap(googleMap, getContext(), MapsFragment.this);
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtils.getAccessLocationRequestCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                GoogleMapService.zoomToUserLocation(getContext(), mMap);
            } else {
                Log.e("INFO", "Permissions not granted");
            }
        }
    }

}
