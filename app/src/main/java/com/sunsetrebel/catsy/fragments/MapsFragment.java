package com.sunsetrebel.catsy.fragments;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MapStyleOptions;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.miguelbcr.ui.rx_paparazzo2.RxPaparazzo;
import com.sunsetrebel.catsy.R;
import com.sunsetrebel.catsy.activities.HomeActivity;
import com.sunsetrebel.catsy.models.AddEventModel;
import android.location.Location;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import org.jetbrains.annotations.NotNull;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import nl.joery.animatedbottombar.AnimatedBottomBar;



public class MapsFragment extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener {
    //Variables for map
    private static final String TAG = "MapsFragment";
    private GoogleMap mMap;
    private Geocoder geocoder;
    private int ACCESS_LOCATION_REQUEST_CODE = 10001;
    FusedLocationProviderClient fusedLocationProviderClient;
    //Event input dialog data
    TextView infoTv;
    private List<AddEventModel> postagens = new ArrayList<>();
    private RecyclerView recyclerPostagem;
    private static final String TAGG = MapsFragment.class.getSimpleName();

    AnimatedBottomBar animatedBottomBar;
    FragmentManager fragmentManager;

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
        setContentView(R.layout.fragment_maps);
        hideSystemUI();
        RxPaparazzo.register(getApplication());
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        ImageView MapFilter;
        MapFilter = findViewById(R.id.map_filter);

        try {
            assert mapFragment.getView() != null;
            final ViewGroup parent = (ViewGroup) mapFragment.getView().findViewWithTag("Compass").getParent();
            parent.post(new Runnable() {
                @Override
                public void run() {
                }
            });
        } catch (Exception ex) {
            ex.printStackTrace();
        }

        //Event creation dialog opening
        FloatingActionButton fab;
       /* fab = findViewById(R.id.fab);
        fab = findViewById(R.id.fab);*/
        infoTv = findViewById(R.id.info_tv);

/*        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showCustomDialog();
            }
        });*/
        recyclerPostagem = findViewById(R.id.list_background);
        // Definir layout
        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
/*        recyclerPostagem.setLayoutManager(layoutManager);
        this.prepararPostagens();
        AddEventDataAdapter adapter = new AddEventDataAdapter(postagens);
        recyclerPostagem.setAdapter(adapter);*/
        setTitle("Example 1");

        animatedBottomBar = findViewById(R.id.animatedBottomBar);

        if (savedInstanceState == null) {
            animatedBottomBar.selectTabById(R.id.menu_map, true);
            fragmentManager = getSupportFragmentManager();
            HomeActivity homeActivity = new HomeActivity();
            fragmentManager.beginTransaction().replace(R.id.fragment_container, homeActivity)
                    .commit();
        }

        animatedBottomBar.setOnTabSelectListener(new AnimatedBottomBar.OnTabSelectListener() {
            @Override
            public void onTabSelected(int lastIndex, @Nullable AnimatedBottomBar.Tab lastTab, int newIndex, @NotNull AnimatedBottomBar.Tab newTab) {
                Fragment fragment = null;
                switch (newTab.getId()) {
                    case R.id.menu_map:
                        fragment = new HomeActivity();
                        break;
                    case R.id.menu_event_list:
                        fragment = new EventListFragment();
                        break;
                    case R.id.menu_add_event:
                        fragment = new AddEventFragment();
                        break;
                    case R.id.menu_message:
                        fragment = new MessageFragment();
                        break;
                    case R.id.menu_account:
                        fragment = new AccountFragment();
                        break;
                }

                if (fragment != null) {
                    fragmentManager = getSupportFragmentManager();
                    fragmentManager.beginTransaction().setCustomAnimations(R.anim.bottom_in, R.anim.slide_in_right).replace(R.id.fragment_container, fragment)
                            .commit();
                  //  animationLibrary.rightEnterLeftOut();
                } else {
                    Log.e(TAGG, "Error in creating Fragment");
                }
            }
        });

    }


    public void prepararPostagens() {
        AddEventModel post = new AddEventModel(

        );
        this.postagens.add(post);}

    void populateInfoTv(String name, String age, Boolean hasAcceptedTerms) {
        infoTv.setVisibility(View.VISIBLE);
        String acceptedText = "have";
        if (!hasAcceptedTerms) {
            acceptedText = "have not";
        }
        infoTv.setText(String.format(getString(R.string.info), name, age, acceptedText));
    }



    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {

        try {
            // Customise the styling of the base map using a JSON object defined
            // in a raw resource file.
            boolean success = googleMap.setMapStyle(
                    MapStyleOptions.loadRawResourceStyle(
                            getApplicationContext(), R.raw.google_style));

            if (!success) {
                Log.e(TAG, "Style parsing failed.");
            }
        } catch (Resources.NotFoundException e) {
            Log.e(TAG, "Can't find style. Error: ", e);
        }
        mMap = googleMap;
        //Google maps default buttons disabling
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.getUiSettings().setCompassEnabled(false);
        mMap.getUiSettings().setRotateGesturesEnabled(false);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            enableUserLocation();
            zoomToUserLocation();
        } else {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
                //We can show user a dialog why this permission is necessary
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            } else {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, ACCESS_LOCATION_REQUEST_CODE);
            }

        }

        try {
            List<Address> addresses = geocoder.getFromLocationName("Current location", 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                LatLng curr_location = new LatLng(address.getLatitude(), address.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(curr_location).icon(BitmapDescriptorFactory.fromResource(R.drawable.im_cat_location_sample_35))
                        .title(address.getLocality());
                mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curr_location, 12));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Event examples
        LatLng FirstEvent = new LatLng(50.436404, 30.369498);
        googleMap.addMarker(new MarkerOptions().position(FirstEvent).icon(BitmapDescriptorFactory.fromResource(R.drawable.im_cat_blue_40)).title(String.valueOf(FirstEvent)));

        LatLng SecondEvent = new LatLng(50.449, 30.512850);
        googleMap.addMarker(new MarkerOptions().position(SecondEvent).icon(BitmapDescriptorFactory.fromResource(R.drawable.im_cat_blue_40)).title("Guitar night at 20:00"));

        LatLng ThirdEvent = new LatLng(50.391566, 30.481428);
        googleMap.addMarker(new MarkerOptions().position(ThirdEvent).icon(BitmapDescriptorFactory.fromResource(R.drawable.im_cat_blue_40)).title(String.valueOf(ThirdEvent)));
        LatLng mountainView = new LatLng(37.4, -122.1);

    }

    private void enableUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        mMap.setMyLocationEnabled(true);
    }

    private void zoomToUserLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            return;
        }
        Task<Location> locationTask = fusedLocationProviderClient.getLastLocation();
        locationTask.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                LatLng latLng = null;
                try {
                    latLng = new LatLng(location.getLatitude(), location.getLongitude());

                } catch (Exception e) {
                    latLng = new LatLng(50.436404, 30.369498);
                }

                List<Address> addresses = null;
                try {
                    addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Address address = addresses.get(0);
                String streetAddress = address.getAddressLine(0);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.im_cat_location_sample_35)).title(streetAddress));
            }
        });
    }




    @Override
    public void onMapLongClick(LatLng latLng) {
        Log.d(TAG, "onMapLongClick: " + latLng.toString());
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String streetAddress = address.getAddressLine(0);
             /*   mMap.addMarker(new MarkerOptions()
                        .position(latLng)
                        .title(streetAddress)
                        .draggable(true).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cat_location_sample_35))
                );*/
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onMarkerDragStart(Marker marker) {
        Log.d(TAG, "onMarkerDragStart: ");
    }

    @Override
    public void onMarkerDrag(Marker marker) {
        Log.d(TAG, "onMarkerDrag: ");
    }

    @Override
    public void onMarkerDragEnd(Marker marker) {
        Log.d(TAG, "onMarkerDragEnd: ");
        LatLng latLng = marker.getPosition();
        try {
            List<Address> addresses = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                String streetAddress = address.getAddressLine(0);
                marker.setTitle(streetAddress);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == ACCESS_LOCATION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                enableUserLocation();
                zoomToUserLocation();
            } else {
                Log.e(TAG, "Permission is not granted. Please, try again. ");
            }
        }
    }
}
