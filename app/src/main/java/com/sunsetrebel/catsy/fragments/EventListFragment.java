package com.sunsetrebel.catsy.fragments;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;

import androidx.activity.OnBackPressedCallback;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.mancj.materialsearchbar.MaterialSearchBar;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.adapters.EventListAdapter;
import com.sunsetrebel.catsy.viewmodel.EventListViewModel;


public class EventListFragment extends Fragment {
    private RecyclerView eventRecycler;
    private EventListViewModel eventListViewModel;
    private EventListAdapter eventListAdapter;
    private ProgressBar progressBar;
    private MaterialSearchBar materialSearchBar;

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
        progressBar = v.findViewById(R.id.pb_event_list);
        materialSearchBar = v.findViewById(R.id.searchViewEventList);
        progressBar.setVisibility(View.VISIBLE);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        eventRecycler.setLayoutManager(layoutManager);
        DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(eventRecycler.getContext(),
                layoutManager.getOrientation());
        eventRecycler.addItemDecoration(dividerItemDecoration);
        //Init viewmodel
        eventListViewModel = new ViewModelProvider(this).get(EventListViewModel.class);
        eventListViewModel.init();
        //Get event list through viewmodel
        eventListViewModel.getLiveEventListData().observe(getViewLifecycleOwner(), eventList -> {
            eventListAdapter = new EventListAdapter(this, eventList);
            eventRecycler.setAdapter(eventListAdapter);
            eventListAdapter.notifyDataSetChanged();
            progressBar.setVisibility(View.INVISIBLE);
        });

        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                eventListViewModel.setSearchMutableLiveData(s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

//        requireActivity().getOnBackPressedDispatcher().addCallback(new OnBackPressedCallback(true
//        ) {
//            @Override
//            public void handleOnBackPressed() {
//
//            }
//        });

        return v;
    }

    @Override
    public void onStop() {
        super.onStop();
        eventListViewModel.removeEventListListener();
    }
}
