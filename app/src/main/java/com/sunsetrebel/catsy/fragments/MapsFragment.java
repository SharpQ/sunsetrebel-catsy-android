package com.sunsetrebel.catsy.fragments;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Point;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.util.TypedValue;
import android.view.Display;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.res.ResourcesCompat;
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
import com.sunsetrebel.catsy.models.EventModel;
import com.sunsetrebel.catsy.repositories.FirebaseFirestoreService;
import com.sunsetrebel.catsy.utils.EventThemes;
import com.sunsetrebel.catsy.utils.EventThemesUtil;
import com.sunsetrebel.catsy.utils.GoogleMapService;
import com.sunsetrebel.catsy.utils.ImageUtils;
import com.sunsetrebel.catsy.utils.PermissionUtils;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;


public class MapsFragment extends Fragment implements OnMapReadyCallback, GoogleApiClient.OnConnectionFailedListener, RoutingListener {
    private GoogleMap mMap;
    private FirebaseFirestoreService firebaseFirestoreService;
    private FloatingActionButton fab;
    private PopupWindow infoPopup = null;
    private SimpleDateFormat simpleDateFormat;
    private Random rand = new Random();
    private EventThemesUtil eventThemesUtil;
    private Map<Enum<?>, String> eventThemesEnumList;
    private List<Polyline> polylines = null;
    private FusedLocationProviderClient fusedLocationProviderClient;
    private EventModel selectedEvent = null;

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
        simpleDateFormat = new SimpleDateFormat("HH:mm d MMM ''yy", Locale.getDefault());
        eventThemesUtil = new EventThemesUtil(getContext().getResources());
        eventThemesEnumList = eventThemesUtil.getEventThemesList();
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

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStop() {
        super.onStop();
        closePopup();
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
            closePopup();
        });

        mMap.setOnMarkerClickListener(marker -> {
            EventModel event = (EventModel) marker.getTag();
            selectedEvent = event;
            showPopup(getView(), event);
            clearPolylines();
            fab.setEnabled(true);
            fab.setVisibility(View.VISIBLE);
            return false;
        });
    }


    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtils.getAccessLocationRequestCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                GoogleMapService.zoomToUserLocation(getContext(), mMap);
            } else {
                Log.d("DEBUG", "Permissions not granted");
            }
        }
    }

    private void showPopup(View view, EventModel eventModel) {
        if (infoPopup != null) {
            infoPopup.dismiss();
        }

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View popupView = layoutInflater.inflate(R.layout.item_map_fragment, null);
        TextView tvEventTitle = popupView.findViewById(R.id.textViewEventTitle);
        TextView tvHostName = popupView.findViewById(R.id.textViewHostName);
        TextView tvStartTime = popupView.findViewById(R.id.til_start_time);
        TextView tvEventLocation = popupView.findViewById(R.id.textViewLocation);
        TextView tvEventDescription = popupView.findViewById(R.id.tiet_event_description);
        TextView tvEventParticipants = popupView.findViewById(R.id.textViewParticipants);
        LinearLayout linearLayout = popupView.findViewById(R.id.ll_tags);
        ImageButton likeButton = popupView.findViewById(R.id.imageButtonLike);
        ImageButton shareButton = popupView.findViewById(R.id.imageButtonShare);
        ImageView ivEventAvatar = popupView.findViewById(R.id.imageViewEventAvatar);
        ImageView ivHostAvatar = popupView.findViewById(R.id.imageViewHostAvatar);

        //Set event avatar
        ImageUtils.loadImageView(getContext(), eventModel.getEventAvatar(), ivEventAvatar, R.drawable.im_event_avatar_placeholder_64);
        //Set host avatar
        ImageUtils.loadImageView(getContext(), eventModel.getHostProfileImg(), ivHostAvatar, R.drawable.im_cat_hearts);
        tvEventTitle.setText(eventModel.getEventTitle());
        tvHostName.setText(eventModel.getHostName());
        tvStartTime.setText(simpleDateFormat.format(eventModel.getEventStartTime()));
        tvEventParticipants.setText(String.format(Locale.getDefault(), "%d", eventModel.getEventParticipants()));
        tvEventDescription.setText(eventModel.getEventDescr());
        tvEventLocation.setText(eventModel.getEventLocation());
        likeButton.setEnabled(false);
        likeButton.setVisibility(View.INVISIBLE);
        shareButton.setEnabled(false);
        shareButton.setVisibility(View.INVISIBLE);

        List<EventThemes> eventThemes = eventModel.getEventThemes();
        if (eventThemes != null) {
            for (EventThemes theme : eventThemes) {
                TextView tv = new TextView(getContext());
                tv.setText("#" + eventThemesEnumList.get(theme));
                LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(
                        LinearLayout.LayoutParams.WRAP_CONTENT,
                        LinearLayout.LayoutParams.MATCH_PARENT
                );
                params.setMargins(0,0,0,0);
                tv.setLayoutParams(params);
                tv.setPadding(1,1,5,1);
                tv.setGravity(Gravity.CENTER_HORIZONTAL);
                tv.setTextSize(14);
                tv.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);

                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    tv.setTextColor(Color.rgb(rand.nextFloat(), rand.nextFloat(), rand.nextFloat()));
                } else {
                    tv.setTextColor(getContext().getResources().getColor(R.color.primaryTextColor));
                }
                Typeface typeface = ResourcesCompat.getFont(getContext(), R.font.audiowide);
                tv.setTypeface(typeface);
                linearLayout.addView(tv);
            }
        }

        infoPopup = new PopupWindow(popupView,
                ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);

        Display display = getActivity().getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int width = size.x;
        infoPopup.setWidth(width - (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 5, getResources().getDisplayMetrics()));
        infoPopup.setAnimationStyle(R.style.popup_window_animation);
        infoPopup.showAtLocation(view, Gravity.TOP, 0, 0);
    }

    private void closePopup() {
        if (infoPopup != null) {
            infoPopup.dismiss();
            selectedEvent = null;
            fab.setEnabled(false);
            fab.setVisibility(View.INVISIBLE);
        }
    }

    private void drawPrimaryLinePath(LatLng start, LatLng end)
    {
        if (mMap == null) {
            return;
        }
        clearPolylines();
        findroutes(start, end);
    }

    public void findroutes(LatLng Start, LatLng End)
    {
        if (Start==null || End==null) {
            return;
        }
        else
        {
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
