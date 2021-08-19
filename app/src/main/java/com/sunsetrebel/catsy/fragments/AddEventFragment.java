package com.sunsetrebel.catsy.fragments;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
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
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
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
import com.sunsetrebel.catsy.utils.AccessTypes;
import com.sunsetrebel.catsy.utils.FirebaseAuthService;
import com.sunsetrebel.catsy.utils.FirebaseFirestoreService;
import com.sunsetrebel.catsy.utils.FirebaseStorageService;
import com.sunsetrebel.catsy.utils.GoogleMapService;
import com.sunsetrebel.catsy.utils.PermissionUtils;
import java.text.SimpleDateFormat;
import java.util.Calendar;


public class AddEventFragment extends Fragment implements OnMapReadyCallback {
    private com.google.firebase.auth.FirebaseAuth fAuth;
    private final FirebaseAuthService firebaseAuthService = new FirebaseAuthService();
    private final FirebaseFirestoreService firebaseFirestoreService = new FirebaseFirestoreService();
    private final FirebaseStorageService firebaseStorageService = new FirebaseStorageService();
    private TextInputLayout eventAccess;
    private TextInputEditText eventTitle, eventLocation, eventStartTime, eventEndTime, eventDescr;
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

    public AddEventFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_event, container, false);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragmentMap);
        mapFragment.getMapAsync(this);
        fAuth = firebaseAuthService.getInstance();
        firebaseFirestoreService.getUserNameInFirestore(value -> {
            userFullName = value;
        }, fAuth.getUid());
        listOfAccessTypes = getResources().getStringArray(R.array.event_access_types);
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<String>(getContext(), R.layout.item_ddl_event_type, listOfAccessTypes);

        eventTitle = v.findViewById(R.id.inputEditEventTitle);
        eventLocation = v.findViewById(R.id.inputEditLocation);
        eventStartTime = v.findViewById(R.id.inputEditStartTime);
        eventEndTime = v.findViewById(R.id.inputEditEndTime);
        eventAccess = v.findViewById(R.id.textInputLayoutEventAccess);
        eventDescr = v.findViewById(R.id.inputEditEventDescription);
        submitButton = v.findViewById(R.id.buttonSubmitNewEvent);
        autoCompleteTextView = v.findViewById(R.id.autoCompleteTextView);
        mAvatarImageView = v.findViewById(R.id.imageViewAddEventAvatar);
        mAddImageLabel = v.findViewById(R.id.materialTextViewAddImage);
        autoCompleteTextView.setAdapter(arrayAdapter);

        eventStartTime.setOnClickListener(v15 -> showDateTimeDialog(eventStartTime));

        eventEndTime.setOnClickListener(v16 -> showDateTimeDialog(eventEndTime));

        autoCompleteTextView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ddlEventAccessPosition = position;
            }
        });

        submitButton.setOnClickListener(v1 -> {
            String eventTitleValue = eventTitle.getText().toString().trim();
            String eventLocationValue = eventLocation.getText().toString().trim();
            String eventStartTimeValue = eventStartTime.getText().toString().trim();
            String eventEndTimeValue = eventEndTime.getText().toString().trim();
            String eventDescrValue = eventDescr.getText().toString().trim();

            AccessTypes eventAccessValue;
            switch (ddlEventAccessPosition) {
                case 0:
                    eventAccessValue = AccessTypes.PUBLIC;
                    break;
                case 1:
                    eventAccessValue = AccessTypes.PRIVATE;
                    break;
                case 2:
                    eventAccessValue = AccessTypes.SELECTIVE;
                    break;
                default:
                    eventAccessValue = null;
            }

            if (TextUtils.isEmpty(eventTitleValue) || TextUtils.isEmpty(eventStartTimeValue) || TextUtils.isEmpty(eventEndTimeValue)
                    || eventAccessValue == null || TextUtils.isEmpty(eventLocationValue) || TextUtils.isEmpty(eventDescrValue)) {
                return;
            }

            if (eventAccessValue == AccessTypes.PUBLIC || eventAccessValue == AccessTypes.SELECTIVE) {
                firebaseStorageService.getAvatarStorageReference(downloadUrl -> {
                    firebaseFirestoreService.createNewPublicEvent(fAuth.getCurrentUser().getUid(), eventTitleValue, eventLocationValue, eventLatLng, eventStartTimeValue, eventEndTimeValue, eventAccessValue, eventDescrValue, downloadUrl, userFullName);
                }, fAuth.getUid(), eventAvatar);
            } else {
                firebaseStorageService.getAvatarStorageReference(downloadUrl -> {
                    firebaseFirestoreService.createNewPrivateEvent(fAuth.getCurrentUser().getUid(), eventTitleValue, eventLocationValue, eventLatLng, eventStartTimeValue, eventEndTimeValue, eventAccessValue, eventDescrValue, downloadUrl, userFullName);
                }, fAuth.getUid(), eventAvatar);
            }

            clearInputFiels();
        });

        eventLocation.setOnClickListener(v12 -> {
            startActivityForResult(new Intent(getContext(), AddEventMapsActivity.class), ADDRESS_PICK_CODE);
            Animatoo.animateFade(getActivity());
        });

        mAddImageLabel.setOnClickListener(v17 -> {
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
                }
                break;
            }
            case (ADDRESS_PICK_CODE) : {
                if (resultCode == Activity.RESULT_OK) {
                    eventLatLng = data.getExtras().getParcelable("EVENT_LAT_LNG");
                    eventAddress = data.getStringExtra("EVENT_ADDRESS");
                    eventLocation.setText(eventAddress);
                    GoogleMapService.clearAndSetMarker(eventLatLng);
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
                GoogleMapService.zoomToUserLocation(getContext());
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

    private void showDateTimeDialog(final EditText date_time_in) {
        final Calendar calendar = Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener = new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener = new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);

                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("MM-dd-yy HH:mm");

                        date_time_in.setText(simpleDateFormat.format(calendar.getTime()));
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
    }
}
