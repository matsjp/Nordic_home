package com.example.nordichome;


import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;


import java.util.UUID;

import viewmodels.MeshRepo;


public class MainActivity extends AppCompatActivity {

    private UUID filterUuid = UUID.fromString("00001827-0000-1000-8000-00805F9B34FB");


    private MeshRepo meshRepo;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        final FloatingActionButton addDevice = findViewById(R.id.add_device);
        meshRepo = new MeshRepo(this);

        addDevice.setOnClickListener(v -> {
            final Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
            startActivity(intent);
            //scanner.startScan(filters, settings, scanCallback);
        });


    }
}
