package com.sunsetrebel.catsy.activities;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageButton;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.AppCompatButton;
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
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class AddEventMapsActivity extends AppCompatActivity implements OnMapReadyCallback {
    private PlacesClient placesClient;
    private List<AutocompletePrediction> predictionList;
    private MaterialSearchBar materialSearchBar;
    private AppCompatButton confirmLocationButton;
    private ImageButton backButton;
    private TextView locationConfirmText;
    private LatLng eventLatLng;
    private String eventAddress;
    private Geocoder geocoder;
    private GoogleMap mMap;

    private void hideSystemUI() {
        View decorView = getWindow().getDecorView();
        decorView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_IMMERSIVE
                | View.SYSTEM_UI_FLAG_LAYOUT_STABLE
                | View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN);
        getWindow().setStatusBarColor(Color.parseColor("#00000000"));
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event_map);
        hideSystemUI();

        materialSearchBar = findViewById(R.id.searchBar);
        confirmLocationButton = findViewById(R.id.buttonConfirm);
        locationConfirmText = findViewById(R.id.locationConfirmText);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.addEventBigFullMap);
        mapFragment.getMapAsync(this);
        geocoder = GoogleMapService.getGeocoderInstance(getApplicationContext());
        Places.initialize(AddEventMapsActivity.this, getString(R.string.google_maps_key));
        placesClient = Places.createClient(this);
        final AutocompleteSessionToken token = AutocompleteSessionToken.newInstance();


        materialSearchBar.setOnSearchActionListener(new MaterialSearchBar.OnSearchActionListener() {
            @Override
            public void onSearchStateChanged(boolean enabled) {

            }

            @Override
            public void onSearchConfirmed(CharSequence text) {
                startSearch(text.toString(), true, null, true);
            }

            @Override
            public void onButtonClicked(int buttonCode) {
                if (buttonCode == MaterialSearchBar.BUTTON_NAVIGATION) {
                    /** Opening or closing a navigation drawer **/
                } else if (buttonCode == MaterialSearchBar.BUTTON_BACK) {
                    materialSearchBar.disableSearch();
                }
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
                                for (int i = 0; i < predictionList.size(); i++) {
                                    AutocompletePrediction prediction = predictionList.get(i);
                                    suggestionsList.add(prediction.getFullText(null).toString());
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

        materialSearchBar.setSuggstionsClickListener(new SuggestionsAdapter.OnItemViewClickListener() {
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
                InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
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
                            GoogleMapService.clearAndSetMarker(eventLatLng, mMap, 12);
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
                    Intent resultIntent = new Intent();
                    resultIntent.putExtra("EVENT_LAT_LNG", eventLatLng);
                    resultIntent.putExtra("EVENT_ADDRESS", eventAddress);
                    setResult(Activity.RESULT_OK, resultIntent);
                    finish();
                }
            }
        });

        backButton = findViewById(R.id.imageButtonBack);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        GoogleMapService.setupMapActivity(googleMap, getApplicationContext(), AddEventMapsActivity.this);

        googleMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (materialSearchBar.isSuggestionsVisible())
                    locationConfirmText.clearComposingText();
                    materialSearchBar.clearSuggestions();
                if (materialSearchBar.isSearchEnabled())
                    locationConfirmText.clearComposingText();
                materialSearchBar.disableSearch();
                return false;
            }
        });

        googleMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                GoogleMapService.clearAndSetMarker(latLng, mMap, 12);

                List<Address> addresses = null;

                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1); // Here 1 represent max location result to returned, by documents it recommended 1 to 5
                    Log.d("INFO", "List of addresses from users mark: " + String.valueOf(addresses));
                } catch (IOException e) {
                    e.printStackTrace();
                }

                eventLatLng = latLng;
                if (addresses == null || !addresses.isEmpty()) {
                    eventAddress = addresses.get(0).getAddressLine(0);
                    locationConfirmText.setText(eventAddress);
                } else {
                    locationConfirmText.setText("Can't identify address. Please use search field");
                }
            }
        });
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == PermissionUtils.getAccessLocationRequestCode()) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                GoogleMapService.zoomToUserLocation(getApplicationContext(), mMap);
            } else {
                Log.e("INFO", "Permissions not granted");
            }
        }
    }

}
