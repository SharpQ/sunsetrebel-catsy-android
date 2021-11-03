package com.sunsetrebel.catsy.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.adapters.EventListAdapter;
import com.sunsetrebel.catsy.viewmodel.EventListViewModel;


public class EventListFragment extends Fragment {
    private RecyclerView eventRecycler;
    private EventListViewModel eventListViewModel;
    private EventListAdapter eventListAdapter;

    public EventListFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) { super.onCreate(savedInstanceState); }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_event_list, container, false);
        eventRecycler = v.findViewById(R.id.recyclerViewEventList);
        eventRecycler.setLayoutManager(new LinearLayoutManager(getContext()));
        //Init viewmodel
        eventListViewModel = new ViewModelProvider(this).get(EventListViewModel.class);
        eventListViewModel.init();
        //Get event list through viewmodel
        eventListViewModel.getLiveEventListData().observe(getViewLifecycleOwner(), eventList -> {
            eventListAdapter = new EventListAdapter(getContext(), eventList);
            eventRecycler.setAdapter(eventListAdapter);
            eventListAdapter.notifyDataSetChanged();
        });
        return v;
    }
}
