package com.thor.roadreport.activity;

import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.error.VolleyError;
import com.android.volley.request.StringRequest;
import com.mikepenz.google_material_typeface_library.GoogleMaterial;
import com.mikepenz.materialdrawer.AccountHeader;
import com.mikepenz.materialdrawer.AccountHeaderBuilder;
import com.mikepenz.materialdrawer.Drawer;
import com.mikepenz.materialdrawer.DrawerBuilder;
import com.mikepenz.materialdrawer.model.PrimaryDrawerItem;
import com.mikepenz.materialdrawer.model.ProfileDrawerItem;
import com.mikepenz.materialdrawer.model.SecondaryDrawerItem;
import com.mikepenz.materialdrawer.model.SectionDrawerItem;
import com.mikepenz.materialdrawer.model.interfaces.IProfile;
import com.thor.roadreport.R;
import com.thor.roadreport.adapter.ListAdapter;
import com.thor.roadreport.app.AppController;
import com.thor.roadreport.helper.SQLiteHandler;
import com.thor.roadreport.helper.SessionManager;
import com.thor.roadreport.library.Config;
import com.thor.roadreport.model.ListItem;
import com.thor.roadreport.util.Cons;
import com.thor.roadreport.util.MyRecyclerScroll;
import com.thor.roadreport.util.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;


public class MainActivity extends AppCompatActivity implements ListAdapter.OnClickItemOnList {

    private SQLiteHandler db;
    private SessionManager session;

    //save our header or result
    private AccountHeader headerResult = null;
    private Drawer result = null;

    private IProfile profile;

    Toolbar toolbar;
    RecyclerView recyclerView;
    int fabMargin;
    LinearLayout toolbarContainer;
    int toolbarHeight;
    FrameLayout fab;
    ImageButton fabBtn;
    View fabShadow;
    boolean fadeToolbar = false;
    ListAdapter adapter;
    List<ListItem> items;

    // LogCat tag
    private static final String TAG = MainActivity.class.getSimpleName();

    // Camera activity request codes
    private static final int CAMERA_CAPTURE_IMAGE_REQUEST_CODE = 100;

    public static final int MEDIA_TYPE_IMAGE = 1;

    private Uri fileUri; // file url to store image/video

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Animation animation = AnimationUtils.loadAnimation(this, R.anim.simple_grow);

        setContentView(R.layout.activity_fab_hide);

        toolbar = (Toolbar) findViewById(R.id.toolbar);

        setSupportActionBar(toolbar);

        // SqLite database handler
        db = new SQLiteHandler(getApplicationContext());

        // session manager
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

        // Fetching user details from sqlite
        HashMap<String, String> user = db.getUserDetails();

        String name = user.get("name");
        String email = user.get("email");

        // Create a few sample profile
        profile = new ProfileDrawerItem().withName(name).withEmail(email).withIcon(getResources().getDrawable(R.drawable.profile));

        // Create the AccountHeader
        buildHeader(false, savedInstanceState);

        //Create the drawer
        result = new DrawerBuilder()
                .withActivity(this)
                .withToolbar(toolbar)
                .withAccountHeader(headerResult) //set the AccountHeader we created earlier for the header
                .addDrawerItems(
                        new PrimaryDrawerItem().withName(R.string.drawer_item_home).withIcon(GoogleMaterial.Icon.gmd_home),
                        new PrimaryDrawerItem().withName(R.string.drawer_item_profil).withIcon(GoogleMaterial.Icon.gmd_account_box),
                        new SectionDrawerItem().withName(R.string.drawer_item_report),
                        new SecondaryDrawerItem().withName(R.string.drawer_item_laporan).withIcon(GoogleMaterial.Icon.gmd_report)
                ) // add the items we want to use with our Drawer
                .withOnDrawerNavigationListener(new Drawer.OnDrawerNavigationListener() {
                    @Override
                    public boolean onNavigationClickListener(View clickedView) {
                        //this method is only called if the Arrow icon is shown. The hamburger is automatically managed by the MaterialDrawer
                        //if the back arrow is shown. close the activity
                        MainActivity.this.finish();
                        //return true if we have consumed the event
                        return true;
                    }
                })
                .withSavedInstance(savedInstanceState)
                .build();

        //  FAB margin needed for animation
        fabMargin = getResources().getDimensionPixelSize(R.dimen.fab_margin);
        toolbarHeight = Utils.getToolbarHeight(this);

        toolbarContainer = (LinearLayout) findViewById(R.id.fabhide_toolbar_container);
        recyclerView = (RecyclerView) findViewById(R.id.recyclerview);

        /* Set top padding= toolbar height.
         So there is no overlap when the toolbar hides.
         Avoid using 0 for the other parameters as it resets padding set via XML!*/
        recyclerView.setPadding(recyclerView.getPaddingLeft(), toolbarHeight,
                recyclerView.getPaddingRight(), recyclerView.getPaddingBottom());

        recyclerView.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(linearLayoutManager);

        items = new ArrayList<>();
        items.add(new ListItem("1", "satu"));
        items.add(new ListItem("2", "dua"));
        items.add(new ListItem("3", "tiga"));
        items.add(new ListItem("4", "empat"));
        items.add(new ListItem("5", "lima"));
        items.add(new ListItem("6", "enam"));
        items.add(new ListItem("7", "tujuh"));

        adapter = new ListAdapter(items, this);
        adapter.setOnClickItemOnList(this);
        recyclerView.setAdapter(adapter);

//        ambilDataDariAPI();

        recyclerView.addOnScrollListener(new MyRecyclerScroll() {
            @Override
            public void show() {
                toolbarContainer.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
                if (fadeToolbar)
                    toolbarContainer.animate().alpha(1).setInterpolator(new DecelerateInterpolator(1)).start();
                fab.animate().translationY(0).setInterpolator(new DecelerateInterpolator(2)).start();
            }

            @Override
            public void hide() {
                toolbarContainer.animate().translationY(-toolbarHeight).setInterpolator(new AccelerateInterpolator(2)).start();
                if (fadeToolbar)
                    toolbarContainer.animate().alpha(0).setInterpolator(new AccelerateInterpolator(1)).start();
                fab.animate().translationY(fab.getHeight() + fabMargin).setInterpolator(new AccelerateInterpolator(2)).start();
            }
        });

        fab = (FrameLayout) findViewById(R.id.myfab_main);
        fabBtn = (ImageButton) findViewById(R.id.myfab_main_btn);
        fabShadow = findViewById(R.id.myfab_shadow);

//        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//            fabShadow.setVisibility(View.GONE);
//            fabBtn.setBackground(getDrawable(R.drawable.ripple_accent));
//        }

        fab.startAnimation(animation);

        fabBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                captureImage();
            }
        });

        // Checking camera availability
        if (!isDeviceSupportCamera()) {
            Toast.makeText(getApplicationContext(),
                    "Sorry! Your device doesn't support camera",
                    Toast.LENGTH_LONG).show();
            // will close the app if the device does't have camera
            finish();
        }
    }

    /**
     * Logging out the user. Will set isLoggedIn flag to false in shared
     * preferences Clears the user data from sqlite users table
     * */
    private void logoutUser() {
        session.setLogin(false);

        db.deleteUsers();

        // Launching the login activity
        Intent intent = new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    /**
     * small helper method to reuse the logic to build the AccountHeader
     * this will be used to replace the header of the drawer with a compact/normal header
     *
     * @param compact
     * @param savedInstanceState
     */
    private void buildHeader(boolean compact, Bundle savedInstanceState) {
        // Create the AccountHeader
        headerResult = new AccountHeaderBuilder()
                .withActivity(this)
                .withHeaderBackground(R.drawable.header)
                .addProfiles(profile)
                .withSelectionListEnabledForSingleProfile(false)
                .withSavedInstance(savedInstanceState)
                .build();
    }

    /**
     * Checking device has camera hardware or not
     * */
    private boolean isDeviceSupportCamera() {
        if (getApplicationContext().getPackageManager().hasSystemFeature(
                PackageManager.FEATURE_CAMERA)) {
            // this device has a camera
            return true;
        } else {
            // no camera on this device
            return false;
        }
    }

    /**
     * Launching camera app to capture image
     */
    private void captureImage() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        fileUri = getOutputMediaFileUri(MEDIA_TYPE_IMAGE);
        intent.putExtra(MediaStore.EXTRA_OUTPUT, fileUri);
        // start the image capture Intent
        startActivityForResult(intent, CAMERA_CAPTURE_IMAGE_REQUEST_CODE);
    }

    /**
     * Here we store the file url as it will be null after returning from camera
     * app
     */
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        //add the values which need to be saved from the drawer to the bundle
        outState = result.saveInstanceState(outState);
        //add the values which need to be saved from the accountHeader to the bundle
        outState = headerResult.saveInstanceState(outState);

        // save file url in bundle as it will be null on screen orientation
        // changes
        outState.putParcelable("file_uri", fileUri);
        super.onSaveInstanceState(outState);
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);

        // get the file url
        fileUri = savedInstanceState.getParcelable("file_uri");
    }

    /**
     * Receiving activity result method will be called after closing the camera
     * */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // if the result is capturing Image
        if (requestCode == CAMERA_CAPTURE_IMAGE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {

                // successfully captured the image
                // launching upload activity
                launchUploadActivity(true);


            } else if (resultCode == RESULT_CANCELED) {

                // user cancelled Image capture
                Toast.makeText(getApplicationContext(),
                        "User cancelled image capture", Toast.LENGTH_SHORT)
                        .show();

            } else {
                // failed to capture image
                Toast.makeText(getApplicationContext(),
                        "Sorry! Failed to capture image", Toast.LENGTH_SHORT)
                        .show();
            }

        }
    }

    private void launchUploadActivity(boolean isImage){
        Intent i = new Intent(MainActivity.this, ReportActivity.class);
        i.putExtra("filePath", fileUri.getPath());
        i.putExtra("isImage", isImage);
        startActivity(i);
    }

    /**
     * ------------ Helper Methods ----------------------
     * */

    /**
     * Creating file uri to store image/video
     */
    public Uri getOutputMediaFileUri(int type) {
        return Uri.fromFile(getOutputMediaFile(type));
    }

    /**
     * returning image / video
     */
    private static File getOutputMediaFile(int type) {

        // External sdcard location
        File mediaStorageDir = new File(
                Environment
                        .getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES),
                Config.IMAGE_DIRECTORY_NAME);

        // Create the storage directory if it does not exist
        if (!mediaStorageDir.exists()) {
            if (!mediaStorageDir.mkdirs()) {
                Log.d(TAG, "Oops! Failed create "
                        + Config.IMAGE_DIRECTORY_NAME + " directory");
                return null;
            }
        }

        // Create a media file name
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss",
                Locale.getDefault()).format(new Date());
        File mediaFile;
        if (type == MEDIA_TYPE_IMAGE) {
            mediaFile = new File(mediaStorageDir.getPath() + File.separator
                    + "IMG_" + timeStamp + ".jpg");
        } else {
            return null;
        }

        return mediaFile;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.pengaturan:
                Toast.makeText(getApplicationContext(),
                    "Menu Pengaturan", Toast.LENGTH_SHORT)
                    .show();
                return true;
            case R.id.logout:
                logoutUser();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }

    }

    @Override
    public void onBackPressed() {
        //handle the back press :D close the drawer first and if the drawer is closed close the activity
        if (result != null && result.isDrawerOpen()) {
            result.closeDrawer();
        } else {
            super.onBackPressed();
        }
    }

    public void ambilDataDariAPI() {
        StringRequest stringRequest = new StringRequest(Request.Method.GET, Cons.URL_ITEM, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null) {
                    try {
                        JSONObject obj = new JSONObject(response);
                        JSONArray data = obj.getJSONArray("items");

                        for (int i=0; i<data.length(); i++) {
                            ListItem item = new ListItem();

                            item.setJudul(data.getJSONObject(i).getString("judul"));
                            item.setKeterangan(data.getJSONObject(i).getString("keterangan"));
                            item.setImage(data.getJSONObject(i).getString("imageUrl"));

                            items.add(item);
                        }

                        adapter.notifyDataSetChanged();

                    } catch (JSONException e) {
                        e.printStackTrace();
                    }


                }

            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        });

        AppController.getInstance().addToRequestQueue(stringRequest, TAG);
    }


    @Override
    public void onClick(int position) {
        ListItem itemnya = items.get(position);
        String judul = itemnya.getJudul();
        String keterangan = itemnya.getKeterangan();

        Intent intent = new Intent(MainActivity.this, DetailActivity.class);
        intent.putExtra("judul", judul);
        intent.putExtra("ket", keterangan);
        startActivity(intent);

    }
}
