package com.sunsetrebel.catsy.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.activities.AddEventMapsActivity;
import com.sunsetrebel.catsy.utils.AccessTypes;
import com.sunsetrebel.catsy.utils.FirebaseAuthService;
import com.sunsetrebel.catsy.utils.FirebaseFirestoreService;
import com.sunsetrebel.catsy.utils.FirebaseStorageService;

import java.text.SimpleDateFormat;
import java.util.Calendar;


public class AddEventFragment extends Fragment {
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
    private View fragmentMap;
    private AutoCompleteTextView autoCompleteTextView;
    private GoogleMap mMap;
    static AppCompatEditText cardEventLocation;
    private static final int IMAGE_PICK_CODE = 1000;
    private static final int PERMISSION_CODE = 1001;
    private int ACCESS_LOCATION_REQUEST_CODE = 10001;
    private Uri eventAvatar;
    private String userFullName;
    private int ddlEventAccessPosition;


    public AddEventFragment() {
        // Required empty public constructor
    }


    public static void putArguments(Bundle args)
    {
        String cardEventLocationInfo = args.getString("Location");
        Float latlng = args.getFloat("Coordinates");
        cardEventLocation.setText(cardEventLocationInfo);
        latlng.longValue();
    }

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            try {
                // Customise the styling of the base map using a JSON object defined
                // in a raw resource file.
                boolean success = googleMap.setMapStyle(
                        MapStyleOptions.loadRawResourceStyle(
                                getContext(), R.raw.google_style));

                if (!success) {
                    Log.e("INFO", "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e("INFO", "Can't find style. Error: ", e);
            }
            mMap = googleMap;
            //Google maps default buttons disabling
            mMap.getUiSettings().setMyLocationButtonEnabled(false);
            mMap.getUiSettings().setMapToolbarEnabled(false);
            mMap.getUiSettings().setCompassEnabled(false);
            mMap.getUiSettings().setRotateGesturesEnabled(false);
            mMap.getUiSettings().setAllGesturesEnabled(false);

            if (ContextCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
                zoomToUserLocation();
            } else {
                if (ActivityCompat.shouldShowRequestPermissionRationale(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION)) {
                    //We can show user a dialog why this permission is necessary
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
                } else {
                    ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
                }

            }
            LatLng FirstMarkerPosition = new LatLng(50.436404, 30.369498);
                    MarkerOptions markerOptions = new MarkerOptions()
                            .position(FirstMarkerPosition).icon(BitmapDescriptorFactory.fromResource(R.drawable.im_cat_location_sample_35));
                    mMap.addMarker(markerOptions);
                    mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(FirstMarkerPosition, 12));
        }
    };


    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private void zoomToUserLocation() {
        if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }

    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_event, container, false);
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
        fragmentMap = v.findViewById(R.id.fragmentMap);
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
                    || eventAccessValue == null || TextUtils.isEmpty(eventDescrValue)) { //TextUtils.isEmpty(eventLocationValue)
                return;
            }

            if (eventAccessValue == AccessTypes.PUBLIC || eventAccessValue == AccessTypes.SELECTIVE) {
                firebaseStorageService.getAvatarStorageReference(downloadUrl -> {
                    firebaseFirestoreService.createNewPublicEvent(fAuth.getCurrentUser().getUid(), eventTitleValue, eventLocationValue, eventStartTimeValue, eventEndTimeValue, eventAccessValue, eventDescrValue, downloadUrl, userFullName);
                }, fAuth.getUid(), eventAvatar);
            } else {
                firebaseStorageService.getAvatarStorageReference(downloadUrl -> {
                    firebaseFirestoreService.createNewPrivateEvent(fAuth.getCurrentUser().getUid(), eventTitleValue, eventLocationValue, eventStartTimeValue, eventEndTimeValue, eventAccessValue, eventDescrValue, downloadUrl, userFullName);
                }, fAuth.getUid(), eventAvatar);
            }

            clearInputFiels();
        });

        eventLocation.setOnClickListener(v12 -> {
            startActivity(new Intent(getContext(), AddEventMapsActivity.class));
            Animatoo.animateFade(getActivity());
        });

        mAddImageLabel.setOnClickListener(v17 -> {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (ActivityCompat.checkSelfPermission(getActivity(), Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_DENIED) {
                    String[] permissions = {Manifest.permission.READ_EXTERNAL_STORAGE};
                    requestPermissions(permissions, PERMISSION_CODE);
                } else {
                    pickImageFromGallery();
                }
            } else {
                pickImageFromGallery();
            }
        });
        return v;
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK);
        intent.setType("image/*");
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.fragmentMap);
        if (mapFragment != null) {
            mapFragment.getMapAsync(callback);
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == Activity.RESULT_OK && requestCode == IMAGE_PICK_CODE) {
            eventAvatar = data.getData();
            mAvatarImageView.setImageURI(data.getData());
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PERMISSION_CODE: {
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    pickImageFromGallery();
                } else {
                    Toast.makeText(getActivity(), "Please accept storage permission!", Toast.LENGTH_SHORT).show();
                }
            }
        }
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


//    private void showTimeDialog(final EditText time_in) {
//        final Calendar calendar=Calendar.getInstance();
//
//        TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
//            @Override
//            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
//                calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
//                calendar.set(Calendar.MINUTE,minute);
//                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm");
//                time_in.setText(simpleDateFormat.format(calendar.getTime()));
//            }
//        };
//
//        new TimePickerDialog(getActivity(),timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
//    }
//
//    private void showDateDialog(final EditText date_in) {
//        final Calendar calendar=Calendar.getInstance();
//        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
//            @Override
//            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
//                calendar.set(Calendar.YEAR,year);
//                calendar.set(Calendar.MONTH,month);
//                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
//                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yy-MM-dd");
//                date_in.setText(simpleDateFormat.format(calendar.getTime()));
//
//            }
//        };
//
//        new DatePickerDialog(getActivity(),dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
//    }

    private void clearInputFiels() {
        eventTitle.getText().clear();
        eventLocation.getText().clear();
        eventStartTime.getText().clear();
        eventEndTime.getText().clear();
        eventDescr.getText().clear();
    }
}
