package com.sunsetrebel.catsy.fragments;

import android.os.Bundle;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.adapters.AddEventDataAdapter;
import com.sunsetrebel.catsy.models.AddEventModel;
import com.sunsetrebel.catsy.utils.EventListService;
import com.sunsetrebel.catsy.utils.FirebaseFirestoreService;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import me.everything.android.ui.overscroll.VerticalOverScrollBounceEffectDecorator;
import me.everything.android.ui.overscroll.adapters.RecyclerViewOverScrollDecorAdapter;


public class EventListFragment extends Fragment {
    private final FirebaseFirestoreService firebaseFirestoreService = new FirebaseFirestoreService();
    private RecyclerView recyclerPostagem;
    private List<AddEventModel> postagens = new ArrayList<>();

    public EventListFragment() {
        // Required empty public constructor
    }

    private void addEventToList(String eventName, String eventDate, String eventLocation,
                               String eventDescr, String eventAuthor) {
        AddEventModel post = new AddEventModel(
                eventName,
                eventDate,
                eventLocation,
                eventDescr,
                eventAuthor,
                R.drawable.im_event_icon_example_1,
                R.drawable.im_cat_bright
        );
        this.postagens.add(post);
       /** Future code to make image background rounded
        * recyclerPostagem.setBackgroundResource(R.drawable.ui_transparent_rounded_corners);**/
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        //Eventlist fragment creation
        View v = inflater.inflate(R.layout.fragment_event_list, container, false);
        recyclerPostagem = v.findViewById(R.id.list_background);
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(getActivity());
        recyclerPostagem.setLayoutManager(layoutManager);
        RecyclerView recyclerView = (RecyclerView) v.findViewById(R.id.list_background);
        new VerticalOverScrollBounceEffectDecorator(new RecyclerViewOverScrollDecorAdapter(recyclerView));
        //Fill EventList with events
        if (EventListService.getListUpdateStatus()) {
            firebaseFirestoreService.getEventList(events -> {
                EventListService.setCurrentEventList(events);
                for (Map<String, Object> event : events) {
                    addEventToList(event.get("eventName").toString(), event.get("eventDate").toString(),
                            event.get("eventLocation").toString(), event.get("eventDescr").toString(),
                            event.get("userName").toString());
                    Log.d("INFO", String.valueOf(postagens));
                    AddEventDataAdapter adapter = new AddEventDataAdapter(postagens);
                    recyclerPostagem.setAdapter(adapter);
                }
            });
        } else {
            Log.d("INFO", "NO EVENT LIST UPDATE NEEDED");
            List<Map<String, Object>> eventListPreviousResponse = EventListService.getCurrentEventList();
            if (eventListPreviousResponse != null) {
                for (Map<String, Object> event : eventListPreviousResponse) {
                    addEventToList(event.get("eventName").toString(), event.get("eventDate").toString(),
                            event.get("eventLocation").toString(), event.get("eventDescr").toString(),
                            event.get("userName").toString());
                }
            }
            Log.d("INFO", String.valueOf(postagens));
            AddEventDataAdapter adapter = new AddEventDataAdapter(postagens);
            recyclerPostagem.setAdapter(adapter);
        }
        return v;
    }
}
