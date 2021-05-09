package com.sunsetrebel;

import androidx.core.app.ActivityCompat;
import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
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
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import  com.sunsetrebel.catsy.PopUpWindow;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;



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


                         /*   googleMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
                                @Override
                                public boolean onMarkerClick(Marker marker) {
                                    openPopUpWindow();
                                    return true;
                                }

                                private void openPopUpWindow() {
                                    Intent popupwindow = new Intent(MapsActivity.this,PopUpWindow.class);
                                    startActivity(popupwindow);
                                }


                            });*/
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


