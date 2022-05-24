package com.sunsetrebel.catsy.fragments;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Point;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.directions.route.AbstractRouting;
import com.directions.route.Route;
import com.directions.route.RouteException;
import com.directions.route.Routing;
import com.directions.route.RoutingListener;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.enums.PopupType;
import com.sunsetrebel.catsy.models.EventModel;
import com.sunsetrebel.catsy.repositories.FirebaseFirestoreService;
import com.sunsetrebel.catsy.utils.GoogleMapService;
import com.sunsetrebel.catsy.utils.PermissionUtil;
import com.sunsetrebel.catsy.utils.PopupService;

import java.util.ArrayList;
import java.util.List;


public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, RoutingListener {
    private GoogleMap mMap;
    private FirebaseFirestoreService firebaseFirestoreService;
    private FloatingActionButton fab;
    private List<Polyline> polylines = null;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private EventModel selectedEvent = null;
    private int width, height;

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

        fab = v.findViewById(R.id.fab_draw_route);
        fusedLocationProviderClient = GoogleMapService.getFusedLocationProviderInstance(getContext());
        firebaseFirestoreService = FirebaseFirestoreService.getInstance();
        getDisplaySize();

        fab.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("MissingPermission")
            @Override
            public void onClick(View v) {
                fusedLocationProviderClient.getLastLocation().addOnSuccessListener(location -> {
                    if (location != null && selectedEvent != null) {
                        drawPrimaryLinePath(new LatLng(location.getLatitude(), location.getLongitude()), selectedEvent.getEventGeoLocation());
                    }
                });
            }
        });
        return v;
    }

    private void getDisplaySize() {
        Display display = this.getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        width = size.x;
        height = size.y;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
        PopupService.closePopup();
        selectedEvent = null;
        fab.setEnabled(false);
        fab.setVisibility(View.INVISIBLE);
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        GoogleMapService.setupMap(googleMap, getContext(), true, false, MapsFragment.this);
        firebaseFirestoreService.getEventList(eventList -> {
            if (eventList != null) {
                for (EventModel event : eventList) {
                    GoogleMapService.setEventMarker(mMap, event, getContext());
                }
            }
        });
        mMap.setOnMapClickListener(latLng -> {
            PopupService.closePopup();
            selectedEvent = null;
            fab.setEnabled(false);
            fab.setVisibility(View.INVISIBLE);
        });

        mMap.setOnMarkerClickListener(marker -> {
            EventModel event = (EventModel) marker.getTag();
            selectedEvent = event;
            PopupService.showPopup(new PopupService.PopupBuilder(this, event,
                    PopupType.EVENT_MAPS, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
                    .animationStyle(R.style.popup_window_animation)
                    .setFocusable(false)
                    .build(), this, Gravity.TOP);
            clearPolylines();
            fab.setEnabled(true);
            fab.setVisibility(View.VISIBLE);
            return false;
        });
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtil.getAccessLocationRequestCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                GoogleMapService.zoomToUserLocation(getContext(), mMap);
            } else {
                Log.d("DEBUG", "Permissions not granted");
            }
        }
    }

    private void drawPrimaryLinePath(LatLng start, LatLng end) {
        if (mMap == null) {
            return;
        }
        clearPolylines();
        findroutes(start, end);
    }

    public void findroutes(LatLng Start, LatLng End) {
        if (Start==null || End==null) {
            return;
        } else {
            Routing routing = new Routing.Builder()
                    .travelMode(AbstractRouting.TravelMode.DRIVING)
                    .withListener(this)
                    .alternativeRoutes(true)
                    .waypoints(Start, End)
                    .key(getResources().getString(R.string.google_maps_key))  //also define your api key here.
                    .build();
            routing.execute();
        }
    }

    private void clearPolylines() {
        if (polylines != null) {
            for(Polyline line : polylines)
            {
                line.remove();
            }
            polylines.clear();
        }
    }

    @Override
    public void onRoutingSuccess(ArrayList<Route> route, int shortestRouteIndex) {
        PolylineOptions polyOptions = new PolylineOptions();
        polylines = new ArrayList<>();
        //add route(s) to the map using polyline
        for (int i = 0; i <route.size(); i++) {
            if (i==shortestRouteIndex)
            {
                polyOptions.color(getResources().getColor(R.color.primaryLightColor));
                polyOptions.width(7);
                polyOptions.addAll(route.get(shortestRouteIndex).getPoints());
                Polyline polyline = mMap.addPolyline(polyOptions);
                polylines.add(polyline);
            }
        }
    }

    @Override
    public void onRoutingFailure(RouteException e) {}

    @Override
    public void onRoutingStart() {}

    @Override
    public void onRoutingCancelled() {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

}
