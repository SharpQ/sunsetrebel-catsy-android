package com.sunsetrebel.catsy.fragments;

import android.app.Activity;
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
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.android.material.textview.MaterialTextView;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.utils.PermissionUtils;
import com.sunsetrebel.catsy.viewmodel.NewEventViewModel;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import nl.joery.animatedbottombar.AnimatedBottomBar;

import static android.app.Activity.RESULT_OK;


public class NewEventFinalFragment extends Fragment {
    private TextInputLayout eventDescrLayout;
    private TextInputEditText eventDescr, eventMinAge, eventMaxAge, eventMaxPeople;
    private AppCompatButton submitButton, backButton;
    private MaterialTextView mAddImageLabel;
    private ImageView mAvatarImageView;
    private static final int IMAGE_PICK_CODE = 1000;
    private Uri eventAvatarURI;
    private NewEventViewModel newEventViewModel;

    public NewEventFinalFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_event_final, container, false);
        newEventViewModel = new ViewModelProvider(requireActivity()).get(NewEventViewModel.class);

        eventDescr = v.findViewById(R.id.tiet_event_description);
        eventMinAge = v.findViewById(R.id.tiet_min_age_limit);
        eventMaxAge = v.findViewById(R.id.tiet_max_age_limit);
        eventMaxPeople = v.findViewById(R.id.tiet_event_max_people);
        submitButton = v.findViewById(R.id.button_create_new_event);
        mAvatarImageView = v.findViewById(R.id.iv_event_avatar);
        mAddImageLabel = v.findViewById(R.id.mtv_event_avatar);
        eventDescrLayout = v.findViewById(R.id.til_event_description);
        backButton = v.findViewById(R.id.button_back_new_event_final);

        backButton.setOnClickListener(v12 -> {
            getParentFragmentManager().popBackStack();
        });

        submitButton.setOnClickListener(v1 -> {
            eventDescrLayout.setError(null);
            String eventDescrValue = eventDescr.getText().toString().trim();
            Integer eventMinAgeValue = null, eventMaxAgeValue = null, eventMaxPeopleValue = null;

            if (TextUtils.isEmpty(eventDescrValue)) {
                eventDescrLayout.setError(getResources().getString(R.string.new_event_fill_mandatory_field_error));
                return;
            }

            if (!TextUtils.isEmpty(eventMinAge.getText().toString())) {
                eventMinAgeValue = Integer.parseInt(eventMinAge.getText().toString().trim());
            }

            if (!TextUtils.isEmpty(eventMaxAge.getText().toString())) {
                eventMaxAgeValue = Integer.parseInt(eventMaxAge.getText().toString().trim());
            }

            if (!TextUtils.isEmpty(eventMaxPeople.getText().toString())) {
                eventMaxPeopleValue = Integer.parseInt(eventMaxPeople.getText().toString().trim());
            }

            newEventViewModel.completeNewEventInfo(getContext(), eventDescrValue, eventAvatarURI, eventMinAgeValue, eventMaxAgeValue, eventMaxPeopleValue);
            getParentFragmentManager().beginTransaction().replace(R.id.frameLayoutMain, new EventListFragment()).commit();
            AnimatedBottomBar animatedBottomBar = getActivity().findViewById(R.id.animatedBottomBar);
            animatedBottomBar.selectTabById(R.id.navigationBarEventList, true);
        });

        mAvatarImageView.setOnClickListener(v17 -> {
            if (PermissionUtils.isGalleryPermissionEnabled(getContext())) {
                pickImageFromGallery();
            } else {
                PermissionUtils.requestGalleryPermissionsFragment(NewEventFinalFragment.this);
            }
        });
        return v;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        switch(requestCode) {
            case IMAGE_PICK_CODE: //When image picked
                if (resultCode == RESULT_OK && data != null) {
                    Uri selectedImage = data.getData();
                    CropImage.activity(selectedImage)
                            .setGuidelines(CropImageView.Guidelines.ON)
                            .setMinCropResultSize(100, 100)
                            .setMaxCropResultSize(2000, 2000)
                            .setAspectRatio(1, 1)
                            .start(getContext(), this);
                }
                break;
            case CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE: //When image is cropped
                CropImage.ActivityResult result = CropImage.getActivityResult(data);
                if (resultCode == RESULT_OK) {
                    Uri resultUri = result.getUri();
                    eventAvatarURI = resultUri;
                    mAvatarImageView.setImageURI(resultUri);
                    mAddImageLabel.setVisibility(View.INVISIBLE);
                } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                    Exception error = result.getError();
                    Log.d("DEBUG", error.toString());
                }
                break;
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PermissionUtils.getAccessGalleryRequestCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                pickImageFromGallery();
            } else {
                Log.d("DEBUG", "Permissions not granted");
            }
        }
    }

    private void pickImageFromGallery() {
        Intent intent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent, IMAGE_PICK_CODE);
    }
}
