package com.example.nordichome;


import android.bluetooth.BluetoothAdapter;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


import java.util.UUID;

import viewmodels.MeshRepo;


public class MainActivity extends AppCompatActivity {

    private UUID filterUuid = UUID.fromString("00001827-0000-1000-8000-00805F9B34FB");
    private MeshRepo meshRepo;

    private static final String TAG = "MainActivity";
    private static final int REQUEST_ENABLE_BT = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        final FloatingActionButton addDevice = findViewById(R.id.add_device);
        meshRepo = new MeshRepo(this);

        this.enableBluetooth();

        addDevice.setOnClickListener(v -> {
            final Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
            startActivity(intent);
            //scanner.startScan(filters, settings, scanCallback);
        });
    }

    //Function to enable bluetooth on the device
    private void enableBluetooth(){
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "deviceSupportBluetooth: The device doesn't support bluetooth");
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }
}
