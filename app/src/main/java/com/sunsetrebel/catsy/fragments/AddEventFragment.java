package com.sunsetrebel.catsy.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.activities.AddEventMapsActivity;
import com.sunsetrebel.catsy.utils.AccessType;
import com.sunsetrebel.catsy.utils.EventThemes;
import com.sunsetrebel.catsy.repositories.FirebaseAuthService;
import com.sunsetrebel.catsy.repositories.FirebaseFirestoreService;
import com.sunsetrebel.catsy.repositories.FirebaseStorageService;
import com.sunsetrebel.catsy.utils.GoogleMapService;
import com.sunsetrebel.catsy.utils.PermissionUtils;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.Map;


public class AddEventFragment extends Fragment implements OnMapReadyCallback {
    private com.google.firebase.auth.FirebaseAuth fAuth;
    private final FirebaseAuthService firebaseAuthService = FirebaseAuthService.getInstance();
    private final FirebaseFirestoreService firebaseFirestoreService = FirebaseFirestoreService.getInstance();
    private final FirebaseStorageService firebaseStorageService = FirebaseStorageService.getInstance();
    private TextInputLayout eventTitleLayout, eventAccessLayout, eventLocationLayout,
            eventDescrLayout, eventStartTimeLayout, eventEndTimeLayout;
    private TextInputEditText eventTitle, eventLocation, eventStartTime, eventEndTime, eventDescr,
            eventTheme, eventMinAge, eventMaxAge, eventAttendees;
    private String[] listOfAccessTypes;
    private AppCompatButton submitButton;
    private MaterialTextView mAddImageLabel;
    private ImageView mAvatarImageView;
    private AutoCompleteTextView autoCompleteTextView;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int ADDRESS_PICK_CODE = 228;
    private Uri eventAvatar;
    private String userFullName;
    private int ddlEventAccessPosition;
    private LatLng eventLatLng;
    private String eventAddress;
    private boolean[] selectedTheme;
    private List<Enum<?>> eventThemes;
    private ArrayAdapter<String> arrayAdapter;
    private GoogleMap mMap;
    private Date startTimeDate;
    private Date endTimeDate;

    public AddEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_event, container, false);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragmentMap);
        mapFragment.getMapAsync(this);
        fAuth = firebaseAuthService.getFirebaseClient();
        firebaseFirestoreService.getUserNameInFirestore(value -> {
            userFullName = value;
        }, fAuth.getUid());
        listOfAccessTypes = getResources().getStringArray(R.array.event_access_types);
        arrayAdapter = new ArrayAdapter<>(getContext(), R.layout.item_ddl_event_type, listOfAccessTypes);

        eventTitle = v.findViewById(R.id.inputEditEventTitle);
        eventLocation = v.findViewById(R.id.inputEditLocation);
        eventStartTime = v.findViewById(R.id.inputEditStartTime);
        eventEndTime = v.findViewById(R.id.inputEditEndTime);
        eventTheme = v.findViewById(R.id.inputEditEventTheme);
        eventDescr = v.findViewById(R.id.textViewEventDescription);
        eventMinAge = v.findViewById(R.id.inputEditEventMinAge);
        eventMaxAge = v.findViewById(R.id.inputEditEventMaxAge);
        eventAttendees = v.findViewById(R.id.inputEditEventAttendees);
        submitButton = v.findViewById(R.id.buttonSubmitNewEvent);
        autoCompleteTextView = v.findViewById(R.id.autoCompleteTextView);
        mAvatarImageView = v.findViewById(R.id.imageViewAddEventAvatar);
        mAddImageLabel = v.findViewById(R.id.materialTextViewAddImage);
        eventTitleLayout = v.findViewById(R.id.inputLayoutEventTitle);
        eventAccessLayout = v.findViewById(R.id.inputLayoutEventAccess);
        eventLocationLayout = v.findViewById(R.id.textViewLocation);
        eventDescrLayout = v.findViewById(R.id.inputLayoutEventDescription);
        eventStartTimeLayout = v.findViewById(R.id.textViewStartTime);
        eventEndTimeLayout = v.findViewById(R.id.inputLayoutEndTime);

        autoCompleteTextView.setAdapter(arrayAdapter);

        eventStartTime.setOnClickListener(v15 -> showDateTimeDialog(eventStartTime, true));

        eventEndTime.setOnClickListener(v16 -> showDateTimeDialog(eventEndTime, false));

        //Initialize themes array
        EventThemes eventThemesList = new EventThemes(getActivity().getResources());
        Map<Enum<?>, String> themesArray = eventThemesList.getEventThemesList();
        String[] themesArrayValues = themesArray.values().toArray(new String[0]);
        ArrayList<Integer> chosenThemesArray = new ArrayList<>();
        selectedTheme = new boolean[themesArray.size()];

        eventTheme.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize alert dialog
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext(), R.style.DatePickerDialog);
                //Set builder properties
                builder.setTitle(getResources().getString(R.string.add_event_event_theme_dialog_title));
                builder.setCancelable(false);
                builder.setMultiChoiceItems(themesArrayValues, selectedTheme, new DialogInterface.OnMultiChoiceClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which, boolean isChecked) {
                        if (isChecked) {
                            //When checkbox selected add position in theme list
                            if (!chosenThemesArray.contains(which)) {
                                chosenThemesArray.add(which);
                            }
                            //Sort theme list
                            Collections.sort(chosenThemesArray);
                        } else {
                            if (chosenThemesArray.size() > which && chosenThemesArray.contains(which)) {
                                //When checkbox unselected remove position from theme list
                                chosenThemesArray.remove(which);
                            }
                        }
                    }
                });

                builder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        StringBuilder stringBuilder = new StringBuilder();
                        eventThemes = new ArrayList<>();
                        int maxQuantity;
                        if (chosenThemesArray.size() > 3) {
                            Toast.makeText(getContext(), getResources().getString(R.string.add_event_event_theme_dialog_notification), Toast.LENGTH_SHORT).show();
                            maxQuantity = 3;
                        } else {
                            maxQuantity = chosenThemesArray.size();
                        }
                        for (int j = 0; j < maxQuantity; j++) {
                            eventThemes.add(EventThemes.getKeyByValue(themesArray, themesArrayValues[chosenThemesArray.get(j)]));
                            //Concat array value
                            stringBuilder.append(themesArrayValues[chosenThemesArray.get(j)]);
                            if (j != maxQuantity - 1) {
                                stringBuilder.append(", ");
                            }
                        }
                        //Clear all checkboxes and chosen list
                        chosenThemesArray.clear();
                        selectedTheme = new boolean[themesArray.size()];
                        //Set text on textview
                        eventTheme.setText(stringBuilder.toString());
                    }
                });

                builder.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                });

                builder.setNeutralButton("Clear All", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        for (int j=0; j<selectedTheme.length; j++) {
                            //Remove all selection
                            selectedTheme[j] = false;
                            chosenThemesArray.clear();
                            eventTheme.setText("");
                        }
                    }
                });

                builder.show();
            }
        });

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ddlEventAccessPosition = position;
            }
        });

        submitButton.setOnClickListener(v1 -> {
            eventTitleLayout.setError(null);
            eventLocationLayout.setError(null);
            eventStartTimeLayout.setError(null);
            eventEndTimeLayout.setError(null);
            eventDescrLayout.setError(null);
            eventAccessLayout.setError(null);
            String eventTitleValue = eventTitle.getText().toString().trim();
            String eventLocationValue = eventLocation.getText().toString().trim();
            String eventDescrValue = eventDescr.getText().toString().trim();
            String eventMinAgeValue = eventMinAge.getText().toString().trim();
            String eventMaxAgeValue = eventMaxAge.getText().toString().trim();
            String eventAttendeesValue = eventAttendees.getText().toString().trim();

            AccessType eventAccessValue;
            switch (ddlEventAccessPosition) {
                case 0:
                    eventAccessValue = AccessType.PUBLIC;
                    break;
                case 1:
                    eventAccessValue = AccessType.PRIVATE;
                    break;
                case 2:
                    eventAccessValue = AccessType.SELECTIVE;
                    break;
                default:
                    eventAccessValue = null;
            }

            if (TextUtils.isEmpty(eventTitleValue) || startTimeDate == null || endTimeDate == null
                    || eventAccessValue == null || TextUtils.isEmpty(eventLocationValue) || TextUtils.isEmpty(eventDescrValue)) {
                //TO DO: change color of all mandatory fields
                eventTitleLayout.setError(getResources().getString(R.string.add_event_fill_mandatory_field_error));
                eventLocationLayout.setError(getResources().getString(R.string.add_event_fill_mandatory_field_error));
                eventStartTimeLayout.setError(getResources().getString(R.string.add_event_fill_mandatory_field_error));
                eventEndTimeLayout.setError(getResources().getString(R.string.add_event_fill_mandatory_field_error));
                eventDescrLayout.setError(getResources().getString(R.string.add_event_fill_mandatory_field_error));
                eventAccessLayout.setError(getResources().getString(R.string.add_event_fill_mandatory_field_error));
                return;
            }

            if (eventAvatar != null) {
                firebaseStorageService.getAvatarStorageReference(downloadUrl -> {
                    if (eventAccessValue == AccessType.PUBLIC || eventAccessValue == AccessType.SELECTIVE) {
                        firebaseFirestoreService.createNewPublicEvent(fAuth.getCurrentUser().getUid(), eventTitleValue, eventLocationValue, eventLatLng, startTimeDate, endTimeDate, eventAccessValue, eventDescrValue, eventMinAgeValue, eventMaxAgeValue, eventAttendeesValue, downloadUrl, eventThemes, userFullName);
                    } else {
                        firebaseFirestoreService.createNewPrivateEvent(fAuth.getCurrentUser().getUid(), eventTitleValue, eventLocationValue, eventLatLng, startTimeDate, endTimeDate, eventAccessValue, eventDescrValue, eventMinAgeValue, eventMaxAgeValue, eventAttendeesValue, downloadUrl, eventThemes, userFullName);
                    }
                }, fAuth.getUid(), eventAvatar);
            } else {
                if (eventAccessValue == AccessType.PUBLIC || eventAccessValue == AccessType.SELECTIVE) {
                    firebaseFirestoreService.createNewPublicEvent(fAuth.getCurrentUser().getUid(), eventTitleValue, eventLocationValue, eventLatLng, startTimeDate, endTimeDate, eventAccessValue, eventDescrValue, eventMinAgeValue, eventMaxAgeValue, eventAttendeesValue, null, eventThemes, userFullName);
                } else {
                    firebaseFirestoreService.createNewPrivateEvent(fAuth.getCurrentUser().getUid(), eventTitleValue, eventLocationValue, eventLatLng, startTimeDate, endTimeDate, eventAccessValue, eventDescrValue, eventMinAgeValue, eventMaxAgeValue, eventAttendeesValue, null, eventThemes, userFullName);
                }
            }
            restartFragment();
            Toast.makeText(getContext(), getResources().getString(R.string.add_event_event_created_notification), Toast.LENGTH_SHORT).show();
        });

        eventLocation.setOnClickListener(v12 -> {
            startActivityForResult(new Intent(getContext(), AddEventMapsActivity.class), ADDRESS_PICK_CODE);
            Animatoo.animateFade(getActivity());
        });

        mAvatarImageView.setOnClickListener(v17 -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (PermissionUtils.isGalleryPermissionEnabled(getContext())) {
                    pickImageFromGallery();
                } else {
                    PermissionUtils.requestGalleryPermissionsFragment(AddEventFragment.this);
                }
            } else {
                pickImageFromGallery();
            }
        });
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
        GoogleMapService.setupMap(googleMap, getContext(), AddEventFragment.this);

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                startActivityForResult(new Intent(getContext(), AddEventMapsActivity.class), ADDRESS_PICK_CODE);
                Animatoo.animateFade(getActivity());
            }
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (IMAGE_PICK_CODE) : {
                if (resultCode == Activity.RESULT_OK) {
                    eventAvatar = data.getData();
                    mAvatarImageView.setImageURI(data.getData());
                    mAddImageLabel.setVisibility(View.INVISIBLE);
                }
                break;
            }
            case (ADDRESS_PICK_CODE) : {
                if (resultCode == Activity.RESULT_OK) {
                    eventLatLng = data.getExtras().getParcelable("EVENT_LAT_LNG");
                    eventAddress = data.getStringExtra("EVENT_ADDRESS");
                    eventLocation.setText(eventAddress);
                    GoogleMapService.clearAndSetMarker(eventLatLng, mMap, 10);
                }
                break;
            }
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PermissionUtils.getAccessGalleryRequestCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            } else {
                Log.e("INFO", "Permissions not granted");
            }
        } else if (requestCode == PermissionUtils.getAccessLocationRequestCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                GoogleMapService.zoomToUserLocation(getContext(), mMap);
            } else {
                Log.e("INFO", "Permissions not granted");
            }
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    private void showDateTimeDialog(final EditText timeEditText, boolean isStartTime) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR, year);
                calendar.set(Calendar.MONTH, month);
                calendar.set(Calendar.DAY_OF_MONTH, dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);

                        if (isStartTime) {
                            startTimeDate = calendar.getTime();
                        } else {
                            endTimeDate = calendar.getTime();
                        }

                        SimpleDateFormat simpleDateFormat = new SimpleDateFormat("HH:mm d MMM ''yy", Locale.getDefault());
                        timeEditText.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };
                new TimePickerDialog(getActivity(), R.style.DatePickerDialog, timeSetListener, calendar.get(Calendar.HOUR_OF_DAY), calendar.get(Calendar.MINUTE),true).show();
            }
        };
        new DatePickerDialog(getActivity(), R.style.DatePickerDialog, dateSetListener, calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DAY_OF_MONTH)).show();
    }

    private void clearInputFiels() {
        eventTitle.getText().clear();
        eventLocation.getText().clear();
        eventStartTime.getText().clear();
        eventEndTime.getText().clear();
        eventDescr.getText().clear();
        eventMinAge.getText().clear();
        eventMaxAge.getText().clear();
        eventAttendees.getText().clear();
        eventTheme.getText().clear();
        autoCompleteTextView.setText(getResources().getText(R.string.add_event_event_access));
        mAvatarImageView.setImageURI(null);
        mAddImageLabel.setVisibility(View.VISIBLE);
    }

    private void restartFragment() {
        FragmentManager fragmentManager = getFragmentManager();
        Fragment fragment = new AddEventFragment();
        fragmentManager.beginTransaction().replace(R.id.frameLayoutMain, fragment)
                .commit();
    }
}
