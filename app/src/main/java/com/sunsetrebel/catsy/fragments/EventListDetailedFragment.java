package com.sunsetrebel.catsy.fragments;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.enums.PopupType;
import com.sunsetrebel.catsy.models.CommonUserModel;
import com.sunsetrebel.catsy.models.EventModel;
import com.sunsetrebel.catsy.utils.CustomToastUtil;
import com.sunsetrebel.catsy.utils.EventThemesUtil;
import com.sunsetrebel.catsy.utils.GoogleMapService;
import com.sunsetrebel.catsy.utils.ImageUtils;
import com.sunsetrebel.catsy.utils.PopupService;
import com.sunsetrebel.catsy.viewmodel.EventListViewModel;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;


public class EventListDetailedFragment extends Fragment implements OnMapReadyCallback {
    private EventListViewModel eventListViewModel;
    private EventModel eventModel;
    private GoogleMap mMap;
    private SimpleDateFormat simpleDateFormat;
    private ImageView backButton, likeButton, shareButton, extraButton;
    private AppCompatButton joinButton;
    private ImageView ivHostAvatar, ivEventAvatar;
    private TextView tvEventTitle, tvHostName, tvEventStartTime, tvEventEndTime, tvEventDescription,
            tvEventParticipants, tvAgeLimit;
    private LinearLayout linearLayoutThemes, linearLayoutParticipants;
    private boolean isUserJoinedToEvent;
    private boolean isUserEventHost;
    private int imageSizeUsersProfile;
    private int imageMarginUsersProfile;
    private final int maxUsersToDisplayInLinear = 3;
    private EventThemesUtil eventThemesUtil;
    private PopupService popupService;

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
        eventModel = eventListViewModel.getSelectedEvent();
        simpleDateFormat = new SimpleDateFormat("HH:mm d MMM ''yy", Locale.getDefault());
        eventThemesUtil = EventThemesUtil.getInstance(getContext().getResources());
        SupportMapFragment mapFragment = (SupportMapFragment) this.getChildFragmentManager()
                .findFragmentById(R.id.fragment_event_detailed_map);
        mapFragment.getMapAsync(this);
        popupService = PopupService.getInstance(getContext());
        imageSizeUsersProfile = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 35, getResources().getDisplayMetrics());
        imageMarginUsersProfile = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 4, getResources().getDisplayMetrics());

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
        tvAgeLimit = v.findViewById(R.id.tv_event_detailed_age_limit_value);
        linearLayoutThemes = v.findViewById(R.id.ll_event_detailed_tags);
        linearLayoutParticipants = v.findViewById(R.id.ll_event_users);

        backButton.setOnClickListener(v1 -> getParentFragmentManager().popBackStack());

        //Get event values
        String usersCountValue = String.format(Locale.getDefault(), "%d", eventModel.getEventParticipants());
        Integer eventMaxPersonInt = eventModel.getEventMaxPerson();
        if (eventMaxPersonInt != null) {
            String eventMaxPersonString = String.format(Locale.getDefault(), "%d", eventMaxPersonInt);
            usersCountValue = usersCountValue.concat(" / ").concat(eventMaxPersonString);
        }
        SpannableString usersCountSpan = new SpannableString(usersCountValue);
        usersCountSpan.setSpan(new UnderlineSpan(), 0, usersCountSpan.length(), 0);
        Integer eventMinAgeInt = eventModel.getEventMinAge();
        Integer eventMaxAgeInt = eventModel.getEventMaxAge();
        String eventAgeLimitStr = "", eventMinAgeStr, eventMaxAgeStr;
        if (eventMinAgeInt != null && eventMaxAgeInt != null) {
            eventMinAgeStr = String.format(Locale.getDefault(), "%d", eventMinAgeInt);
            eventMaxAgeStr = String.format(Locale.getDefault(), "%d", eventMaxAgeInt);
            eventAgeLimitStr = eventAgeLimitStr.concat(eventMinAgeStr).concat(" - ").concat(eventMaxAgeStr)
                    .concat(getContext().getResources().getText(R.string.event_detailed_age_limit_placeholder_years).toString());
        } else if (eventMinAgeInt == null && eventMaxAgeInt == null) {
            eventAgeLimitStr = "N/A";
        } else if (eventMinAgeInt != null && eventMaxAgeInt == null) {
            eventMinAgeStr = String.format(Locale.getDefault(), "%d", eventMinAgeInt);
            eventAgeLimitStr = eventAgeLimitStr.concat(getContext().getResources().getText(R.string.event_detailed_age_limit_placeholder_more).toString())
                    .concat(eventMinAgeStr).concat(getContext().getResources().getText(R.string.event_detailed_age_limit_placeholder_years).toString());
        } else if (eventMinAgeInt == null && eventMaxAgeInt != null) {
            eventMaxAgeStr = String.format(Locale.getDefault(), "%d", eventMaxAgeInt);
            eventAgeLimitStr = eventAgeLimitStr.concat(getContext().getResources().getText(R.string.event_detailed_age_limit_placeholder_less).toString())
                    .concat(eventMaxAgeStr).concat(getContext().getResources().getText(R.string.event_detailed_age_limit_placeholder_years).toString());
        }
        //Set event avatar
        ImageUtils.loadImageView(getContext(), eventModel.getEventAvatar(), ivEventAvatar, R.drawable.im_event_avatar_placeholder_64);
        //Set host avatar
        ImageUtils.loadImageView(getContext(), eventModel.getHostProfileImg(), ivHostAvatar, R.drawable.im_cat_hearts);
        tvEventTitle.setText(eventModel.getEventTitle());
        tvHostName.setText(eventModel.getHostName());
        tvEventStartTime.setText(simpleDateFormat.format(eventModel.getEventStartTime()));
        tvEventEndTime.setText(simpleDateFormat.format(eventModel.getEventEndTime()));
        tvEventDescription.setText(eventModel.getEventDescr());
        tvEventParticipants.setText(usersCountSpan);

        tvAgeLimit.setText(eventAgeLimitStr);
        if (eventListViewModel.isEventLikedByUser(eventModel.getEventId())) {
            likeButton.setVisibility(View.INVISIBLE);
            likeButton.setEnabled(false);
        } else {
            likeButton.setVisibility(View.VISIBLE);
            likeButton.setEnabled(true);
        }

        isUserEventHost = eventListViewModel.isUserEventHost(eventModel);

        if (isUserEventHost) {
            setJoinButtonAsHost();
        }

        eventListViewModel.getEventParticipants(value -> {
            boolean isUserJoinedToEvent = eventListViewModel.isUserJoinedToEvent(value);
            if (!isUserEventHost && isUserJoinedToEvent) {
                setJoinButtonAsJoined();
            } else if (!isUserEventHost && !isUserJoinedToEvent) {
                setJoinButtonAsGuest();
            }
            setEventUsers(value);
        }, eventModel);

        joinButton.setOnClickListener(v12 -> {
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
        });

        likeButton.setOnClickListener(v13 -> eventListViewModel.likeEvent(value -> {
            if (value) {
                likeButton.setVisibility(View.INVISIBLE);
                CustomToastUtil.showSuccessToast(getContext(), getContext().getResources().getText(R.string.event_liked_success).toString() + eventModel.getEventTitle());
                Log.d("DEBUG", "You liked event: " + eventModel.getEventId());
            } else {
                likeButton.setVisibility(View.VISIBLE);
                CustomToastUtil.showFailToast(getContext(), getContext().getResources().getText(R.string.event_liked_fail).toString() + eventModel.getEventTitle());
                Log.d("DEBUG", "Failed to like event: " + eventModel.getEventId());
            }
        }, eventModel.getEventId()));

        eventThemesUtil.setEventThemesUI(eventModel.getEventThemes(), this, linearLayoutThemes, null);
        return v;
    }

    @Override
    public void onStop() {
        super.onStop();
        popupService.closePopup();
    }

    private void setEventUsers(List<CommonUserModel> eventParticipants) {
        if (eventParticipants.size() > 0) {
            if (eventParticipants.size() <= maxUsersToDisplayInLinear) {
                tvEventParticipants.setVisibility(View.INVISIBLE);
                tvEventParticipants.setEnabled(false);
                for (CommonUserModel userProfile : eventParticipants) {
                    setParticipantsImageButton(getContext(), userProfile);
                }
            } else {
                tvEventParticipants.setVisibility(View.VISIBLE);
                tvEventParticipants.setEnabled(true);
                for (int i = 0; i<maxUsersToDisplayInLinear; i++) {
                    setParticipantsImageButton(getContext(), eventParticipants.get(i));
                }
            }
        }
    }

    private void setParticipantsImageButton(Context context, CommonUserModel userProfile) {
        ImageButton imageButton = new ImageButton(context);
        imageButton.setBackgroundColor(getResources().getColor(R.color.primaryDarkColor));
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(imageSizeUsersProfile, imageSizeUsersProfile);
        params.setMargins(imageMarginUsersProfile,0,imageMarginUsersProfile,0);
        imageButton.setLayoutParams(params);
        imageButton.setAdjustViewBounds(true);
        imageButton.setScaleType(ImageView.ScaleType.FIT_XY);
        imageButton.setPadding(0,0,0,0);
        imageButton.setBackgroundColor(Color.TRANSPARENT);
        linearLayoutParticipants.addView(imageButton);
        ImageUtils.loadRoundedImageView(getContext(), userProfile.getUserProfileImg(), imageButton, R.drawable.im_cat_hearts);

        imageButton.setOnClickListener(v -> popupService.showPopup(this, userProfile,
                PopupType.USER_EVENT_DETAILED, null, null, R.style.popup_window_animation, Gravity.CENTER, true));
    }

    private void setJoinButtonAsHost() {
        isUserJoinedToEvent = true;
        joinButton.setEnabled(false);
        joinButton.setVisibility(View.INVISIBLE);
    }

    private void setJoinButtonAsJoined() {
        isUserJoinedToEvent = true;
        joinButton.setEnabled(true);
        joinButton.setVisibility(View.VISIBLE);
        joinButton.setText(getContext().getString(R.string.event_detailed_joined_button_leave_state));
        joinButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryLightColor)));
    }

    private void setJoinButtonAsGuest() {
        isUserJoinedToEvent = false;
        joinButton.setEnabled(true);
        joinButton.setVisibility(View.VISIBLE);
        joinButton.setText(getContext().getString(R.string.event_detailed_join_button));
        joinButton.setBackgroundTintList(ColorStateList.valueOf(getResources().getColor(R.color.primaryColor)));
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        GoogleMapService.setupMap(googleMap, getContext(), false, false, EventListDetailedFragment.this);
        GoogleMapService.clearAndSetMarker(mMap, eventModel.getEventGeoLocation(), 12, eventModel.getEventLocation(), getContext());
    }
}
