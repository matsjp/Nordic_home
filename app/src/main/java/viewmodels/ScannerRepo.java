package viewmodels;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.nordichome.BleMeshManager;
import com.example.nordichome.BleMeshManagerCallbacks;
import com.example.nordichome.adapter.DiscoveredBluetoothDevice;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import no.nordicsemi.android.meshprovisioner.MeshBeacon;
import no.nordicsemi.android.meshprovisioner.MeshManagerApi;
import no.nordicsemi.android.meshprovisioner.MeshManagerCallbacks;
import no.nordicsemi.android.meshprovisioner.MeshNetwork;
import no.nordicsemi.android.meshprovisioner.MeshProvisioningStatusCallbacks;
import no.nordicsemi.android.meshprovisioner.MeshStatusCallbacks;
import no.nordicsemi.android.meshprovisioner.UnprovisionedBeacon;
import no.nordicsemi.android.meshprovisioner.provisionerstates.ProvisioningState;
import no.nordicsemi.android.meshprovisioner.provisionerstates.UnprovisionedMeshNode;
import no.nordicsemi.android.meshprovisioner.transport.MeshMessage;
import no.nordicsemi.android.meshprovisioner.transport.ProvisionedMeshNode;
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanRecord;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;
import viewmodels.DevicesLiveData;

import static no.nordicsemi.android.meshprovisioner.provisionerstates.ProvisioningState.States.PROVISIONING_CAPABILITIES;

public class ScannerRepo implements BleMeshManagerCallbacks, MeshManagerCallbacks, MeshStatusCallbacks, MeshProvisioningStatusCallbacks {

    private MeshManagerApi mMeshManagerApi;
    private BleMeshManager mBleMeshManager;
    private boolean isScanning = false;
    private DevicesLiveData mUnprovisionedDevicesLiveData = new DevicesLiveData(true, false);
    private DiscoveredBluetoothDevice discoveredBluetoothDevice;
    public final static String TAG = "ScannerRepo";

    public ScannerRepo(Context context){
        mBleMeshManager = new BleMeshManager(context);
        mBleMeshManager.setGattCallbacks(this);
        mMeshManagerApi = new MeshManagerApi(context);
        mMeshManagerApi.setMeshManagerCallbacks(this);
        mMeshManagerApi.setProvisioningStatusCallbacks(this);
        mMeshManagerApi.setMeshStatusCallbacks(this);
        mMeshManagerApi.loadMeshNetwork();
    }

    public DevicesLiveData getUnprovisionedDevicesLiveData() {
        return mUnprovisionedDevicesLiveData;
    }

    public void startScan(final UUID filterUuid){
        if(isScanning)
            return;

        final List<ScanFilter> filters = new ArrayList<>();
        filters.add(new ScanFilter.Builder().setServiceUuid(new ParcelUuid((filterUuid))).build());
        BluetoothLeScannerCompat scanner = BluetoothLeScannerCompat.getScanner();

        final ScanSettings settings = new ScanSettings.Builder()
                .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                // Refresh the devices list every second
                .setReportDelay(0)
                // Hardware filtering has some issues on selected devices
                .setUseHardwareFilteringIfSupported(false)
                // Samsung S6 and S6 Edge report equal value of RSSI for all devices. In this app we ignore the RSSI.
                /*.setUseHardwareBatchingIfSupported(false)*/
                .build();
        scanner.startScan(filters, settings, scanCallback);
        isScanning = true;
    }

    public void stopScan(){
        BluetoothLeScannerCompat.getScanner().stopScan(scanCallback);
        isScanning = false;
    }

    @Nullable
    public static byte[] getServiceData(@NonNull final ScanResult result, @NonNull final UUID serviceUuid) {
        final ScanRecord scanRecord = result.getScanRecord();
        if (scanRecord != null) {
            return scanRecord.getServiceData(new ParcelUuid((serviceUuid)));
        }
        return null;
    }

    private final ScanCallback scanCallback = new ScanCallback() {
        @Override
        public void onScanResult(final int callbackType, final ScanResult result){
            String address = result.getDevice().getAddress();
            if (mUnprovisionedDevicesLiveData.deviceDiscovered(result, mMeshManagerApi.getMeshBeacon(getServiceData(result, BleMeshManager.MESH_PROVISIONING_UUID)))) {
                mUnprovisionedDevicesLiveData.applyFilter();
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


    public void connect(final DiscoveredBluetoothDevice device) {
        discoveredBluetoothDevice = device;
        mBleMeshManager.connect(device.getDevice());
    }

    @Override
    public void onDataReceived(BluetoothDevice bluetoothDevice, int mtu, byte[] pdu) {
        Log.d(TAG, "onDataRecieved");
        mMeshManagerApi.handleNotifications(mtu, pdu);
    }

    @Override
    public void onDataSent(BluetoothDevice device, int mtu, byte[] pdu) {
        Log.d(TAG, "onDataSent");
        mMeshManagerApi.handleWriteCallbacks(mtu, pdu);

    }

    @Override
    public void onDeviceConnecting(BluetoothDevice device) {

    }

    @Override
    public void onDeviceConnected(BluetoothDevice device) {
        Log.d(TAG, "onDeviceConnected");
    }

    @Override
    public void onDeviceDisconnecting(BluetoothDevice device) {

    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice device) {

    }

    @Override
    public void onLinklossOccur(BluetoothDevice device) {

    }

    @Override
    public void onServicesDiscovered(BluetoothDevice device, boolean optionalServicesFound) {

    }

    @Override
    public void onDeviceReady(BluetoothDevice device) {
        Log.d(TAG, "onDeviceReady");
        try {
            Log.d(TAG, mBleMeshManager.getmMeshProvisioningDataInCharacteristic().toString());
        }
        catch (NullPointerException e){
            Log.d(TAG, "Null pointer error");
        }
        final UnprovisionedBeacon beacon = (UnprovisionedBeacon) discoveredBluetoothDevice.getBeacon();
        mMeshManagerApi.identifyNode(beacon.getUuid(), "Living Room");
    }

    @Override
    public boolean shouldEnableBatteryLevelNotifications(BluetoothDevice device) {
        return false;
    }

    @Override
    public void onBatteryValueReceived(BluetoothDevice device, int value) {

    }

    @Override
    public void onBondingRequired(BluetoothDevice device) {

    }

    @Override
    public void onBonded(BluetoothDevice device) {

    }

    @Override
    public void onError(BluetoothDevice device, String message, int errorCode) {

    }

    @Override
    public void onDeviceNotSupported(BluetoothDevice device) {

    }

    @Override
    public void onNetworkLoaded(MeshNetwork meshNetwork) {

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
        Log.d("MeshRepo", "sendProvisioningPdu");
        mBleMeshManager.sendPdu(pdu);
    }

    @Override
    public void sendMeshPdu(byte[] pdu) {

    }

    @Override
    public int getMtu() {
        return mBleMeshManager.getMtuSize();
    }

    @Override
    public void onProvisioningStateChanged(UnprovisionedMeshNode meshNode, ProvisioningState.States state, byte[] data) {
        Log.d(TAG, "onProvisionaingStateChanged " + state.toString());
        if (state == PROVISIONING_CAPABILITIES){
            Log.d(TAG, "Starting provisioning");
            mMeshManagerApi.startProvisioning(meshNode);
        }
    }

    @Override
    public void onProvisioningFailed(UnprovisionedMeshNode meshNode, ProvisioningState.States state, byte[] data) {
        Log.d(TAG, "onProvisioningFailed");

    }

    @Override
    public void onProvisioningCompleted(ProvisionedMeshNode meshNode, ProvisioningState.States state, byte[] data) {
        Log.d(TAG, "ProvisioningComplete");

    }

    @Override
    public void onTransactionFailed(byte[] dst, boolean hasIncompleteTimerExpired) {

    }

    @Override
    public void onUnknownPduReceived(byte[] src, byte[] accessPayload) {

    }

    @Override
    public void onBlockAcknowledgementSent(byte[] dst) {
        Log.d(TAG, "onBLockAcknoledgementSend");

    }

    @Override
    public void onBlockAcknowledgementReceived(byte[] src) {
        Log.d(TAG, "onBlockAcknowledgementReceived");

    }

    @Override
    public void onMeshMessageSent(byte[] dst, MeshMessage meshMessage) {

    }

    @Override
    public void onMeshMessageReceived(byte[] src, MeshMessage meshMessage) {

    }

    @Override
    public void onMessageDecryptionFailed(String meshLayer, String errorMessage) {

    }
}
