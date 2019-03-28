package com.example.nordichome;

import android.arch.lifecycle.Observer;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;

import com.example.nordichome.adapter.DevicesAdapter;
import com.example.nordichome.adapter.DiscoveredBluetoothDevice;

import java.util.ArrayList;
import java.util.Scanner;

import no.nordicsemi.android.meshprovisioner.Group;
import no.nordicsemi.android.meshprovisioner.UnprovisionedBeacon;
import no.nordicsemi.android.meshprovisioner.transport.MeshMessage;
import viewmodels.ScannerRepo;

public class ScannerActivity extends AppCompatActivity implements DevicesAdapter.OnItemClickListener, DevicesAdapter.ConnectButtonClickListener,
        DevicesAdapter.IdentifyButtonClickListener, DevicesAdapter.ProvisionButtonClickListener {

    private ScannerRepo scannerRepo;
    private String TAG = ScannerActivity.class.getSimpleName();
    private Group group;
    private Snackbar connectedSnackbar;
    private Snackbar identifyReadySnackbar;
    private Snackbar identifiedSnackbar;
    private Snackbar provisionedSnackbar;
    private Snackbar disconnectedSnackbar;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scanner);

        View v = findViewById(R.id.myCoordinatorLayout);

        identifyReadySnackbar = Snackbar.make(v, R.string.identify_ready, Snackbar.LENGTH_SHORT);
        identifiedSnackbar = Snackbar.make(v, R.string.provisioning_ready, Snackbar.LENGTH_SHORT);
        provisionedSnackbar = Snackbar.make(v, R.string.provisioning_complete, Snackbar.LENGTH_SHORT);

        ApplicationExtension application = (ApplicationExtension) getApplication();
        scannerRepo = application.getScannerRepo();
        Intent intent = getIntent();
        group = intent.getParcelableExtra("group");
        scannerRepo.setSelectedGroup(group);

        scannerRepo.getIdentifyReady().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean identifyReady) {
                if (identifyReady){
                    identifyReadySnackbar.show();
                }
            }
        });

        scannerRepo.getProvisioningReady().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean provisioningReady) {
                if (provisioningReady){
                    identifiedSnackbar.show();
                }
            }
        });

        scannerRepo.getProvisioningComplete().observe(this, new Observer<Boolean>() {
            @Override
            public void onChanged(@Nullable Boolean provisioningComplete) {
                if (provisioningComplete){
                    provisionedSnackbar.show();
                    scannerRepo.getProvisioningComplete().postValue(false);
                    Intent intent = new Intent(ScannerActivity.this, GroupConfigActivity.class);
                    intent.putExtra("group", group);
                    scannerRepo.getUnprovisionedDevicesLiveData().clear();
                    ScannerActivity.this.startActivity(intent);
                }
            }
        });

        final RecyclerView recyclerView = findViewById(R.id.recycler_view_devices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        //Dividing items in RecyclerView
        final DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(this, DividerItemDecoration.VERTICAL);
        recyclerView.addItemDecoration(dividerItemDecoration);

        final DevicesAdapter adapter = new DevicesAdapter(this, scannerRepo.getUnprovisionedDevicesLiveData());
        adapter.setOnItemClickListener(this);
        adapter.setConnectButtonClickListener(this);
        adapter.setIdentifyButtonClickListener(this);
        adapter.setProvisionButtonClickListener(this);
        recyclerView.setAdapter(adapter);


    }

    @Override
    protected void onStart() {
        super.onStart();
        scannerRepo.startScan(BleMeshManager.MESH_PROVISIONING_UUID);
    }

    @Override
    protected void onPause(){
        super.onPause();
        scannerRepo.stopScan();
    }

    @Override
    protected void onResume(){
        super.onResume();
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

    @Override
    public void onConnectButtonClick(@NonNull DiscoveredBluetoothDevice device){
        Log.d(TAG, "Connect provisionButton clicked");
        if(scannerRepo.getBleMeshManager().isConnected()){
            scannerRepo.getBleMeshManager().disconnect();
        }
        scannerRepo.connect(device);
    }

    @Override
    public void onIdentifyButtonClick(@NonNull DiscoveredBluetoothDevice device){
        Log.d(TAG, "Identify Button Clicked");
        scannerRepo.identifyNode((UnprovisionedBeacon) device.getBeacon());
    }

    @Override
    public void onProvisionButtonClickListener() {
        Log.d(TAG, "Provision provisionButton clicked");
        scannerRepo.provisionCurrentUnprovisionedMesNode();
    }
}
