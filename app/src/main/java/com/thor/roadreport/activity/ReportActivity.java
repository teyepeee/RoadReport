package com.thor.roadreport.activity;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.support.design.widget.TextInputLayout;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.thor.roadreport.R;
import com.thor.roadreport.app.AppController;
import com.thor.roadreport.util.Cons;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReportActivity extends AppCompatActivity {
    // LogCat tag
    private static final String TAG = ReportActivity.class.getSimpleName();

    @Bind(R.id.input_layout_judul)
    TextInputLayout inputLayoutJudul;
    @Bind(R.id.input_layout_laporan)
    TextInputLayout inputLayoutLaporan;
    @Bind(R.id.judul_laporan)
    EditText inputJudul;
    @Bind(R.id.isi_laporan)
    EditText inputIsi;
    @Bind(R.id.imgPreview)
    ImageView imgPreview;

    private String filePath = null;
    private String imgStr = "";
    //private ImageView imgPreview;

    private ProgressDialog pDialog;

    private GoogleMap mMap;

    LocationManager locManager;
    Location location;
    String provider;
    Criteria criteria = new Criteria(); // Criteria object to get provider

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        locManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        String gpsProvider = LocationManager.GPS_PROVIDER;

        //Prompts user to enable location services if it is not already enabled
        if (!locManager.isProviderEnabled(gpsProvider)) {

            //Alert Dialog
            AlertDialog alertDialog = new AlertDialog.Builder(this).create();
            alertDialog.setTitle("Pemberitahuan");
            alertDialog.setMessage("Lokasi GPS harus diaktifkan!");
            alertDialog.setCancelable(false);
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE,
                    "OK", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            String locConfig = Settings.ACTION_LOCATION_SOURCE_SETTINGS;
                            Intent enableGPS = new Intent(locConfig);
                            startActivity(enableGPS);
                        }
                    });
            alertDialog.show();

        }

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        // Progress dialog
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        //imgPreview = (ImageView) findViewById(R.id.imgPreview);

        // Receiving the data from previous activity
        Intent i = getIntent();

        // image or video path that is captured in previous activity
        filePath = i.getStringExtra("filePath");

        // boolean flag to identify the media type, image or video
        boolean isImage = i.getBooleanExtra("isImage", true);

        if (filePath != null) {
            // Displaying the image or video on the screen
            previewMedia(isImage);
        } else {
            Toast.makeText(getApplicationContext(),
                    "Sorry, file path is missing!", Toast.LENGTH_LONG).show();
        }

        inputJudul.addTextChangedListener(new MyTextWatcher(inputJudul));
        inputIsi.addTextChangedListener(new MyTextWatcher(inputIsi));

        setUpMapIfNeeded();

        mMap.setMyLocationEnabled(true);
        mMap.getUiSettings().setZoomControlsEnabled(true);

    }

    /**
     * Displaying captured image/video on the screen
     */
    private void previewMedia(boolean isImage) {
        // Checking whether captured media is image or video
        if (isImage) {

            setImgStr(convertGambar(filePath));

            imgPreview.setVisibility(View.VISIBLE);

            // bimatp factory
            BitmapFactory.Options options = new BitmapFactory.Options();

            // down sizing image as it throws OutOfMemory Exception for larger
            // images
            options.inSampleSize = 8;

            final Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

            imgPreview.setImageBitmap(bitmap);


        }
    }

    private Bitmap getImageBitmap(String filePath) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inSampleSize = 8;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);

        return bitmap;
    }


    private String convertGambar(String filePath) {
        Bitmap bitmap = getImageBitmap(filePath);
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 90, stream);

        byte [] byte_arr = stream.toByteArray();
        String imgStr = Base64.encodeToString(byte_arr, Base64.DEFAULT);

        return imgStr;
    }

    /**
     * Validating form
     */
    private void submitForm() {
        if (!validateJudul()) {
            return;
        }

        if (!validateIsi()) {
            return;
        }

        PostDataToServer();
    }

    private boolean validateJudul() {
        if (inputJudul.getText().toString().trim().isEmpty()) {
            inputLayoutJudul.setError(getString(R.string.err_msg_judul));
            requestFocus(inputJudul);
            return false;
        } else {
            inputLayoutJudul.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateIsi() {
        if (inputIsi.getText().toString().trim().isEmpty()) {
            inputLayoutLaporan.setError(getString(R.string.err_msg_isi));
            requestFocus(inputIsi);
            return false;
        } else {
            inputLayoutLaporan.setErrorEnabled(false);
        }

        return true;
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        private MyTextWatcher(View view) {
            this.view = view;
        }

        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
        }

        public void afterTextChanged(Editable editable) {
            switch (view.getId()) {
                case R.id.judul_laporan:
                    validateJudul();
                    break;
                case R.id.isi_laporan:
                    validateIsi();
                    break;
            }
        }
    }

    private void PostDataToServer() {

        final String imgSource = getImgStr();
        final String latitude = String.valueOf(location.getLatitude());
        final String longitude = String.valueOf(location.getLongitude());

        pDialog.setMessage("Mengirim laporan...");
        showDialog();

        StringRequest stringRequest = new StringRequest(Request.Method.POST, Cons.URL_REPORT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                hideDialog();
                Toast.makeText(getApplicationContext(), "Laporan berhasil dikirim.", Toast.LENGTH_LONG).show();

                Intent intent = new Intent(
                        ReportActivity.this,
                        MainActivity.class);
                startActivity(intent);
                finish();


            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                //Log.e(TAG, "Reporting Error: " + error.getMessage());
                error.printStackTrace();
                Toast.makeText(getApplicationContext(),
                        error.getMessage(), Toast.LENGTH_LONG).show();
                hideDialog();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();

                params.put(Cons.JUDUL, inputJudul.getText().toString());
                params.put(Cons.ISI_LAPORAN, inputIsi.getText().toString());
                params.put(Cons.GAMBAR, imgSource);
                params.put(Cons.LATITUDE, latitude);
                params.put(Cons.LONGITUDE, longitude);

                return params;
            }
        };

        stringRequest.setShouldCache(false);
        AppController.getInstance().addToRequestQueue(stringRequest, TAG);
    }

    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
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
        if(location != null){
            //Get current long and lat positions
            LatLng currentPos = new LatLng(location.getLatitude(), location.getLongitude());
            //Add a marker on the map with the current position
            mMap.addMarker(new MarkerOptions().position(currentPos).title("Current Position"));

            //Controls the camera so it would zoom into current position
            CameraUpdate cameraUpdate = CameraUpdateFactory.newLatLngZoom(currentPos, 15);
            mMap.animateCamera(cameraUpdate);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_send, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.btnSend:
                submitForm();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void setImgStr(String imgStr) {
        this.imgStr = imgStr;
    }

    public String getImgStr() {
        return imgStr;
    }
}