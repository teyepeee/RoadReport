package com.thor.roadreport.activity;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.squareup.picasso.Picasso;
import com.thor.roadreport.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    @Bind(R.id.judul)
    TextView judul;
    @Bind(R.id.isi_laporan)
    TextView isi_laporan;
    @Bind(R.id.gambar_detail)
    ImageView gambar_detail;

    private GoogleMap mMap;
    private LatLng lokasiTujuan;

    LocationManager locManager;
    Location location;
    String provider;
    Criteria criteria = new Criteria(); // Criteria object to get provider

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        locManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        Bundle data = getIntent().getExtras();

        judul.setText(data.getString("judul"));
        isi_laporan.setText(data.getString("isi_laporan"));
        Picasso.with(getApplicationContext()).load(data.getString("gambar")).into(gambar_detail);
        lokasiTujuan = new LatLng(data.getDouble("latitude"), data.getDouble("longitude"));

        setUpMapIfNeeded();

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

    }

    private void setUpMapIfNeeded() {
        // Do a null check to confirm that we have not already instantiated the map.
        if (mMap == null) {
            // Try to obtain the map from the SupportMapFragment.
            mMap = ((SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.maps))
                    .getMap();
            // Check if we were successful in obtaining the map.
            if (mMap != null) {
                setUpMap();
            }
        }
    }

    private void setUpMap() {
        //mMap.addMarker(new MarkerOptions().position(new LatLng(0, 0)).title("Marker"));
        provider = locManager.getBestProvider(criteria, true); // Name for best provider
        //Check for permissions if they are granted
        if((ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) &&
                (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED)){
            return;
        }
        location = locManager.getLastKnownLocation(provider); // Get last known location, basically current location
        //if(location != null){
            //Get current long and lat positions
            LatLng currentPos = new LatLng(lokasiTujuan.latitude, lokasiTujuan.longitude);
            //Add a marker on the map with the current position
            mMap.addMarker(new MarkerOptions().position(currentPos).title("Lokasi pelaporan"));

            //Controls the camera so it would zoom into current position
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentPos, 15);
            mMap.animateCamera(cameraUpdate);
        //}
    }

}
