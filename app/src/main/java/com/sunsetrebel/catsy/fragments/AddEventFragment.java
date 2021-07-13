package com.sunsetrebel.catsy.fragments;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.blogspot.atifsoftwares.animatoolib.Animatoo;
import com.fxn.pix.Pix;
import com.fxn.utility.ImageQuality;
import com.fxn.utility.PermUtil;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData;
import com.miguelbcr.ui.rx_paparazzo2.entities.size.Size;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.activities.AddEventMapActivity;
import com.sunsetrebel.catsy.activities.MapsActivity;
import com.sunsetrebel.catsy.adapters.MyAdapter;
import com.sunsetrebel.catsy.utils.FirebaseAuthService;
import com.sunsetrebel.catsy.utils.FirebaseFirestoreService;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;

import com.fxn.pix.Options;

import me.everything.android.ui.overscroll.OverScrollDecoratorHelper;

import static com.facebook.FacebookSdk.getApplicationContext;


public class AddEventFragment extends Fragment {
    private com.google.firebase.auth.FirebaseAuth fAuth;
    private final FirebaseAuthService firebaseAuthService = new FirebaseAuthService();
    private final FirebaseFirestoreService firebaseFirestoreService = new FirebaseFirestoreService();
    EditText eventName, eventLocation, eventDateStart, eventDateEnd, eventTimeStart, eventTimeEnd, eventType, eventDescr;
    Button submitButton;
    private GoogleMap mMap;
    private Geocoder geocoder;
    //Pick image variables
    private RecyclerView recyclerView;
    private ArrayList<FileData> fileDataList;
    private Size size;
    private static final String STATE_FILES = "FILES";
    RecyclerView recyclerViewEventImage;
    MyAdapter myAdapter;

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    Options options;
    ArrayList<String> returnValue = new ArrayList<>();

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

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_event, container, false);
        fAuth = firebaseAuthService.getInstance();

        //Initializing the views of the dialog.
        eventName = v.findViewById(R.id.card_event_name);
        eventLocation = v.findViewById(R.id.card_event_location);
       eventDateStart = v.findViewById(R.id.card_event_date);
        eventDateEnd = v.findViewById(R.id.card_event_date_end);
        eventTimeStart = v.findViewById(R.id.card_event_time_start);
        eventTimeEnd = v.findViewById(R.id.card_event_time_end);
        eventType = v.findViewById(R.id.event_type);
        eventDescr = v.findViewById(R.id.card_event_detail_description);
        submitButton = v.findViewById(R.id.submit_button);

        submitButton.setOnClickListener(v1 -> {
            String eventNameValue = eventName.getText().toString().trim();
            String eventLocationValue = eventLocation.getText().toString().trim();
            String eventDateValue = eventDateStart.getText().toString().trim();
            String eventDateEndValue = eventDateEnd.getText().toString().trim();
            String eventTimeStartValue = eventTimeStart.getText().toString().trim();
            String eventTimeEndValue = eventTimeEnd.getText().toString().trim();
            String eventTypeValue = eventType.getText().toString().trim();
            String eventDescrValue = eventDescr.getText().toString().trim();

            SupportMapFragment mapFragment = ((SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.addEventSmallMap));
            mapFragment.getMapAsync((OnMapReadyCallback) this);
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

            /*eventLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment addEventMapsFragment = new AddEventFragment();
                    FragmentTransaction transaction = getFragmentManager().beginTransaction();
                    transaction.replace(R.id.fragment_container, addEventMapsFragment);
                    transaction.addToBackStack(null);
                    transaction.commit();
                }
            });*/

            if (TextUtils.isEmpty(eventNameValue) || TextUtils.isEmpty(eventLocationValue) || TextUtils.isEmpty(eventDateValue) || TextUtils.isEmpty(eventDateEndValue)|| TextUtils.isEmpty(eventTypeValue) || TextUtils.isEmpty(eventDescrValue)) {
                return;
            }
            firebaseFirestoreService.getUserNameInFirestore(value -> {
                firebaseFirestoreService.createNewEvent(fAuth.getCurrentUser().getUid(), eventNameValue, eventLocationValue, eventDateValue, eventTypeValue, eventDescrValue, value);
            }, fAuth.getUid());

            clearInputFiels();
        });

        eventDateStart.setInputType(InputType.TYPE_NULL);
        eventDateEnd.setInputType(InputType.TYPE_NULL);
        eventTimeStart.setInputType(InputType.TYPE_NULL);
        eventDateEnd.setInputType(InputType.TYPE_NULL);

        MapsActivity mapsAct = new MapsActivity();


        ScrollView scrollView = (ScrollView) v.findViewById(R.id.addEventScrollView);
        OverScrollDecoratorHelper.setUpOverScroll(scrollView);



        v.findViewById(R.id.card_event_location).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(getApplicationContext(), AddEventMapActivity.class));
                Animatoo.animateShrink(getActivity());

            } });

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
        myAdapter = new MyAdapter(getActivity());
        options = Options.init()
                .setRequestCode(100)
                .setCount(3)
                .setFrontfacing(false)
                .setImageQuality(ImageQuality.LOW)
                .setPreSelectedUrls(returnValue)
                .setScreenOrientation(com.fxn.pix.Options.SCREEN_ORIENTATION_PORTRAIT)
                .setPath("/akshay/new")
        ;
        recyclerView.setAdapter(myAdapter);
        v.findViewById(R.id.fab).setOnClickListener((View view) -> {
            options.setPreSelectedUrls(returnValue);
            Pix.start(this, options);
        });
        return v;
    }

    private void clearInputFiels() {
        eventName.getText().clear();
        eventLocation.getText().clear();
        eventDateStart.getText().clear();
        eventDateEnd.getText().clear();
        eventTimeStart.getText().clear();
        eventTimeEnd.getText().clear();
        eventType.getText().clear();
        eventDescr.getText().clear();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.e("val", "requestCode ->  " + requestCode+"  resultCode "+resultCode);
        switch (requestCode) {
            case (100): {
                if (resultCode == Activity.RESULT_OK) {
                    returnValue = data.getStringArrayListExtra(Pix.IMAGE_RESULTS);
                    myAdapter.addImage(returnValue);
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
