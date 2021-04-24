package com.sunsetrebel;

import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;
import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.sunsetrebel.catsy.R;
import com.google.android.gms.maps.GoogleMap.OnMyLocationButtonClickListener;
import com.google.android.gms.maps.GoogleMap.OnMyLocationClickListener;
import android.location.Location;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import android.widget.Toast;


public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        googleMap.clear();
        mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        //mMap.isMyLocationEnabled();
        mMap.getUiSettings().setMyLocationButtonEnabled(true);
      /*  if (mMap != null) {
            if (ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED
                    && ActivityCompat.checkSelfPermission(this,
                    Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                // TODO: Consider calling
                //    ActivityCompat#requestPermissions
                // here to request the missing permissions, and then overriding
                //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                //                                          int[] grantResults)
                // to handle the case where the user grants the permission. See the documentation
                // for ActivityCompat#requestPermissions for more details.
                return;
            }
            mMap.setMyLocationEnabled(true);}*/
        //First Event
        LatLng FirstEvent = new LatLng(50.442914, 30.512982);
        mMap.addMarker(new MarkerOptions().position(FirstEvent).title("Open air cinema at 19:00"));

        LatLng SecondEvent = new LatLng(50.449, 30.512850);
        mMap.addMarker(new MarkerOptions().position(SecondEvent).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cat_dark_40)).title("Guitar night at 20:00"));

        LatLng ThirdEvent = new LatLng(50.440, 30.513900);
        mMap.addMarker(new MarkerOptions().position(ThirdEvent).icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cat_bright_40)).title("Ze! pussy destroying at 09:00"));
        LatLng mountainView = new LatLng(37.4, -122.1);

        final LatLng  FourthEvent = new LatLng(50.450, 30.513800);
        Marker FourthEventMarker = mMap.addMarker(
                new MarkerOptions()
                        .icon(BitmapDescriptorFactory.fromResource(R.drawable.ic_cat_dark_40))
                        .position(FourthEvent)
                        .title("Niggas gay party at 22:00").snippet("Here you will get some african fun"));


      //  LatLng cur_loc = new LatLng(50.442914,30.512982);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(FirstEvent, 15));

       /* LatLngBounds KievBounds = new LatLngBounds(
                new LatLng(50.43933295180478, 30.50080082260643), // SW bounds
                new LatLng(50.44665927119242, 30.522634903365686)  // NE bounds
        );
       mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(KievBounds.getCenter(), 15));*/
    }

}

//https://developers.google.com/maps/documentation/android-sdk/location?hl=ru
/*
class MyLocationLayerActivity extends AppCompatActivity
    implements GoogleMap.OnMyLocationButtonClickListener,
    GoogleMap.OnMyLocationClickListener,
    OnMapReadyCallback {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_location);

        SupportMapFragment mapFragment =
            (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    @SuppressLint("MissingPermission")
    @Override
    public void onMapReady(GoogleMap map) {
        // TODO: Before enabling the My Location layer, you must request
        // location permission from the user. This sample does not include
        // a request for location permission.
        map.setMyLocationEnabled(true);
        map.setOnMyLocationButtonClickListener(this);
        map.setOnMyLocationClickListener(this);
    }

    @Override
    public void onMyLocationClick(@NonNull Location location) {
        Toast.makeText(this, "Current location:\n" + location, Toast.LENGTH_LONG)
            .show();
    }

    @Override
    public boolean onMyLocationButtonClick() {
        Toast.makeText(this, "MyLocation button clicked", Toast.LENGTH_SHORT)
            .show();
        // Return false so that we don't consume the event and the default behavior still occurs
        // (the camera animates to the user's current position).
        return false;
    }
}*/

