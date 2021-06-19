package com.sunsetrebel.catsy.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.models.AddEvent;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.RecyclerView;

import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;

import java.util.ArrayList;
import java.util.List;


public class AddEventFragment extends  Fragment{
    private RecyclerView recyclerPostagem;
    private List<AddEvent> postagens = new ArrayList<>();
    public AddEventFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_add_event, container, false);

        //Initializing the views of the dialog.
        final EditText textName = v.findViewById(R.id.card_event_name);
        final EditText textDate = v.findViewById(R.id.card_event_date);
        final EditText  textLocation = v.findViewById(R.id.card_event_location);
        final EditText  textEventDescription = v.findViewById(R.id.card_event_detail_description);
        final EditText textEventCreatorName = v.findViewById(R.id.event_type);
        final CheckBox termsCb = v.findViewById(R.id.terms_cb);
        Button submitButton = v.findViewById(R.id.submit_button);
        submitButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String name = textName.getText().toString();
                String date = textDate.getText().toString();
                String location = textLocation.getText().toString();
                String event_description = textEventDescription.getText().toString();
                String event_creator_name = textEventCreatorName.getText().toString();
                // populateInfoTv(name, age, hasAccepted);
                prepararPostagens();
            }
        });


        return v;
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
