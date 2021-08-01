package com.example.favoritelist;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.fragment.app.FragmentActivity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    public static final int REQUEST_CODE = 1001;
    private GoogleMap mMap;
    private Clinic clinic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);

        clinic = (Clinic) getIntent().getSerializableExtra(DetailActivity.BOOK_STORE_OBJECT_MAPS);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
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
        mMap = googleMap;
        mMap.setMapType(googleMap.MAP_TYPE_NORMAL);

        UiSettings uiSettings = mMap.getUiSettings();
        uiSettings.setZoomControlsEnabled(true);

        if (enableMyLocation()) return;

        enableOnMapClick();

        // Add a marker in Sydney and move the camera
        LatLng location = new LatLng(clinic.getLatitude(), clinic.getLongitude());
        mMap.addMarker(new MarkerOptions().position(location).title(clinic.getClinicName() + " in "  + clinic.getClinicLocation() ));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(location));
    }

    private void enableOnMapClick() {
        mMap.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
            @Override
            public void onMapClick(LatLng latLng) {
                Geocoder geocoder = getGeocoder();
                List<Address> addressList = null;
                try {
                    addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 1);
                } catch (IOException e) {
                    e.printStackTrace();
                }
                Address address = getAddress(addressList);
                if (address != null) {
                    Toast.makeText(MapsActivity.this, address.getLocality() + ", " + address.getCountryCode()+ ": " + address.getCountryName(), Toast.LENGTH_SHORT).show();
                }


            }
        });
    }

    private Address getAddress(List<Address> addressList) {
        Address address = null;
        if (addressList != null) {
            address = addressList.get(0);
        }
        return address;
    }

    private Geocoder getGeocoder() {
        if (!Geocoder.isPresent()) {
            Toast.makeText(MapsActivity.this, "Sorry! Geocoder is not available!", Toast.LENGTH_SHORT).show();
        }
        return new Geocoder(MapsActivity.this);
    }

    private boolean enableMyLocation() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // TODO: Consider calling
            //    ActivityCompat#requestPermissions
            // here to request the missing permissions, and then overriding
            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
            //                                          int[] grantResults)
            // to handle the case where the user grants the permission. See the documentation
            // for ActivityCompat#requestPermissions for more details.
            ActivityCompat.requestPermissions(this, new String [] {Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, REQUEST_CODE);


            return true;
        }
        mMap.setMyLocationEnabled(true);
        return false;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == REQUEST_CODE) {
            if(grantResults.length >0 && grantResults[0] == PackageManager.PERMISSION_GRANTED)
                enableMyLocation();
            else
                Toast.makeText(this, "The necessary permissions have not been granted", Toast.LENGTH_SHORT).show();

        }
    }

    public void locateCity(View view) {
        EditText etCity = findViewById(R.id.etSearchFor);
        String city = etCity.getText().toString();

        Geocoder geocoder = getGeocoder();
        List<Address> addressList = null;
        try {
            addressList = geocoder.getFromLocationName(city, 1);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Address address = getAddress(addressList);
        LatLng location = null;
        if (address != null) {
            location = new LatLng(address.getLatitude(), address.getLongitude());
        }
        if (location != null) {
            mMap.addMarker(new MarkerOptions().position(location).title(city + " in " + address.getCountryCode() + ": " + address.getCountryName()));
        }
        CameraPosition cameraPosition = new CameraPosition.Builder()
                .target(location)
                .zoom(15.5f)
                .bearing(300)
                .tilt(50)
                .build();
        mMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
        //mMap.moveCamera(CameraUpdateFactory.newLatLng(location));


    }
}