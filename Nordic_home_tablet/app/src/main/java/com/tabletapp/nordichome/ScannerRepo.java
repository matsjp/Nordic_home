package com.tabletapp.nordichome;

import android.arch.lifecycle.MutableLiveData;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.os.Handler;
import android.os.ParcelUuid;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.util.Log;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import no.nordicsemi.android.meshprovisioner.Group;
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
import no.nordicsemi.android.meshprovisioner.transport.ConfigModelSubscriptionAdd;
import no.nordicsemi.android.meshprovisioner.transport.ConfigModelSubscriptionStatus;
import no.nordicsemi.android.meshprovisioner.transport.Element;
import no.nordicsemi.android.meshprovisioner.transport.GenericOnOffStatus;
import no.nordicsemi.android.meshprovisioner.transport.MeshMessage;
import no.nordicsemi.android.meshprovisioner.transport.MeshModel;
import no.nordicsemi.android.meshprovisioner.transport.ProvisionedMeshNode;
import no.nordicsemi.android.meshprovisioner.transport.SceneRegisterStatus;
import no.nordicsemi.android.meshprovisioner.transport.SceneStore;
import no.nordicsemi.android.support.v18.scanner.BluetoothLeScannerCompat;
import no.nordicsemi.android.support.v18.scanner.ScanCallback;
import no.nordicsemi.android.support.v18.scanner.ScanFilter;
import no.nordicsemi.android.support.v18.scanner.ScanRecord;
import no.nordicsemi.android.support.v18.scanner.ScanResult;
import no.nordicsemi.android.support.v18.scanner.ScanSettings;

import static com.tabletapp.nordichome.BleMeshManager.MESH_PROXY_UUID;
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
    private Map<MeshModel, Integer> meshModels = new HashMap<>();

    private Boolean scenePreset = false;
    private Group scenePresetGroup;
    private int sceneNum;

    private Group selectedGroup = null;

    private UnprovisionedMeshNode currentUnprovisionedMeshNode;

    private MutableLiveData<Boolean> identifyReady = new MutableLiveData<>();
    private MutableLiveData<Boolean> provisioningReady = new MutableLiveData<>();
    private MutableLiveData<BluetoothDevice> connectedDevice = new MutableLiveData<>();
    private MutableLiveData<Boolean> isConnecting = new MutableLiveData<>();
    private MutableLiveData<Boolean> isDisconnecting = new MutableLiveData<>();
    private MutableLiveData<Boolean> provisioningComplete = new MutableLiveData<>();

    private MutableLiveData<Boolean> importSuccessSignal = new MutableLiveData<>();
    private MutableLiveData<Boolean> importFailSignal = new MutableLiveData<>();

    public MutableLiveData<Boolean> getIdentifyReady(){ return identifyReady; }

    public MutableLiveData<Boolean> getProvisioningReady(){ return provisioningReady; }

    public MutableLiveData<Boolean> getImportSuccessSignal(){
        return importSuccessSignal;
    }

    public MutableLiveData<Boolean> getImportFailSignal(){
        return  importFailSignal;
    }

    public MutableLiveData<Boolean> getProvisioningComplete(){ return provisioningComplete; }


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

    public void setSelectedGroup(Group group){
        this.selectedGroup = group;
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

    public void identifyNode(UnprovisionedBeacon beacon){
        mMeshManagerApi.identifyNode(beacon.getUuid(), "Temp name");
    }

    public void provisionCurrentUnprovisionedMesNode(){
        if (currentUnprovisionedMeshNode != null && provisioningReady.getValue()) {
            mMeshManagerApi.startProvisioning(currentUnprovisionedMeshNode);
            provisioningReady.postValue(false);
        }
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
            try {
                if (mUnprovisionedDevicesLiveData.deviceDiscovered(result, mMeshManagerApi.getMeshBeacon(getServiceData(result, BleMeshManager.MESH_PROVISIONING_UUID)))) {
                    mUnprovisionedDevicesLiveData.applyFilter();
                }
            }
            catch(IllegalArgumentException e){
                Log.d(TAG, "Illegal argument. No clue why it's broken " + e.toString());
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
        Log.d(TAG, "onDeviceConnecting");
    }

    @Override
    public void onDeviceConnected(BluetoothDevice device) {
        Log.d(TAG, "onDeviceConnected");
        isConnecting.postValue(false);
        connectedDevice.postValue(device);
    }

    @Override
    public void onDeviceDisconnecting(BluetoothDevice device) {
        Log.d(TAG, "onDeviceDisconnecting");
    }

    @Override
    public void onDeviceDisconnected(BluetoothDevice device) {
        Log.d(TAG, "onDeviceDisconnected");
        connectedDevice.postValue(null);
        stopScan();
        if (isReconnecting){
            isReconnecting = false;
            final List<ScanFilter> filters = new ArrayList<>();
            final BluetoothLeScannerCompat scanner2 = BluetoothLeScannerCompat.getScanner();

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
            final ConfigCompositionDataGet configCompositionDataGet = new ConfigCompositionDataGet();
            Log.d(TAG, "Pause");
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.d(TAG, "Sending mesh message");
                    mMeshManagerApi.sendMeshMessage(reconnectionNode.getUnicastAddress(), configCompositionDataGet);
                }
            }, 1000);
        }
        else {
            identifyReady.postValue(true);
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
        Log.d(TAG, meshNetwork.getMeshUUID());
        //mMeshManagerApi.exportMeshNetwork(context.getFilesDir().getPath());
    }

    @Override
    public void onNetworkUpdated(MeshNetwork meshNetwork) {
        this.meshNetwork = meshNetwork;
    }

    @Override
    public void onNetworkLoadFailed(String error) {

    }

    private void loadNetwork(final MeshNetwork meshNetwork) {
        this.meshNetwork = meshNetwork;
    }

    @Override
    public void onNetworkImported(MeshNetwork meshNetwork) {
        Log.d(TAG, "onNetworkmported");
        final MeshNetwork oldNet = this.meshNetwork;
        if (!oldNet.getMeshUUID().equals(meshNetwork.getMeshUUID())) {
            mMeshManagerApi.deleteMeshNetworkFromDb(oldNet);
        }
        loadNetwork(meshNetwork);
        Log.d(TAG, meshNetwork.getMeshUUID());
        importSuccessSignal.postValue(true);
        //Pay attention to global meshNetwork variable
    }

    @Override
    public void onNetworkImportFailed(String error) {
        Log.d(TAG, "onNetworkImportFailed");
        importFailSignal.postValue(true);
    }

    @Override
    public void onNetworkExported(MeshNetwork meshNetwork) {
        Log.d(TAG, "onNetworkExported");

    }

    @Override
    public void onNetworkExportedJson(MeshNetwork meshNetwork, String networkJson) {
        Log.d(TAG, "onNetworkExportedJson");
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
        currentUnprovisionedMeshNode = meshNode;
        if (state == PROVISIONING_CAPABILITIES){
            Log.d(TAG, "Starting provisioning");
            provisioningReady.postValue(true);
            //mMeshManagerApi.startProvisioning(meshNode);
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
    public void onTransactionFailed(int dst, boolean hasIncompleteTimerExpired) {

    }

    @Override
    public void onUnknownPduReceived(byte[] src, byte[] accessPayload) {
        Log.d(TAG, "onUnknownPduReceived");
    }

    @Override
    public void onUnknownPduReceived(int src, byte[] accessPayload) {

    }

    @Override
    public void onBlockAcknowledgementSent(byte[] dst) {
        Log.d(TAG, "onBLockAcknoledgementSend");

    }

    @Override
    public void onBlockAcknowledgementSent(int dst) {

    }

    @Override
    public void onBlockAcknowledgementReceived(byte[] src) {
        Log.d(TAG, "onBlockAcknowledgementReceived");

    }

    @Override
    public void onBlockAcknowledgementReceived(int src) {

    }

    @Override
    public void onMeshMessageSent(byte[] dst, MeshMessage meshMessage) {
        Log.d(TAG, "onMeshMessageSent");
        Log.d(TAG, meshMessage.toString());

    }

    @Override
    public void onMeshMessageSent(int dst, MeshMessage meshMessage) {
        Log.d(TAG, "onMEshMessageSent");
        Log.d(TAG, meshMessage.toString());
    }

    @Override
    public void onMeshMessageReceived(final int src, MeshMessage meshMessage) {
        Log.d(TAG, "onMesMessageReceived");
        Log.d(TAG, meshMessage.toString());
        if (meshMessage instanceof ConfigCompositionDataStatus){
            Log.d(TAG, "ConfigCompositionDataStatus");
            configCompositionDataStatus = (ConfigCompositionDataStatus) meshMessage;
            final ConfigAppKeyAdd configAppKeyAdd = new ConfigAppKeyAdd(mMeshManagerApi.getMeshNetwork().getNetKeys().get(0), mMeshManagerApi.getMeshNetwork().getAppKey(0));
            handler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mMeshManagerApi.sendMeshMessage(src, configAppKeyAdd);
                }
            }, 200);
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
                    Integer elementAddress = meshModels.remove(meshModel);
                    if (elementAddress != null) {
                        final ConfigModelAppBind configModelAppBind = new ConfigModelAppBind(elementAddress, meshModel.getModelId(), 0);
                        handler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                mMeshManagerApi.sendMeshMessage(src, configModelAppBind);
                            }
                        }, 200);
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
                Integer elementAddress = meshModels.remove(meshModel);
                if (elementAddress != null) {
                    ConfigModelAppBind configModelAppBind = new ConfigModelAppBind(elementAddress, meshModel.getModelId(), 0);
                    mMeshManagerApi.sendMeshMessage(src, configModelAppBind);
                }
            }
            else{
                Log.d(TAG, "Done inding keys, adding groups");
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
                    Integer elementAddress = meshModels.remove(meshModel);
                    if (elementAddress != null) {
                        ConfigModelSubscriptionAdd configModelSubscriptionAdd = new ConfigModelSubscriptionAdd(elementAddress, selectedGroup.getGroupAddress(), meshModel.getModelId());
                        mMeshManagerApi.sendMeshMessage(src, configModelSubscriptionAdd);
                    }
                }
            }
        }
        else if (meshMessage instanceof GenericOnOffStatus){
            Log.d(TAG, "GenericOnOffStatus");
            GenericOnOffStatus genericOnOffStatus = (GenericOnOffStatus) meshMessage;
            Log.d(TAG, "Current light state: " + Boolean.toString(genericOnOffStatus.getPresentState()));
            if (scenePreset){
                Log.d(TAG, "Sending scene message");
                scenePreset = false;
                byte[] appKey = getMeshManagerApi().getMeshNetwork().getAppKey(0).getKey();
                SceneStore sceneStore = new SceneStore(appKey, sceneNum);
                getMeshManagerApi().sendMeshMessage(scenePresetGroup.getGroupAddress(), sceneStore);
            }
        }
        else if (meshMessage instanceof ConfigModelSubscriptionStatus){
            Log.d(TAG, "ConfigModelSubscriptionStatus");
            if (((ConfigModelSubscriptionStatus) meshMessage).isSuccessful()){
                Log.d(TAG, "ConfigModelSubscriptionStatus: Success");
            }
            else {
                Log.d(TAG, "ConfigModelSubscriptionStatus: Fail");
            }
            if (!meshModels.isEmpty()){
                MeshModel meshModel = new ArrayList<>(meshModels.keySet()).get(0);
                Integer elementAddress = meshModels.remove(meshModel);
                if (elementAddress != null) {
                    ConfigModelSubscriptionAdd configModelSubscriptionAdd = new ConfigModelSubscriptionAdd(elementAddress, selectedGroup.getGroupAddress(), meshModel.getModelId());
                    mMeshManagerApi.sendMeshMessage(src, configModelSubscriptionAdd);
                }
            }
            else{
                Log.d(TAG, "Done provisioning");
                provisioningComplete.postValue(true);
                provisioningReady.postValue(false);
                identifyReady.postValue(false);
            }
        }
        else if (meshMessage instanceof SceneRegisterStatus){
            Log.d(TAG, "SceneRegisterStatus");
        }
    }

    @Override
    public void onMeshMessageReceived(byte[] src, MeshMessage meshMessage) {
        Log.d(TAG, "onMeshMessageReceived");
        Log.d(TAG, meshMessage.toString());

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

    public void presetScene(Group group, MeshMessage meshMessage, int sceneNum){
        scenePreset = true;
        scenePresetGroup = group;
        this.sceneNum = sceneNum;

        mMeshManagerApi.sendMeshMessage(group.getGroupAddress(), meshMessage);
    }
}
