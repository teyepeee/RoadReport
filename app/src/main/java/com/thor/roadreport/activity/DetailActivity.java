package com.thor.roadreport.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

import com.thor.roadreport.R;

import butterknife.Bind;
import butterknife.ButterKnife;

public class DetailActivity extends AppCompatActivity {

    @Bind(R.id.judul)
    TextView judul;
    @Bind(R.id.keterangan)
    TextView keterangan;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail);
        ButterKnife.bind(this);

        Bundle data = getIntent().getExtras();

        judul.setText(data.getString("judul"));
        keterangan.setText(data.getString("ket"));

    }
}
