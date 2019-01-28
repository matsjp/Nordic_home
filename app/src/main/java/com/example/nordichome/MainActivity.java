package com.example.nordichome;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;


import java.util.List;

import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanResult;

public class MainActivity extends AppCompatActivity {

    final ScanSettings settings = new ScanSettings.Builder()
            .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
            // Refresh the devices list every second
            .setReportDelay(0)
            // Hardware filtering has some issues on selected devices
            .setUseHardwareFilteringIfSupported(false)
            // Samsung S6 and S6 Edge report equal value of RSSI for all devices. In this app we ignore the RSSI.
            /*.setUseHardwareBatchingIfSupported(false)*/
            .build();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        final BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();
        scanner.startScan(scanCallback);

    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(final int callbackType, final ScanResult result){
            Log.d("Scan", "Scan result");
            String address = result.getDevice().getAddress();
            Log.d("Address", address);


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
}
