package viewmodels;

import android.app.Application;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.ColorSpace;
import android.os.Handler;
import android.os.Looper;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import com.example.nordichome.BleMeshManager;
import com.example.nordichome.BleMeshManagerCallbacks;
import com.example.nordichome.adapter.DiscoveredBluetoothDevice;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;
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
import no.nordicsemi.android.meshprovisioner.transport.ConfigAppKeyAdd;
import no.nordicsemi.android.meshprovisioner.transport.ConfigAppKeyStatus;
import no.nordicsemi.android.meshprovisioner.transport.ConfigCompositionDataGet;
import no.nordicsemi.android.meshprovisioner.transport.ConfigCompositionDataStatus;
import no.nordicsemi.android.meshprovisioner.transport.ConfigModelAppBind;
import no.nordicsemi.android.meshprovisioner.transport.ConfigModelAppStatus;
import no.nordicsemi.android.meshprovisioner.transport.Element;
import no.nordicsemi.android.meshprovisioner.transport.GenericOnOffSet;
import no.nordicsemi.android.meshprovisioner.transport.GenericOnOffStatus;
import no.nordicsemi.android.meshprovisioner.transport.MeshMessage;
import no.nordicsemi.android.meshprovisioner.transport.MeshModel;
import no.nordicsemi.android.meshprovisioner.transport.ProvisionedMeshNode;
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanRecord;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;
import viewmodels.DevicesLiveData;

import static com.example.nordichome.BleMeshManager.MESH_PROXY_UUID;
import static no.nordicsemi.android.meshprovisioner.provisionerstates.ProvisioningState.States.PROVISIONING_CAPABILITIES;

public class ScannerRepo implements BleMeshManagerCallbacks, MeshManagerCallbacks, MeshStatusCallbacks, MeshProvisioningStatusCallbacks {

    private MeshManagerApi mMeshManagerApi;
    private BleMeshManager mBleMeshManager;
    private boolean isScanning = false;
    private DevicesLiveData mUnprovisionedDevicesLiveData = new DevicesLiveData(true, false);
    private DiscoveredBluetoothDevice discoveredBluetoothDevice;
    public final static String TAG = "ScannerRepo";
    private Context context;
    private boolean isReconnecting = false;
    private ProvisionedMeshNode reconnectionNode;
    private boolean alreadyProvisioned = false;
    private Handler handler;
    private ConfigCompositionDataStatus configCompositionDataStatus;
    private MeshNetwork meshNetwork;
    private Map<MeshModel, byte[]> meshModels = new HashMap<>();

    public ScannerRepo(Context context){
        mBleMeshManager = new BleMeshManager(context);
        mBleMeshManager.setGattCallbacks(this);
        mMeshManagerApi = new MeshManagerApi(context);
        mMeshManagerApi.setMeshManagerCallbacks(this);
        mMeshManagerApi.setProvisioningStatusCallbacks(this);
        mMeshManagerApi.setMeshStatusCallbacks(this);
        mMeshManagerApi.loadMeshNetwork();
        handler = new Handler(context.getMainLooper());
        this.context = context;
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
        Log.d(TAG, "onDeviceDisconnected");
        stopScan();
        if (isReconnecting){
            isReconnecting = false;
            final List<ScanFilter> filters = new ArrayList<>();
            BluetoothLeScannerCompat scanner2 = BluetoothLeScannerCompat.getScanner();

            final ScanSettings settings = new ScanSettings.Builder()
                    .setScanMode(ScanSettings.SCAN_MODE_LOW_LATENCY)
                    // Refresh the devices list every second
                    .setReportDelay(0)
                    // Hardware filtering has some issues on selected devices
                    .setUseHardwareFilteringIfSupported(false)
                    // Samsung S6 and S6 Edge report equal value of RSSI for all devices. In this app we ignore the RSSI.
                    /*.setUseHardwareBatchingIfSupported(false)*/
                    .build();
            scanner2.startScan(filters, settings, new ScanCallback() {
                @Override
                public void onScanResult(int callbackType, ScanResult result) {
                    Log.d(TAG, "onScanResult");
                    final ScanRecord scanRecord = result.getScanRecord();
                    if (scanRecord != null) {
                        final byte[] serviceData = getServiceData(result, MESH_PROXY_UUID);
                        if (serviceData != null) {
                            if (mMeshManagerApi.isAdvertisedWithNodeIdentity(serviceData)) {
                                final ProvisionedMeshNode node = reconnectionNode;
                                if (mMeshManagerApi.nodeIdentityMatches(node, serviceData)) {
                                    scanner2.stopScan(this);
                                    alreadyProvisioned = true;
                                    mBleMeshManager.connect(result.getDevice());
                                }
                            }
                        }
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
            });
            isScanning = true;
        }
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
        if (alreadyProvisioned){
            alreadyProvisioned = false;
            Log.d(TAG, "alreadyProvisioned");
            ConfigCompositionDataGet configCompositionDataGet = new ConfigCompositionDataGet();
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMeshManagerApi.sendMeshMessage(reconnectionNode.getUnicastAddress(), configCompositionDataGet);
                }
            }, 1000);
        }
        else {
            final UnprovisionedBeacon beacon = (UnprovisionedBeacon) discoveredBluetoothDevice.getBeacon();
            mMeshManagerApi.identifyNode(beacon.getUuid(), "Living Room");
        }
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
        Log.d(TAG, "onNetworkLoaded");
        this.meshNetwork = meshNetwork;
    }

    @Override
    public void onNetworkUpdated(MeshNetwork meshNetwork) {
        this.meshNetwork = meshNetwork;
    }

    @Override
    public void onNetworkLoadFailed(String error) {

    }

    @Override
    public void onNetworkImported(MeshNetwork meshNetwork) {
        Log.d(TAG, "onNetworkmported");
        //Pay attention to global meshNetwork variable
    }

    @Override
    public void onNetworkImportFailed(String error) {
        Log.d(TAG, "onNetworkImportFailed");
    }

    @Override
    public void onNetworkExported(MeshNetwork meshNetwork) {

    }

    @Override
    public void onNetworkExportFailed(String error) {
        Log.d(TAG, "onNetworkExportFailed");

    }

    @Override
    public void sendProvisioningPdu(UnprovisionedMeshNode meshNode, byte[] pdu) {
        Log.d("MeshRepo", "sendProvisioningPdu");
        mBleMeshManager.sendPdu(pdu);
    }

    @Override
    public void sendMeshPdu(byte[] pdu) {
        Log.d(TAG, "sendMeshPdu");
        mBleMeshManager.sendPdu(pdu);
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
        isReconnecting = true;
        reconnectionNode = meshNode;
        mBleMeshManager.disconnect();

    }

    @Override
    public void onTransactionFailed(byte[] dst, boolean hasIncompleteTimerExpired) {

    }

    @Override
    public void onUnknownPduReceived(byte[] src, byte[] accessPayload) {
        Log.d(TAG, "onUnknownPduReceived");
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
        Log.d(TAG, "onMeshMessageSent");
        Log.d(TAG, meshMessage.toString());

    }

    @Override
    public void onMeshMessageReceived(byte[] src, MeshMessage meshMessage) {
        Log.d(TAG, "onMesMessageReceived");
        if (meshMessage instanceof ConfigCompositionDataStatus){
            Log.d(TAG, "ConfigCompositionDataStatus");
            configCompositionDataStatus = (ConfigCompositionDataStatus) meshMessage;
            ConfigAppKeyAdd configAppKeyAdd = new ConfigAppKeyAdd(mMeshManagerApi.getMeshNetwork().getNetKeys().get(0), mMeshManagerApi.getMeshNetwork().getAppKey(0));
            mMeshManagerApi.sendMeshMessage(src, configAppKeyAdd);
        }
        else if (meshMessage instanceof ConfigAppKeyStatus){
            Log.d(TAG, "ConfigAppKeyStatus");
            if (((ConfigAppKeyStatus) meshMessage).isSuccessful()){
                Log.d(TAG, "ConfigAppKeyStatus: success!");
                ArrayList<Element> elements = new ArrayList<>(configCompositionDataStatus.getElements().values());
                for (Element element : elements){
                    ArrayList<MeshModel> meshModelList = new ArrayList<>(element.getMeshModels().values());
                    for (MeshModel meshModel : meshModelList){
                        Log.d(TAG, "Model:" + meshModel.getModelName());
                        meshModels.put(meshModel, element.getElementAddress());
                    }
                }
                if (!meshModels.isEmpty()){
                    MeshModel meshModel = new ArrayList<>(meshModels.keySet()).get(0);
                    byte[] elementAddress = meshModels.remove(meshModel);
                    if (elementAddress != null) {
                        ConfigModelAppBind configModelAppBind = new ConfigModelAppBind(elementAddress, meshModel.getModelId(), 0);
                        mMeshManagerApi.sendMeshMessage(src, configModelAppBind);
                    }
                }
            }
        }
        else if(meshMessage instanceof ConfigModelAppStatus){
            ConfigModelAppStatus configModelAppStatus = (ConfigModelAppStatus) meshMessage;
            if (((ConfigModelAppStatus) meshMessage).isSuccessful()){
                Log.d("TAG", "COnfigModelAppStatus: Success!");
            }
            if (!meshModels.isEmpty()){
                MeshModel meshModel = new ArrayList<>(meshModels.keySet()).get(0);
                byte[] elementAddress = meshModels.remove(meshModel);
                if (elementAddress != null) {
                    ConfigModelAppBind configModelAppBind = new ConfigModelAppBind(elementAddress, meshModel.getModelId(), 0);
                    mMeshManagerApi.sendMeshMessage(src, configModelAppBind);
                }
            }
            /*else{
                Log.d("TAG", "Light switch");
                Log.d(TAG, "Sending Generic On Off");
                byte[] appKey = mMeshManagerApi.getMeshNetwork().getAppKey(0).getKey();
                GenericOnOffSet genericOnOffSet = new GenericOnOffSet(appKey, true, 500);
                mMeshManagerApi.sendMeshMessage(src, genericOnOffSet);
            }*/
        }
        else if (meshMessage instanceof GenericOnOffStatus){
            Log.d(TAG, "GenericOnOffStatus");
            GenericOnOffStatus genericOnOffStatus = (GenericOnOffStatus) meshMessage;
            Log.d(TAG, "Current light state: " + Boolean.toString(genericOnOffStatus.getPresentState()));
        }
    }

    @Override
    public void onMessageDecryptionFailed(String meshLayer, String errorMessage) {

    }

    public MeshManagerApi getMeshManagerApi(){
        return mMeshManagerApi;
    }

    public BleMeshManager getBleMeshManager(){
        return mBleMeshManager;
    }
}
