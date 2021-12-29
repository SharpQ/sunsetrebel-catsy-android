package com.sunsetrebel.catsy.fragments;

import android.content.res.ColorStateList;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Build;
import android.os.Bundle;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.core.content.res.ResourcesCompat;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.models.EventModel;
import com.sunsetrebel.catsy.utils.EventThemes;
import com.sunsetrebel.catsy.utils.EventThemesService;
import com.sunsetrebel.catsy.utils.GoogleMapService;
import com.sunsetrebel.catsy.utils.ImageUtils;
import com.sunsetrebel.catsy.viewmodel.EventListViewModel;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Random;


public class EventListDetailedFragment extends Fragment implements OnMapReadyCallback {
    private EventListViewModel eventListViewModel;
    private EventModel eventModel;
    private GoogleMap mMap;
    private SimpleDateFormat simpleDateFormat;
    private ImageView backButton, likeButton, shareButton, extraButton;
    private AppCompatButton joinButton;
    private ImageView ivHostAvatar, ivEventAvatar;
    private TextView tvEventTitle, tvHostName, tvEventStartTime, tvEventEndTime, tvEventDescription,
            tvEventParticipants, tvEventMinAge, tvEventMaxAge, tvEventMaxPerson;
    private LinearLayout linearLayout;
    private Random rand = new Random();
    private EventThemesService eventThemesService;
    private Map<Enum<?>, String> eventThemesEnumList;
    private boolean isUserJoinedToEvent;

    public EventListDetailedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_list_detailed, container, false);
        eventListViewModel = new ViewModelProvider(requireActivity()).get(EventListViewModel.class);
        eventListViewModel.init();
        eventModel = eventListViewModel.getSelectedEvent();
        simpleDateFormat = new SimpleDateFormat("HH:mm d MMM ''yy", Locale.getDefault());
        eventThemesService = new EventThemesService(getContext().getResources());
        eventThemesEnumList = eventThemesService.getEventThemesList();
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.fragment_event_detailed_map);
        mapFragment.getMapAsync(this);

        backButton = v.findViewById(R.id.ib_back);
        likeButton = v.findViewById(R.id.ib_like);
        shareButton = v.findViewById(R.id.ib_share);
        extraButton = v.findViewById(R.id.ib_extra);
        joinButton = v.findViewById(R.id.button_join);
        ivHostAvatar = v.findViewById(R.id.iv_host_avatar);
        ivEventAvatar = v.findViewById(R.id.iv_event_avatar);
        tvEventTitle = v.findViewById(R.id.tv_event_title);
        tvHostName = v.findViewById(R.id.tv_host_name);
        tvEventStartTime = v.findViewById(R.id.tv_start_time_value);
        tvEventEndTime = v.findViewById(R.id.tv_end_time_value);
        tvEventDescription = v.findViewById(R.id.tv_event_description);
        tvEventParticipants = v.findViewById(R.id.tv_event_detailed_participants_value);
        tvEventMinAge = v.findViewById(R.id.tv_event_detailed_min_age_value);
        tvEventMaxAge = v.findViewById(R.id.tv_event_detailed_max_age_value);
        tvEventMaxPerson = v.findViewById(R.id.tv_event_detailed_max_people_value);
        linearLayout = v.findViewById(R.id.ll_event_detailed_tags);

        backButton.setOnClickListener(v1 -> getParentFragmentManager().popBackStack());

        //Set event avatar
        ImageUtils.loadImageView(getContext(), eventModel.getEventAvatar(), ivEventAvatar, R.drawable.im_event_avatar_placeholder_64);
        //Set host avatar
        ImageUtils.loadImageView(getContext(), eventModel.getHostProfileImg(), ivHostAvatar, R.drawable.im_cat_hearts);
        tvEventTitle.setText(eventModel.getEventTitle());
        tvHostName.setText(getContext().getString(R.string.event_list_host_placeholder) + eventModel.getHostName());
        tvEventStartTime.setText(simpleDateFormat.format(eventModel.getEventStartTime()));
        tvEventEndTime.setText(simpleDateFormat.format(eventModel.getEventEndTime()));
        tvEventDescription.setText(eventModel.getEventDescr());
        tvEventParticipants.setText(String.format(Locale.getDefault(), "%d", eventModel.getEventParticipants().size()));
        setIntegerTextFields(eventModel.getEventMinAge(), tvEventMinAge);
        setIntegerTextFields(eventModel.getEventMaxAge(), tvEventMaxAge);
        setIntegerTextFields(eventModel.getEventMaxPerson(), tvEventMaxPerson);

        if (eventListViewModel.isUserEventHost(eventModel)) {
            setJoinButtonAsHost();
        } else if (eventListViewModel.isUserJoinedToEvent(eventModel.getEventParticipants())) {
            setJoinButtonAsJoined();
        }

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

        joinButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (isUserJoinedToEvent) {
                    eventListViewModel.leaveEvent(isResponseSuccessful -> {
                        if (isResponseSuccessful) {
                            setJoinButtonAsGuest();
                        }
                    }, getContext(), eventModel);
                } else {
                    eventListViewModel.joinEvent(isResponseSuccessful -> {
                        if (isResponseSuccessful) {
                            setJoinButtonAsJoined();
                        }
                    }, getContext(), eventModel);
                }
            }
        });
        return v;
    }

    private void setJoinButtonAsHost() {
        isUserJoinedToEvent = true;
        joinButton.setEnabled(false);
        joinButton.setText(getContext().getString(R.string.event_detailed_joined_button_user_host));
        joinButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.blackQuoterTransparent)));
    }

    private void setJoinButtonAsJoined() {
        isUserJoinedToEvent = true;
        joinButton.setEnabled(true);
        joinButton.setText(getContext().getString(R.string.event_detailed_joined_button_leave_state));
        joinButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryLightColor)));
    }

    private void setJoinButtonAsGuest() {
        isUserJoinedToEvent = false;
        joinButton.setEnabled(true);
        joinButton.setText(getContext().getString(R.string.event_detailed_join_button));
        joinButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryColor)));
    }

    private void setIntegerTextFields(Integer integer, TextView textView) {
        if (integer != null) {
            textView.setText(String.format(Locale.getDefault(), "%d", integer));
        } else {
            textView.setText("N/A");
        }
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        GoogleMapService.setupMap(googleMap, getContext(), EventListDetailedFragment.this);
        GoogleMapService.clearAndSetMarker(mMap, eventModel.getEventGeoLocation(), 12, eventModel.getEventLocation());
    }
}
