package com.sunsetrebel;

import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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
import com.google.android.material.bottomsheet.BottomSheetDialog;
import com.sunsetrebel.catsy.R;

import android.location.Location;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.Toast;

import com.sunsetrebel.catsy.PopUpWindow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;

import java.io.IOException;
import java.util.List;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback, GoogleMap.OnMapLongClickListener, GoogleMap.OnMarkerDragListener {

    private static final String TAG = "MapsActivity";
    private GoogleMap mMap;
    private Geocoder geocoder;
    private int ACCESS_LOCATION_REQUEST_CODE = 10001;
    FusedLocationProviderClient fusedLocationProviderClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        mapFragment.getMapAsync(this);
        geocoder = new Geocoder(this);
        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        ImageButton mBottton = findViewById(R.id.add_button);

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

        mBottton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }

            private void showBottomSheetDialog() {

                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MapsActivity.this);
                bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_layout);

                LinearLayout copy = bottomSheetDialog.findViewById(R.id.copyLinearLayout);
                LinearLayout share = bottomSheetDialog.findViewById(R.id.shareLinearLayout);
                // LinearLayout upload = bottomSheetDialog.findViewById(R.id.uploadLinearLayout);
                LinearLayout download = bottomSheetDialog.findViewById(R.id.download);
                LinearLayout delete = bottomSheetDialog.findViewById(R.id.delete);

                bottomSheetDialog.show();
            }

        });
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
        mMap.getUiSettings().setMyLocationButtonEnabled(false);
        mMap.getUiSettings().setMapToolbarEnabled(false);
        mMap.setOnMapLongClickListener(this);
        mMap.setOnMarkerDragListener(this);
        mMap.getUiSettings().setCompassEnabled(false);

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

        // Add a marker at Taj Mahal and move the camera
//        LatLng latLng = new LatLng(27.1751, 78.0421);
//        MarkerOptions markerOptions = new MarkerOptions()
//                                            .position(latLng)
//                                            .title("Taj Mahal")
//                                            .snippet("Wonder of the world!");
//        mMap.addMarker(markerOptions);
//        CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(latLng, 16);
//        mMap.animateCamera(cameraUpdate);

        try {
            List<Address> addresses = geocoder.getFromLocationName("Current location", 1);
            if (addresses.size() > 0) {
                Address address = addresses.get(0);
                LatLng curr_location = new LatLng(address.getLatitude(), address.getLongitude());
                MarkerOptions markerOptions = new MarkerOptions()
                        .position(curr_location).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cat_location_sample_35))
                        .title(address.getLocality());
                mMap.addMarker(markerOptions);
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(curr_location, 12));

            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        //Event examples
        LatLng FirstEvent = new LatLng(50.436404, 30.369498);
        googleMap.addMarker(new MarkerOptions().position(FirstEvent).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cat_bright_40)).title("Open air cinema at 19:00"));

        LatLng SecondEvent = new LatLng(50.449, 30.512850);
        googleMap.addMarker(new MarkerOptions().position(SecondEvent).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cat_bright_40)).title("Guitar night at 20:00"));

        LatLng ThirdEvent = new LatLng(50.391566, 30.481428);
        googleMap.addMarker(new MarkerOptions().position(ThirdEvent).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cat_bright_40)).title("Mozzy birthday celebration at 09:00"));
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
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 11));
                mMap.addMarker(new MarkerOptions().position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cat_location_sample_35)).title("Current location"));
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
                //We can show a dialog that permission is not granted...
            }
        }
    }
}




/*
public class MapsActivity extends AppCompatActivity {
    SupportMapFragment supportMapFragment;
    FusedLocationProviderClient client;

    private static final String TAG = MapsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        // Get the SupportMapFragment and register for the callback
        // when the map is ready for use.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager()
                        .findFragmentById(R.id.google_map);
        //assign variable
        supportMapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.google_map);
        //Initialize fuze location
        client = LocationServices.getFusedLocationProviderClient(this);

        //Check permission
        if (ActivityCompat.checkSelfPermission(MapsActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED) {
            //When permission granted
            //Call method
            getCurrentLocation();
        }
        else {
            //When permission denied
            //Request permission
            ActivityCompat.requestPermissions(MapsActivity.this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},44);
        }
        ImageButton mBottton = findViewById(R.id.add_button);
        mBottton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showBottomSheetDialog();
            }

            private void showBottomSheetDialog() {

                final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(MapsActivity.this);
                bottomSheetDialog.setContentView(R.layout.bottom_sheet_dialog_layout);

                LinearLayout copy = bottomSheetDialog.findViewById(R.id.copyLinearLayout);
                LinearLayout share = bottomSheetDialog.findViewById(R.id.shareLinearLayout);
                // LinearLayout upload = bottomSheetDialog.findViewById(R.id.uploadLinearLayout);
                LinearLayout download = bottomSheetDialog.findViewById(R.id.download);
                LinearLayout delete = bottomSheetDialog.findViewById(R.id.delete);

                bottomSheetDialog.show();
            }
        });
    }

    private void getCurrentLocation(){
        //Initialize task location
        @SuppressLint("MissingPermission") Task<Location> task = client.getLastLocation();
        task.addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                if (location != null)
                {
                    //Sync map
                    supportMapFragment.getMapAsync(new OnMapReadyCallback() {

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
                            //Initialize lat lng
                            LatLng latLng = new LatLng(location.getLatitude(),location.getLongitude());
                            
                            //Create marker options
                            MarkerOptions options = new MarkerOptions().position(latLng).flat(false).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cat_location_sample_35)).title("Current location");
                            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,11));
                            googleMap.addMarker(options);
                            googleMap.getUiSettings().setMapToolbarEnabled(false);
                            googleMap.getUiSettings().setCompassEnabled(false);


                            //Event examples
                            LatLng FirstEvent = new LatLng(50.436404, 30.369498);
                            googleMap.addMarker(new MarkerOptions().position(FirstEvent).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cat_dark_40)).title("Open air cinema at 19:00"));

                            LatLng SecondEvent = new LatLng(50.449, 30.512850);
                            googleMap.addMarker(new MarkerOptions().position(SecondEvent).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cat_dark_40)).title("Guitar night at 20:00"));

                            LatLng ThirdEvent = new LatLng(50.391566, 30.481428);
                            googleMap.addMarker(new MarkerOptions().position(ThirdEvent).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cat_bright_40)).title("Mozzy birthday celebration at 09:00"));
                            LatLng mountainView = new LatLng(37.4, -122.1);
                        }
                    });
                }

            }
        });
    }

   // @Override
    public void onRequestPermissionResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults){
        if (requestCode == 44){
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                //When permission granted
                //Call method
                getCurrentLocation();
            }
        }
    }

}

*/
