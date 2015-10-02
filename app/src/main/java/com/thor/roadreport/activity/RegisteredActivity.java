package com.thor.roadreport.activity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.thor.roadreport.R;
import com.thor.roadreport.library.DatabaseHandler;

import java.util.HashMap;

public class RegisteredActivity extends Activity {

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registered);

        DatabaseHandler db = new DatabaseHandler(getApplicationContext());

        HashMap<String,String> user = new HashMap<String, String>();
        user = db.getUserDetails();

        /**
         * Displays the registration details in Text view
         **/

        final TextView name = (TextView)findViewById(R.id.name);
        final TextView email = (TextView)findViewById(R.id.email);
        final TextView created_at = (TextView)findViewById(R.id.regat);
        name.setText(user.get("name"));
        email.setText(user.get("email"));
        created_at.setText(user.get("created_at"));

        Button login = (Button) findViewById(R.id.login);
        login.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                Intent myIntent = new Intent(view.getContext(), LoginActivity.class);
                startActivityForResult(myIntent, 0);
                finish();
            }

        });

    }}
