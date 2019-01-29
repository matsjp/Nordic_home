package com.example.nordichome;


import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;


import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import no.nordicsemi.android.meshprovisioner.provisionerstates.UnprovisionedMeshNode;
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanRecord;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanResult;


public class MainActivity extends AppCompatActivity {

    private UUID filterUuid = UUID.fromString("00001827-0000-1000-8000-00805F9B34FB");
    private MeshRepo meshRepo;

    final ScanSettings settings = new ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            // Refresh the devices list every second
            .setReportDelay(0)
            // Hardware filtering has some issues on selected devices
            .setUseHardwareFilteringIfSupported(false)
            // Samsung S6 and S6 Edge report equal value of RSSI for all devices. In this app we ignore the RSSI.
            /*.setUseHardwareBatchingIfSupported(false)*/
            .build();
    private BluetoothLeScannerCompat scanner;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final Button button = (Button) findViewById(R.id.scanButton);
        meshRepo = new MeshRepo(this);

        final List<ScanFilter> filters = new ArrayList<>();
        filters.add(new ScanFilter.Builder().setServiceUuid(new ParcelUuid((filterUuid))).build());
        scanner = BluetoothLeScannerCompat.getScanner();
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                scanner.startScan(filters, settings, scanCallback);
            }
        });
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(final int callbackType, final ScanResult result){
            scanner.stopScan(this);
            Log.d("Scan", "Scan result");
            String address = result.getDevice().getAddress();
            Log.d("Address", address);
            final byte[] serviceData = getServiceData(result, UUID.fromString("00001827-0000-1000-8000-00805F9B34FB"));
            if (serviceData != null){
                Log.d("ServiceData", "ServiceData");
                final UUID uuid = meshRepo.getMeshManagerApi().getDeviceUuid(serviceData);
                if (!meshRepo.getBleMeshManager().isConnected()){
                    meshRepo.connect(result.getDevice());
                }
                //meshRepo.getMeshManagerApi().identifyNode(uuid, result.getDevice().getName());
                //meshRepo.getMeshManagerApi().startProvisioning(meshRepo.getUnprovisionedMeshNode());
            }
        }

        @Override
        public void onBatchScanResults(List<ScanResult> results) {
            Log.d("Scan", "Scan batch result");
        }

        @Override
        public void onScanFailed(int errorCode) {
            Log.d("Scan", "Scan failed");
        }
    };

    @Nullable
    public static byte[] getServiceData(@NonNull final ScanResult result, @NonNull final UUID serviceUuid) {
        final ScanRecord scanRecord = result.getScanRecord();
        if (scanRecord != null) {
            return scanRecord.getServiceData(new ParcelUuid((serviceUuid)));
        }
        return null;
    }
}
