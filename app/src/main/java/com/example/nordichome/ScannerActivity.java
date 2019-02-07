package com.example.nordichome;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Button;

import com.example.nordichome.adapter.DevicesAdapter;
import com.example.nordichome.adapter.DiscoveredBluetoothDevice;

import viewmodels.ScannerRepo;

public class ScannerActivity extends AppCompatActivity implements DevicesAdapter.OnItemClickListener {

    private ScannerRepo scannerRepo;

    private CoordinatorLayout coordinatorLayout;
    private Button button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);
        scannerRepo = new ScannerRepo(this);

        final RecyclerView recyclerView = findViewById(R.id.recycler_view_devices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        final DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);
        final DevicesAdapter adapter = new DevicesAdapter(this, scannerRepo.getUnprovisionedDevicesLiveData());
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

        coordinatorLayout = findViewById(R.id.myCoordinatorLayout);
        button = findViewById(R.id.button);

        button.setOnClickListener(v -> showSnackbar());
    }

    //Popup for connection to a device. Does not work correctly cant show connected message and then change page.
    public void showSnackbar() {
        Snackbar snackbar = Snackbar.make(coordinatorLayout, "Connected", Snackbar.LENGTH_SHORT);
        snackbar.show();
        //Starts new activity, LightActivity.
        //final Intent intent = new Intent(ScannerActivity.this, LightActivity.class);
        //startActivity(intent);
    }

    @Override
    protected void onStart() {
        super.onStart();
        scannerRepo.startScan(BleMeshManager.MESH_PROVISIONING_UUID);
    }


    @Override
    protected void onStop() {
        super.onStop();
        scannerRepo.stopScan();
    }


    @Override
    public void onItemClick(@NonNull DiscoveredBluetoothDevice device) {
        scannerRepo.connect(device);
    }
}
