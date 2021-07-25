package com.sunsetrebel.catsy.fragments;

import android.Manifest;
import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatEditText;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.fxn.pix.Options;
import com.fxn.pix.Pix;
import com.fxn.utility.ImageQuality;
import com.fxn.utility.PermUtil;
import com.google.android.gms.location.FusedLocationProviderClient;
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
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData;
import com.miguelbcr.ui.rx_paparazzo2.entities.size.Size;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.activities.AddEventMapsActivity;
import com.sunsetrebel.catsy.adapters.AddEventImageAdapter;
import com.sunsetrebel.catsy.utils.FirebaseAuthService;
import com.sunsetrebel.catsy.utils.FirebaseFirestoreService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import static com.facebook.FacebookSdk.getApplicationContext;


public class AddEventFragment extends Fragment {
    private com.google.firebase.auth.FirebaseAuth fAuth;
    private final FirebaseAuthService firebaseAuthService = new FirebaseAuthService();
    private final FirebaseFirestoreService firebaseFirestoreService = new FirebaseFirestoreService();
    private EditText eventDateStart, eventDateEnd, eventTimeStart, eventTimeEnd, eventDescr;
    private TextInputLayout eventAccess;
    private TextInputEditText eventTitle, eventLocation;
    private String[] listOfAccessTypes;
    private Button submitButton;
    private View fragmentMap;
    private AutoCompleteTextView autoCompleteTextView;
    private GoogleMap mMap;
    private Geocoder geocoder;
    //Pick image variables
    private RecyclerView recyclerView;
    private ArrayList<FileData> fileDataList;
    private Size size;
    private static final String STATE_FILES = "FILES";
    RecyclerView recyclerViewEventImage;
    AddEventImageAdapter addEventImageAdapter;
    static AppCompatEditText cardEventLocation;
    Float coordinates;
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Options options;
    ArrayList<String> returnValue = new ArrayList<>();
    private static final String TAG = "MapsActivity";

    private int ACCESS_LOCATION_REQUEST_CODE = 10001;
    FusedLocationProviderClient fusedLocationProviderClient;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public AddEventFragment() {
        // Required empty public constructor
    }

    public static AddEventFragment newInstance(String param1, String param2) {
        AddEventFragment fragment = new AddEventFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
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
                                getApplicationContext(), R.raw.google_style));

                if (!success) {
                    Log.e(TAG, "Style parsing failed.");
                }
            } catch (Resources.NotFoundException e) {
                Log.e(TAG, "Can't find style. Error: ", e);
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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_add_event, container, false);
        fAuth = firebaseAuthService.getInstance();
        listOfAccessTypes = getResources().getStringArray(R.array.event_access_types);
        ArrayAdapter arrayAdapter = new ArrayAdapter(getContext(), R.layout.item_ddl_event_type, listOfAccessTypes);

        //Initializing the views of the dialog.

        eventTitle = v.findViewById(R.id.inputEditEventTitle);
        eventLocation = v.findViewById(R.id.inputEditLocation);
        eventDateStart = v.findViewById(R.id.editEventDateStart);
        eventDateEnd = v.findViewById(R.id.editEventDateEnd);
        eventTimeStart = v.findViewById(R.id.editEventTimeStart);
        eventTimeEnd = v.findViewById(R.id.editEventTimeEnd);
        eventAccess = v.findViewById(R.id.textInputLayoutEventAccess);
        eventDescr = v.findViewById(R.id.editDetailedEventDescription);
        submitButton = v.findViewById(R.id.buttonSubmitNewEvent);
        autoCompleteTextView = v.findViewById(R.id.autoCompleteTextView);
        fragmentMap = v.findViewById(R.id.fragmentMap);
        autoCompleteTextView.setAdapter(arrayAdapter);

        eventDateStart.setInputType(InputType.TYPE_NULL);
        eventDateEnd.setInputType(InputType.TYPE_NULL);
        eventTimeStart.setInputType(InputType.TYPE_NULL);
        eventTimeEnd.setInputType(InputType.TYPE_NULL);

        eventDateStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog(eventDateStart);
            }
        });

        eventDateEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDateDialog(eventDateEnd);
            }
        });

        eventTimeStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog(eventTimeStart);
            }
        });

        eventTimeEnd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimeDialog(eventTimeEnd);
            }
        });

        submitButton.setOnClickListener(v1 -> {
            String eventTitleValue = eventTitle.getText().toString().trim();
            String eventLocationValue = eventLocation.getText().toString().trim();
            String eventDateValue = eventDateStart.getText().toString().trim();
            String eventDateEndValue = eventDateEnd.getText().toString().trim();
            String eventTimeStartValue = eventTimeStart.getText().toString().trim();
            String eventTimeEndValue = eventTimeEnd.getText().toString().trim();
            String eventAccessValue = autoCompleteTextView.getText().toString();
            String eventDescrValue = eventDescr.getText().toString().trim();

            if (TextUtils.isEmpty(eventTitleValue) || TextUtils.isEmpty(eventLocationValue) || TextUtils.isEmpty(eventDateValue) || TextUtils.isEmpty(eventDateEndValue)|| TextUtils.isEmpty(eventAccessValue) || TextUtils.isEmpty(eventDescrValue)) {
                return;
            }
            firebaseFirestoreService.getUserNameInFirestore(value -> {
                firebaseFirestoreService.createNewEvent(fAuth.getCurrentUser().getUid(), eventTitleValue, eventLocationValue, eventDateValue, eventAccessValue, eventDescrValue, value);
            }, fAuth.getUid());

            clearInputFiels();
        });



        eventLocation.setOnClickListener(v12 -> {
            startActivity(new Intent(getApplicationContext(), AddEventMapsActivity.class));
            Animatoo.animateFade(getActivity());
        });

       /* v.findViewById(R.id.addEventImageLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v)*{

                BottomFluxDialog.confirmDialog(getActivity())
                        .setTextTitle("PickUp Image")
                        .setTextMessage("Choose image source")
                        .setImageDialog(R.drawable.ui_rounded_corners_grey)
                        .setLeftButtonText("CAMERA")
                        .setRightButtonText("GALLERY")
                        .setConfirmListener(new BottomFluxDialog.OnConfirmListener() {
                            @Override
                            public void onLeftClick() { captureImageWithCrop(); }

                            @Override
                            public void onRightClick() { pickupImage(); }
                        })
                        .show();

            }
        });*/
        RecyclerView recyclerView = v.findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));
        addEventImageAdapter = new AddEventImageAdapter(getActivity());
        options = Options.init()
                .setRequestCode(100)
                .setCount(3)
                .setFrontfacing(false)
                .setImageQuality(ImageQuality.LOW)
                .setPreSelectedUrls(returnValue)
                .setScreenOrientation(com.fxn.pix.Options.SCREEN_ORIENTATION_PORTRAIT)
                .setPath("/akshay/new")
        ;
        recyclerView.setAdapter(addEventImageAdapter);
        v.findViewById(R.id.fab).setOnClickListener((View view) -> {
            options.setPreSelectedUrls(returnValue);
            Pix.start(this, options);
        });
        return v;
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

    private void clearInputFiels() {
        eventTitle.getText().clear();
        eventLocation.getText().clear();
        eventDateStart.getText().clear();
        eventDateEnd.getText().clear();
        eventTimeStart.getText().clear();
        eventTimeEnd.getText().clear();
        eventDescr.getText().clear();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch (requestCode) {
            case (100): {
                if (resultCode == Activity.RESULT_OK) {
                    returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                    addEventImageAdapter.addImage(returnValue);
                }
            }
            break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case PermUtil.REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Pix.start(getActivity(), options);
                } else {
                    Toast.makeText(getActivity(), "Approve permissions to open Pix ImagePicker",
                            Toast.LENGTH_LONG).show();
                }
                return;
            }
        }
    }


    private void showDateTimeDialog(final EditText date_time_in) {
        final Calendar calendar=Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);

                TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                        calendar.set(Calendar.MINUTE,minute);

                        SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yy-MM-dd HH:mm");

                        date_time_in.setText(simpleDateFormat.format(calendar.getTime()));
                    }
                };

                new TimePickerDialog(getActivity(),timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
            }
        };

        new DatePickerDialog(getActivity(),dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();

    }


    private void showTimeDialog(final EditText time_in) {
        final Calendar calendar=Calendar.getInstance();

        TimePickerDialog.OnTimeSetListener timeSetListener=new TimePickerDialog.OnTimeSetListener() {
            @Override
            public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                calendar.set(Calendar.HOUR_OF_DAY,hourOfDay);
                calendar.set(Calendar.MINUTE,minute);
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("HH:mm");
                time_in.setText(simpleDateFormat.format(calendar.getTime()));
            }
        };

        new TimePickerDialog(getActivity(),timeSetListener,calendar.get(Calendar.HOUR_OF_DAY),calendar.get(Calendar.MINUTE),false).show();
    }

    private void showDateDialog(final EditText date_in) {
        final Calendar calendar=Calendar.getInstance();
        DatePickerDialog.OnDateSetListener dateSetListener=new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                calendar.set(Calendar.YEAR,year);
                calendar.set(Calendar.MONTH,month);
                calendar.set(Calendar.DAY_OF_MONTH,dayOfMonth);
                SimpleDateFormat simpleDateFormat=new SimpleDateFormat("yy-MM-dd");
                date_in.setText(simpleDateFormat.format(calendar.getTime()));

            }
        };

        new DatePickerDialog(getActivity(),dateSetListener,calendar.get(Calendar.YEAR),calendar.get(Calendar.MONTH),calendar.get(Calendar.DAY_OF_MONTH)).show();
    }




}
