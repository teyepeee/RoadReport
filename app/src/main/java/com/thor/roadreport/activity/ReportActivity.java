package com.thor.roadreport.activity;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.AuthFailureError;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.thor.roadreport.R;
import com.thor.roadreport.app.AppController;
import com.thor.roadreport.util.Cons;

import java.util.HashMap;
import java.util.Map;

import butterknife.Bind;
import butterknife.ButterKnife;

public class ReportActivity extends AppCompatActivity {
    // LogCat tag
    private static final String TAG = ReportActivity.class.getSimpleName();

//    @Bind(R.id.input_layout_judul)
//    TextInputLayout inputLayoutJudul;
//    @Bind(R.id.input_layout_laporan)
//    TextInputLayout inputLayoutLaporan;
    @Bind(R.id.judul_laporan)
    EditText inputJudul;
    @Bind(R.id.isi_laporan)
    EditText inputIsi;
    @Bind(R.id.imgPreview)
    ImageView imgPreview;

    //private ProgressBar progressBar;
    private String filePath = null;
    //private TextView txtPercentage;
    //private ImageView imgPreview;
    //private Button btnUpload;
    //long totalSize = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_report);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        ButterKnife.bind(this);

        //txtPercentage = (TextView) findViewById(R.id.txtPercentage);
        //btnUpload = (Button) findViewById(R.id.btnUpload);
        //progressBar = (ProgressBar) findViewById(R.id.progressBar);
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

//        inputJudul.addTextChangedListener(new MyTextWatcher(inputJudul));
//        inputIsi.addTextChangedListener(new MyTextWatcher(inputIsi));

    }

    /**
     * Displaying captured image/video on the screen
     */
    private void previewMedia(boolean isImage) {
        // Checking whether captured media is image or video
        if (isImage) {
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

//    /**
//     * Validating form
//     */
//    private void submitForm() {
//        if (!validateJudul()) {
//            return;
//        }
//
//        if (!validateIsi()) {
//            return;
//        }
//
//        PostDataToServer();
//    }
//
//    private boolean validateJudul() {
//        if (inputJudul.getText().toString().trim().isEmpty()) {
//            inputLayoutJudul.setError(getString(R.string.err_msg_judul));
//            requestFocus(inputJudul);
//            return false;
//        } else {
//            inputLayoutJudul.setErrorEnabled(false);
//        }
//
//        return true;
//    }
//
//    private boolean validateIsi() {
//        if (inputIsi.getText().toString().trim().isEmpty()) {
//            inputLayoutLaporan.setError(getString(R.string.err_msg_isi));
//            requestFocus(inputIsi);
//            return false;
//        } else {
//            inputLayoutLaporan.setErrorEnabled(false);
//        }
//
//        return true;
//    }
//
//    private void requestFocus(View view) {
//        if (view.requestFocus()) {
//            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
//        }
//    }
//
//    private class MyTextWatcher implements TextWatcher {
//
//        private View view;
//
//        private MyTextWatcher(View view) {
//            this.view = view;
//        }
//
//        public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//        }
//
//        public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//        }
//
//        public void afterTextChanged(Editable editable) {
//            switch (view.getId()) {
//                case R.id.judul_laporan:
//                    validateJudul();
//                    break;
//                case R.id.isi_laporan:
//                    validateIsi();
//                    break;
//            }
//        }
//    }

    private void PostDataToServer() {
        StringRequest stringRequest = new StringRequest(Request.Method.POST, Cons.URL_REPORT, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                System.out.println(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {

            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();

                params.put(Cons.JUDUL, inputJudul.getText().toString());
                params.put(Cons.ISI_LAPORAN, inputIsi.getText().toString());
                //params.put(Cons.GAMBAR,"");

                return params;
            }
        };

        AppController.getInstance().addToRequestQueue(stringRequest, TAG);
    }

//    /**
//     * Uploading the file to server
//     */
//    private class UploadFileToServer extends AsyncTask<Void, Integer, String> {
//        @Override
//        protected void onPreExecute() {
//            // setting progress bar to zero
//            progressBar.setProgress(0);
//            super.onPreExecute();
//        }
//
//        @Override
//        protected void onProgressUpdate(Integer... progress) {
//            // Making progress bar visible
//            progressBar.setVisibility(View.VISIBLE);
//
//            // updating progress bar value
//            progressBar.setProgress(progress[0]);
//
//            // updating percentage value
//            txtPercentage.setText(String.valueOf(progress[0]) + "%");
//        }
//
//        @Override
//        protected String doInBackground(Void... params) {
//            return uploadFile();
//        }
//
//        @SuppressWarnings("deprecation")
//        private String uploadFile() {
//            String responseString = null;
//
//            HttpClient httpclient = new DefaultHttpClient();
//            HttpPost httppost = new HttpPost(Config.FILE_UPLOAD_URL);
//
//            try {
//                AndroidMultiPartEntity entity = new AndroidMultiPartEntity(
//                        new ProgressListener() {
//
//                            @Override
//                            public void transferred(long num) {
//                                publishProgress((int) ((num / (float) totalSize) * 100));
//                            }
//                        });
//
//                File sourceFile = new File(filePath);
//
//                // Adding file data to http body
//                entity.addPart("image", new FileBody(sourceFile));
//
//                // Extra parameters if you want to pass to server
//                entity.addPart("website",
//                        new StringBody("www.androidhive.info"));
//                entity.addPart("email", new StringBody("abc@gmail.com"));
//
//                totalSize = entity.getContentLength();
//                httppost.setEntity(entity);
//
//                // Making server call
//                HttpResponse response = httpclient.execute(httppost);
//                HttpEntity r_entity = response.getEntity();
//
//                int statusCode = response.getStatusLine().getStatusCode();
//                if (statusCode == 200) {
//                    // Server response
//                    responseString = EntityUtils.toString(r_entity);
//                } else {
//                    responseString = "Error occurred! Http Status Code: "
//                            + statusCode;
//                }
//
//            } catch (ClientProtocolException e) {
//                responseString = e.toString();
//            } catch (IOException e) {
//                responseString = e.toString();
//            }
//
//            return responseString;
//
//        }
//
//        @Override
//        protected void onPostExecute(String result) {
//            Log.e(TAG, "Response from server: " + result);
//
//            // showing the server response in an alert dialog
//            showAlert(result);
//
//            super.onPostExecute(result);
//        }
//
//    }
//
//    /**
//     * Method to show alert dialog
//     */
//    private void showAlert(String message) {
//        AlertDialog.Builder builder = new AlertDialog.Builder(this);
//        builder.setMessage(message).setTitle("Response from Servers")
//                .setCancelable(false)
//                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int id) {
//                        // do nothing
//                    }
//                });
//        AlertDialog alert = builder.create();
//        alert.show();
//    }

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
                PostDataToServer();

                //new UploadFileToServer().execute();
                //Toast.makeText(getApplicationContext(), "Kirim", Toast.LENGTH_LONG).show();
                return true;
        }

        return super.onOptionsItemSelected(item);
    }

}