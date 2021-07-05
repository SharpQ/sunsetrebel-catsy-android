package com.sunsetrebel.catsy.fragments;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
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

import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.haerul.bottomfluxdialog.BottomFluxDialog;
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo;
import com.miguelbcr.ui.rx_paparazzo2.entities.FileData;
import com.miguelbcr.ui.rx_paparazzo2.entities.Response;
import com.miguelbcr.ui.rx_paparazzo2.entities.size.OriginalSize;
import com.miguelbcr.ui.rx_paparazzo2.entities.size.Size;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.activities.MapsActivity;
import com.sunsetrebel.catsy.activities.PickerUtil;
import com.sunsetrebel.catsy.adapters.ImagesAdapter;
import com.sunsetrebel.catsy.utils.FirebaseAuthService;
import com.sunsetrebel.catsy.utils.FirebaseFirestoreService;
import com.yalantis.ucrop.UCrop;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.schedulers.Schedulers;
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

    public AddEventFragment() {
        // Required empty public constructor
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

            eventLocation.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(getApplicationContext(), AddEventMapsFragment.class));
                }
            });

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

        //Bottom sheet camera/gallery choice
        v.findViewById(R.id.addEventImageLayout).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

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

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        fileDataList = new ArrayList<>();
        if (savedInstanceState != null) {
            if (savedInstanceState.containsKey(STATE_FILES)) {
                List files = (List) savedInstanceState.getSerializable(STATE_FILES);
                fileDataList.addAll(files);
            }
        }

        size = new OriginalSize();

        initViews();
    }

    private void initViews() {
        View view = getView();

        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        recyclerView = (RecyclerView) view.findViewById(R.id.rv_images);
/*        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(layoutManager);
        loadImages();*/
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

    private void captureImageWithCrop() {
        UCrop.Options options = new UCrop.Options();
        options.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorAccent));
        options.setToolbarTitle("Cropping single photo");
        options.withAspectRatio(25, 75);

        OriginalSize size = new OriginalSize();

        Observable<Response<AddEventFragment, FileData>> takePhotoAndCrop = pickSingle(options, size)
                .usingCamera();

        processSingle(takePhotoAndCrop);
    }

    private void pickupImage() {
        UCrop.Options options = new UCrop.Options();
        options.setToolbarColor(ContextCompat.getColor(getContext(), R.color.colorPrimaryDark));
        options.setToolbarTitle("Cropping single image");
        options.withAspectRatio(25, 75);
        Observable<Response<AddEventFragment, FileData>> takePhotoAndCrop = pickSingle(options, size)
                .usingFiles();

        processSingle(takePhotoAndCrop);
    }

    private void processSingle(Observable<Response<AddEventFragment, FileData>> pickUsingGallery) {
        pickUsingGallery
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(response -> {
                    if (PickerUtil.checkResultCode(getContext(), response.resultCode())) {
                        response.targetUI().loadImage(response.data());
                    }
                }, throwable -> {
                    throwable.printStackTrace();
                    Toast.makeText(getContext(), "ERROR " + throwable.getMessage(), Toast.LENGTH_SHORT).show();
                });
    }

    private RxPaparazzo.SingleSelectionBuilder<AddEventFragment> pickSingle(UCrop.Options options, Size size) {
        this.size = size;

        RxPaparazzo.SingleSelectionBuilder<AddEventFragment> resized = RxPaparazzo.single(this)
                .sendToMediaScanner()
                .size(size);

        if (options != null) {
            resized.crop(options);
        }

        return resized;
    }

    void loadImage(FileData fileData) {
        this.fileDataList = new ArrayList<>();
        this.fileDataList.add(fileData);

        loadImages();
    }

    private void loadImages() {
        this.fileDataList = new ArrayList<>(fileDataList);

        loadImages(fileDataList);
    }

    private void loadImages(List<FileData> fileDataList) {
        if (fileDataList == null || fileDataList.isEmpty()) {
            return;
        }


        recyclerView.setVisibility(View.VISIBLE);
        recyclerView.setAdapter(new ImagesAdapter(fileDataList));
    }


    public List<FileData> getFileDatas() {
        return fileDataList;
    }


    public List<String> getFilePaths() {
        List<String> filesPaths = new ArrayList<>();
        for (FileData fileData : fileDataList) {
            filesPaths.add(fileData.getFile().getAbsolutePath());
        }

        return filesPaths;
    }

    public Size getSize() {
        return size;
    }
}
