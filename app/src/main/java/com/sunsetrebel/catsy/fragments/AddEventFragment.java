package com.sunsetrebel.catsy.fragments;

import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.RecyclerView;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.models.AddEvent;
import com.sunsetrebel.catsy.utils.FirebaseAuthService;
import com.sunsetrebel.catsy.utils.FirebaseFirestoreService;
import java.util.ArrayList;
import java.util.List;


public class AddEventFragment extends Fragment {
    private RecyclerView recyclerPostagem;
    private com.google.firebase.auth.FirebaseAuth fAuth;
    private final FirebaseAuthService firebaseAuthService = new FirebaseAuthService();
    private final FirebaseFirestoreService firebaseFirestoreService = new FirebaseFirestoreService();
    private List<AddEvent> postagens = new ArrayList<>();
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
            // populateInfoTv(name, age, hasAccepted);
            if (TextUtils.isEmpty(eventNameValue) || TextUtils.isEmpty(eventLocationValue) || TextUtils.isEmpty(eventDateValue) || TextUtils.isEmpty(eventTypeValue) || TextUtils.isEmpty(eventDescrValue)) {
                return;
            }
            prepararPostagens();
            firebaseFirestoreService.createNewEvent(fAuth.getCurrentUser().getUid(), eventNameValue, eventLocationValue, eventDateValue, eventTypeValue, eventDescrValue);
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

    public void addPostagens(String name, String date, String location, String event_description, String event_creator_name,
                             int event_image, int event_creator_photo) {
        AddEvent post = new AddEvent(
                "Kiev flight trip",
                "Tomorrow at 20:00",
                "Kiev, Podol",
                "Waiting for you!",
                "Sonya",
                R.drawable.im_event_icon_example_1,
                R.drawable.im_cat_bright
        );
        this.postagens.add(post);}

    public void prepararPostagens() {
        AddEvent post = new AddEvent(
                "Kiev flight trip",
                "Tomorrow at 20:00",
                "Kiev, Podol",
                "Waiting for you!",
                "Sonya",
                R.drawable.im_event_icon_example_1,
                R.drawable.im_cat_bright
        );
        this.postagens.add(post);

        post = new AddEvent(
                "Downtown excurtion",
                "20.06.2021 at 13:00",
                "Kiev, Maidan",
                "You will see Kiev",
                "Valentin",
                R.drawable.im_event_icon_example_2,
                R.drawable.im_cat_profile);
        this.postagens.add(post);

        post = new AddEvent("Paris in Kiev afterparty",
                "Today at 19:00",
                "Kiev, France Q",
                "Come to Paris quarter",
                "Joseph",
                R.drawable.im_event_icon_example_3,
                R.drawable.ic_catsy_icon
        );
        this.postagens.add(post);

        post = new AddEvent("Masha forest survive",
                "30.06.2021 at 10:00",
                "Kiev, Bilychi",
                "1 knife - 1 life",
                "Masha",
                R.drawable.im_event_icon_example_4,
                R.drawable.im_cat_dark_40);
        this.postagens.add(post);
    }
}
