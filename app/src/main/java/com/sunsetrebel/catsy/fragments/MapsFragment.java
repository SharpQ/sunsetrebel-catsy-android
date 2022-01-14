package com.sunsetrebel.catsy.fragments;

import android.annotation.SuppressLint;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Marker;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.models.EventModel;
import com.sunsetrebel.catsy.repositories.FirebaseFirestoreService;
import com.sunsetrebel.catsy.utils.EventThemes;
import com.sunsetrebel.catsy.utils.EventThemesUtil;
import com.sunsetrebel.catsy.utils.GoogleMapService;
import com.sunsetrebel.catsy.utils.ImageUtils;
import com.sunsetrebel.catsy.utils.PermissionUtils;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;


public class MapsFragment extends Fragment implements OnMapReadyCallback {
    private GoogleMap mMap;
    private FirebaseFirestoreService firebaseFirestoreService;
    private String hostPlaceholder;
    private PopupWindow infoPopup = null;
    private SimpleDateFormat simpleDateFormat;
    private Random rand = new Random();
    private EventThemesUtil eventThemesUtil;
    private Map<Enum<?>, String> eventThemesEnumList;

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
        firebaseFirestoreService = FirebaseFirestoreService.getInstance();
        hostPlaceholder = getContext().getString(R.string.event_list_host_placeholder);
        simpleDateFormat = new SimpleDateFormat("HH:mm d MMM ''yy", Locale.getDefault());
        eventThemesUtil = new EventThemesUtil(getContext().getResources());
        eventThemesEnumList = eventThemesUtil.getEventThemesList();
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
        GoogleMapService.setupMap(googleMap, getContext(), true, false, MapsFragment.this);
        firebaseFirestoreService.getEventList(eventList -> {
            if (eventList != null) {
                for (EventModel event : eventList) {
                    GoogleMapService.setEventMarker(mMap, event, getContext());
                }
            }
        });
        mMap.setOnMarkerClickListener(marker -> {
            EventModel event = (EventModel) marker.getTag();
            showPopup(getView(), event);
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
                Log.e("INFO", "Permissions not granted");
            }
        }
    }

    private void showPopup(View view, EventModel eventModel) {
        if (infoPopup != null) {
            infoPopup.dismiss();
        }

        LayoutInflater layoutInflater = LayoutInflater.from(getActivity());
        View popupView = layoutInflater.inflate(R.layout.popup_event_map_fragment, null);
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
        tvHostName.setText(hostPlaceholder + eventModel.getHostName());
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
        infoPopup.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        infoPopup.setOutsideTouchable(true);
        infoPopup.showAtLocation(view, Gravity.TOP, 0, 0);
    }
}
