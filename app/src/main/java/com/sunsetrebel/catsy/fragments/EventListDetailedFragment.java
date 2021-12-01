package com.sunsetrebel.catsy.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.appcompat.widget.AppCompatTextView;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.models.EventModel;
import com.sunsetrebel.catsy.viewmodel.EventListViewModel;


public class EventListDetailedFragment extends Fragment {
    private EventListViewModel eventListViewModel;
    private EventModel eventModel;
    private AppCompatTextView title1, title2, title3;

    public EventListDetailedFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_list_detailed, container, false);
        eventListViewModel = new ViewModelProvider(requireActivity()).get(EventListViewModel.class);
        eventListViewModel.init();

        title1 = v.findViewById(R.id.tv_title1);
        title2 = v.findViewById(R.id.tv_title2);
        title3 = v.findViewById(R.id.tv_title3);

        eventModel = eventListViewModel.getSelectedEvent();

        title1.setText(eventModel.getEventTitle());
        title2.setText(eventModel.getUserName());
        title3.setText(eventModel.getEventLocation());
        return v;
    }
}
