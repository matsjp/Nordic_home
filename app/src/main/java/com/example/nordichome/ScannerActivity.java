package com.example.nordichome;

import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

import com.example.nordichome.adapter.DevicesAdapter;
import com.example.nordichome.adapter.DiscoveredBluetoothDevice;

import viewmodels.ScannerRepo;

public class ScannerActivity extends AppCompatActivity implements DevicesAdapter.OnItemClickListener {

    private ScannerRepo scannerRepo;
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
