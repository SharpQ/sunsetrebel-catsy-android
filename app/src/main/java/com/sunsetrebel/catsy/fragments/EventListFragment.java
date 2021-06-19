package com.sunsetrebel.catsy.fragments;

import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.adapters.AddEventAdapter;
import com.sunsetrebel.catsy.models.AddEvent;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class EventListFragment extends Fragment {

    public EventListFragment() {
        // Required empty public constructor
    }
    private RecyclerView recyclerPostagem;
    private List<AddEvent> postagens = new ArrayList<>();

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

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Eventlist fragment creation
        View v = inflater.inflate(R.layout.fragment_event_list, container, false);
        recyclerPostagem = v.findViewById(R.id.list_background);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerPostagem.setLayoutManager(layoutManager);

        //Fill EventList with events
        this.prepararPostagens();
       AddEventAdapter adapter = new AddEventAdapter(postagens);
        recyclerPostagem.setAdapter(adapter);

        return v;

    }
}
