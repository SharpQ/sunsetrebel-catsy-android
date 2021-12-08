package com.sunsetrebel.catsy.fragments;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.libraries.places.api.Places;
import com.google.android.libraries.places.api.model.AutocompletePrediction;
import com.google.android.libraries.places.api.model.AutocompleteSessionToken;
import com.google.android.libraries.places.api.model.Place;
import com.google.android.libraries.places.api.model.TypeFilter;
import com.google.android.libraries.places.api.net.FetchPlaceRequest;
import com.google.android.libraries.places.api.net.FetchPlaceResponse;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsRequest;
import com.google.android.libraries.places.api.net.FindAutocompletePredictionsResponse;
import com.google.android.libraries.places.api.net.PlacesClient;
import com.mancj.materialsearchbar.MaterialSearchBar;
import com.mancj.materialsearchbar.adapter.SuggestionsAdapter;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.utils.GoogleMapService;
import com.sunsetrebel.catsy.utils.PermissionUtils;
import com.sunsetrebel.catsy.viewmodel.NewEventViewModel;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class NewEventMapFragment extends Fragment implements OnMapReadyCallback {
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;
    private MaterialSearchBar materialSearchBar;
    private AppCompatButton confirmLocationButton;
    private TextView locationConfirmText;
    private ImageView arrowView;
    private LatLng eventLatLng;
    private String eventAddress;
    private Geocoder geocoder;
    private GoogleMap mMap;
    private NewEventViewModel newEventViewModel;

    public NewEventMapFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_new_event_map, container, false);
        newEventViewModel = new ViewModelProvider(requireActivity()).get(NewEventViewModel.class);

        materialSearchBar = v.findViewById(R.id.searchBar);
        confirmLocationButton = v.findViewById(R.id.buttonConfirm);
        locationConfirmText = v.findViewById(R.id.locationConfirmText);
        arrowView = v.findViewById(R.id.mt_arrow);

        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.addEventBigFullMap);
        mapFragment.getMapAsync(this);
        geocoder = GoogleMapService.getGeocoderInstance(getContext());
        Places.initialize(getContext(), getString(R.string.google_maps_key));
        placesClient = Places.createClient(getContext());
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();

        arrowView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getParentFragmentManager().popBackStack();
            }
        });

        materialSearchBar.addTextChangeListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                FindAutocompletePredictionsRequest predictionsRequest = FindAutocompletePredictionsRequest.builder()
                        .setTypeFilter(TypeFilter.ADDRESS)
                        .setSessionToken(token)
                        .setQuery(s.toString())
                        .build();
                placesClient.findAutocompletePredictions(predictionsRequest).addOnCompleteListener(new OnCompleteListener<FindAutocompletePredictionsResponse>() {
                    @Override
                    public void onComplete(@NonNull Task<FindAutocompletePredictionsResponse> task) {
                        if (task.isSuccessful()) {
                            FindAutocompletePredictionsResponse predictionsResponse = task.getResult();
                            if (predictionsResponse != null) {
                                predictionList = predictionsResponse.getAutocompletePredictions();
                                List<String> suggestionsList = new ArrayList<>();
                                if (predictionList.size() <= 3) {
                                    for (int i = 0; i < predictionList.size(); i++) {
                                        AutocompletePrediction prediction = predictionList.get(i);
                                        suggestionsList.add(prediction.getFullText(null).toString());
                                    }
                                } else {
                                    for (int i = 0; i < 3; i++) {
                                        AutocompletePrediction prediction = predictionList.get(i);
                                        suggestionsList.add(prediction.getFullText(null).toString());
                                    }
                                }

                                materialSearchBar.updateLastSuggestions(suggestionsList);
                                if (!materialSearchBar.isSuggestionsVisible()) {
                                    materialSearchBar.showSuggestionsList();
                                }
                            }
                        } else {
                            Log.i("INFO", "prediction fetching task unsuccessful");
                        }
                    }
                });
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        materialSearchBar.setSuggestionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
            @Override
            public void OnItemClickListener(int position, View v) {
                if (position >= predictionList.size()) {
                    return;
                }
                AutocompletePrediction selectedPrediction = predictionList.get(position);
                eventAddress = materialSearchBar.getLastSuggestions().get(position).toString();
                materialSearchBar.setText(eventAddress);

                //Fill bottom text with address
                locationConfirmText.setText(eventAddress);
                
                materialSearchBar.clearSuggestions();
                InputMethodManager imm = (InputMethodManager) requireActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                if (imm != null)
                    imm.hideSoftInputFromWindow(materialSearchBar.getWindowToken(), InputMethodManager.HIDE_IMPLICIT_ONLY);
                final String placeId = selectedPrediction.getPlaceId();
                List<Place.Field> placeFields = Arrays.asList(Place.Field.LAT_LNG);

                FetchPlaceRequest fetchPlaceRequest = FetchPlaceRequest.builder(placeId, placeFields).build();
                placesClient.fetchPlace(fetchPlaceRequest).addOnSuccessListener(new OnSuccessListener<FetchPlaceResponse>() {
                    @Override
                    public void onSuccess(FetchPlaceResponse fetchPlaceResponse) {
                        Place place = fetchPlaceResponse.getPlace();

                        Log.i("INFO", "Place found: " + place.getName());
                        eventLatLng = place.getLatLng();
                        if (eventLatLng != null) {
                            GoogleMapService.clearAndSetMarker(mMap, eventLatLng, 12, eventAddress);
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        if (e instanceof ApiException) {
                            ApiException apiException = (ApiException) e;
                            apiException.printStackTrace();
                            int statusCode = apiException.getStatusCode();
                            Log.i("INFO", "place not found: " + e.getMessage());
                            Log.i("INFO", "status code: " + statusCode);
                        }
                    }
                });
            }

            @Override
            public void OnItemDeleteListener(int position, View v) {

            }
        });

        confirmLocationButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (eventLatLng != null) {
                    newEventViewModel.setNewEventLocation(eventAddress, eventLatLng);
                    getParentFragmentManager().beginTransaction().addToBackStack("NewEventMapFragment")
                            .replace(R.id.frameLayoutMain, new NewEventFinalFragment()).commit();
                }
            }
        });
        return v;
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        GoogleMapService.setupMapActivity(googleMap, getContext(), getActivity());

        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (materialSearchBar.isSuggestionsVisible())
                    locationConfirmText.clearComposingText();
                    materialSearchBar.clearSuggestions();
                if (materialSearchBar.isSearchOpened())
                    locationConfirmText.clearComposingText();
                materialSearchBar.closeSearch();
                return false;
            }
        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {

                List<Address> addresses = new ArrayList<>();
                materialSearchBar.clearSuggestions();
                eventLatLng = latLng;

                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    Log.d("INFO", "List of addresses from users mark: " + String.valueOf(addresses));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (addresses.size() > 0) {
                    eventAddress = addresses.get(0).getAddressLine(0);
                    GoogleMapService.clearAndSetMarker(mMap, latLng, 12, eventAddress);
                    locationConfirmText.setText(eventAddress);
                    materialSearchBar.setText(eventAddress);
                } else {
                    String eventAddressError = (String) getResources().getText(R.string.new_event_maps_popup_error_text);
                    GoogleMapService.clearAndSetMarker(mMap, latLng, 12, eventAddressError);
                    locationConfirmText.setText(eventAddressError);
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtils.getAccessLocationRequestCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                GoogleMapService.zoomToUserLocation(getContext(), mMap);
            } else {
                Log.e("INFO", "Permissions not granted");
            }
        }
    }

}
