package com.example.nordichome;


import android.Manifest;
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
import android.support.v4.app.ActivityCompat;


import com.example.nordichome.adapter.ProvisionedNodesAdapter;

import java.util.ArrayList;


import no.nordicsemi.android.meshprovisioner.MeshManagerApi;
import no.nordicsemi.android.meshprovisioner.MeshManagerCallbacks;
import no.nordicsemi.android.meshprovisioner.MeshNetwork;
import no.nordicsemi.android.meshprovisioner.provisionerstates.UnprovisionedMeshNode;
import no.nordicsemi.android.meshprovisioner.transport.ProvisionedMeshNode;
import viewmodels.ProvisionedNodesViewmodes;

public class MainActivity extends AppCompatActivity {

    //private static final int REQUEST_ACCESS_COARSE_LOCATION = 1022; // random number
    private static final int REQUEST_ENABLE_BT = 1;
    public static final String TAG = MainActivity.class.getSimpleName();
    public MeshNetwork meshNetwork;
    ProvisionedNodesAdapter adapter;
    public ProvisionedNodesViewmodes view;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        setContentView(R.layout.activity_main);
        final FloatingActionButton addDevice = findViewById(R.id.add_device);


        final ProvisionedNodesViewmodes view = ViewModelProviders.of(this).get(ProvisionedNodesViewmodes.class);
        this.view = view;
        RecyclerView recyclerView = findViewById(R.id.recycler_view_devices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<String> data = new ArrayList<String>();
        adapter = new ProvisionedNodesAdapter(this, view);
        recyclerView.setAdapter(adapter);

        this.enableBluetooth();
        this.checkForLocation();

        addDevice.setOnClickListener(v -> {
            final Intent intent = new Intent(MainActivity.this, ScannerActivity.class);
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
        MeshManagerApi meshManagerApi = new MeshManagerApi(this);
        meshManagerApi.setMeshManagerCallbacks(this.meshManagerCallbacks);
        meshManagerApi.loadMeshNetwork();
    }

    private MeshManagerCallbacks meshManagerCallbacks = new MeshManagerCallbacks() {
        @Override
        public void onNetworkLoaded(MeshNetwork meshNetwork) {
            MainActivity.this.meshNetwork = meshNetwork;
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
}
