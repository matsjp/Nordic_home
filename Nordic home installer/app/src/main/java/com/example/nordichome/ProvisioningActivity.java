package com.example.nordichome;


import android.Manifest;
import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.PackageManager;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.TextView;


import com.example.nordichome.adapter.ProvisionedNodesAdapter;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.Scope;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;
import com.google.api.client.json.gson.GsonFactory;
import com.google.api.services.drive.Drive;
import com.google.api.services.drive.DriveScopes;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;


import no.nordicsemi.android.meshprovisioner.MeshManagerApi;
import no.nordicsemi.android.meshprovisioner.MeshManagerCallbacks;
import no.nordicsemi.android.meshprovisioner.MeshNetwork;
import no.nordicsemi.android.meshprovisioner.provisionerstates.UnprovisionedMeshNode;
import no.nordicsemi.android.meshprovisioner.transport.GenericOnOffSet;
import no.nordicsemi.android.meshprovisioner.transport.ProvisionedMeshNode;
import viewmodels.DriveServiceRepo;
import viewmodels.ProvisionedNodesViewmodes;
import viewmodels.ScannerRepo;

public class ProvisioningActivity extends AppCompatActivity implements ProvisionedNodesAdapter.OnItemClickListener{

    //private static final int REQUEST_ACCESS_COARSE_LOCATION = 1022; // random number
    private static final int REQUEST_ENABLE_BT = 1;
    public static final String TAG = ProvisioningActivity.class.getSimpleName();
    public MeshNetwork meshNetwork;
    ProvisionedNodesAdapter adapter;
    public ProvisionedNodesViewmodes view;
    private ScannerRepo scannerRepo;
    private boolean lightState = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_provisioning);
        ApplicationExtension application = (ApplicationExtension) getApplication();
        scannerRepo = application.getScannerRepo();

        TextView noProvisionedNodesText = findViewById(R.id.no_provisioned_nodes);
        noProvisionedNodesText.setVisibility(View.VISIBLE);

        final FloatingActionButton addDevice = findViewById(R.id.add_device);


        final ProvisionedNodesViewmodes view = ViewModelProviders.of(this).get(ProvisionedNodesViewmodes.class);
        this.view = view;
        RecyclerView recyclerView = findViewById(R.id.recycler_view_devices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<String> data = new ArrayList<String>();
        adapter = new ProvisionedNodesAdapter(this, view);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

        if (adapter.getItemCount() > 0){
            noProvisionedNodesText.setVisibility(View.GONE);
        }

        this.enableBluetooth();
        this.checkForLocation();

        addDevice.setOnClickListener(v -> {
            final Intent intent = new Intent(ProvisioningActivity.this, ScannerActivity.class);
            startActivity(intent);
        });
    }

    //Location access
    private void checkForLocation() {
        checkLocationPermission();
        requestPermissions(new String[]{Manifest.permission.ACCESS_COARSE_LOCATION}, 1);
    }

    public boolean checkLocationPermission()
    {
        String permission = "android.permission.ACCESS_COARSE_LOCATION";
        int res = this.checkCallingOrSelfPermission(permission);
        return (res == PackageManager.PERMISSION_GRANTED);
    }

    public void onRequestPermissionsResult(int requestCode, String permissions[], int[] grantResults) {
        switch (requestCode) {
            case 1: {
                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0
                        && grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }

    //Function to enable bluetooth on the device
    private void enableBluetooth() {
        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        if (mBluetoothAdapter == null) {
            Log.d(TAG, "deviceSupportBluetooth: The device doesn't support bluetooth");
        }

        if (!mBluetoothAdapter.isEnabled()) {
            Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
            startActivityForResult(enableBtIntent, REQUEST_ENABLE_BT);
        }
    }

    @Override
    public void onResume(){
        super.onResume();
        Log.d(TAG, "Connected: " + Boolean.toString(this.scannerRepo.getBleMeshManager().isConnected()));
        MeshManagerApi meshManagerApi = new MeshManagerApi(this);
        meshManagerApi.setMeshManagerCallbacks(this.meshManagerCallbacks);
        meshManagerApi.loadMeshNetwork();
    }

    private MeshManagerCallbacks meshManagerCallbacks = new MeshManagerCallbacks() {
        @Override
        public void onNetworkLoaded(MeshNetwork meshNetwork) {
            ProvisioningActivity.this.meshNetwork = meshNetwork;
            ArrayList<ProvisionedMeshNode> nodes = new ArrayList<ProvisionedMeshNode>(meshNetwork.getProvisionedNodes());
            view.setNodesArrayList(nodes);
            /*for (ProvisionedMeshNode node : nodes){
                view.addNode(node);
            }*/
        }

        @Override
        public void onNetworkUpdated(MeshNetwork meshNetwork) {

        }

        @Override
        public void onNetworkLoadFailed(String error) {

        }

        @Override
        public void onNetworkImported(MeshNetwork meshNetwork) {

        }

        @Override
        public void onNetworkImportFailed(String error) {

        }

        @Override
        public void onNetworkExported(MeshNetwork meshNetwork) {

        }

        @Override
        public void onNetworkExportedJson(MeshNetwork meshNetwork, String networkJson) {

        }

        @Override
        public void onNetworkExportFailed(String error) {

        }

        @Override
        public void sendProvisioningPdu(UnprovisionedMeshNode meshNode, byte[] pdu) {

        }

        @Override
        public void sendMeshPdu(byte[] pdu) {

        }

        @Override
        public int getMtu() {
            return 0;
        }
    };

    @Override
    public void onItemClick(ProvisionedMeshNode node) {
        Log.d(TAG, "onItemClicked");
        byte[] appKey = scannerRepo.getMeshManagerApi().getMeshNetwork().getAppKey(0).getKey();
        GenericOnOffSet genericOnOffSet = new GenericOnOffSet(appKey, lightState, 500);
        lightState = !lightState;
        BleMeshManager meshManager = scannerRepo.getBleMeshManager();
        Log.d(TAG, Boolean.toString(meshManager.isConnected()));
        scannerRepo.getMeshManagerApi().sendMeshMessage(node.getUnicastAddress(), genericOnOffSet);
    }
}
