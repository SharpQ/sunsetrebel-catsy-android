package com.sunsetrebel.catsy.fragments;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.utils.FirebaseAuthService;
import com.sunsetrebel.catsy.utils.FirebaseFirestoreService;


public class AddEventFragment extends Fragment {
    private com.google.firebase.auth.FirebaseAuth fAuth;
    private final FirebaseAuthService firebaseAuthService = new FirebaseAuthService();
    private final FirebaseFirestoreService firebaseFirestoreService = new FirebaseFirestoreService();
    EditText eventName, eventLocation, eventDate, eventType, eventDescr;
    Button submitButton;

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
        eventDate = v.findViewById(R.id.card_event_date);
        eventType = v.findViewById(R.id.event_type);
        eventDescr = v.findViewById(R.id.card_event_detail_description);
        submitButton = v.findViewById(R.id.submit_button);

        submitButton.setOnClickListener(v1 -> {
            String eventNameValue = eventName.getText().toString().trim();
            String eventLocationValue = eventLocation.getText().toString().trim();
            String eventDateValue = eventDate.getText().toString().trim();
            String eventTypeValue = eventType.getText().toString().trim();
            String eventDescrValue = eventDescr.getText().toString().trim();
            if (TextUtils.isEmpty(eventNameValue) || TextUtils.isEmpty(eventLocationValue) || TextUtils.isEmpty(eventDateValue) || TextUtils.isEmpty(eventTypeValue) || TextUtils.isEmpty(eventDescrValue)) {
                return;
            }
            firebaseFirestoreService.getUserNameInFirestore(value -> {
                firebaseFirestoreService.createNewEvent(fAuth.getCurrentUser().getUid(), eventNameValue, eventLocationValue, eventDateValue, eventTypeValue, eventDescrValue, value);
            }, fAuth.getUid());
            clearInputFiels();
        });

        return v;
    }

    private void clearInputFiels() {
        eventName.getText().clear();
        eventLocation.getText().clear();
        eventDate.getText().clear();
        eventType.getText().clear();
        eventDescr.getText().clear();
    }
}
