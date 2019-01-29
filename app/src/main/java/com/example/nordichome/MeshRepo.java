package com.example.nordichome;

import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothGattCharacteristic;
import android.content.Context;
import android.icu.text.LocaleDisplayNames;
import android.os.Handler;
import android.util.Log;

import java.util.UUID;

import no.nordicsemi.android.log.LogSession;
import no.nordicsemi.android.meshprovisioner.MeshManagerApi;
import no.nordicsemi.android.meshprovisioner.MeshManagerCallbacks;
import no.nordicsemi.android.meshprovisioner.MeshNetwork;
import no.nordicsemi.android.meshprovisioner.MeshProvisioningStatusCallbacks;
import no.nordicsemi.android.meshprovisioner.MeshStatusCallbacks;
import no.nordicsemi.android.meshprovisioner.provisionerstates.ProvisioningState;
import no.nordicsemi.android.meshprovisioner.provisionerstates.UnprovisionedMeshNode;
import no.nordicsemi.android.meshprovisioner.transport.MeshMessage;
import no.nordicsemi.android.meshprovisioner.transport.ProvisionedMeshNode;
import no.nordicsemi.android.meshprovisioner.utils.MeshParserUtils;

import static android.support.constraint.Constraints.TAG;

public class MeshRepo implements MeshProvisioningStatusCallbacks, MeshStatusCallbacks, MeshManagerCallbacks, BleMeshManagerCallbacks {
    private int mtuSize = 20;
    private final static String TAG = "MeshRepo";
    private UnprovisionedMeshNode mUnprovisionedMeshNode;

    @Override
    public void onTransactionFailed(final byte[] dst, final boolean hasIncompleteTimerExpired) {
        Log.d(TAG, "onTransactionFailed");
    }

    @Override
    public void onUnknownPduReceived(final byte[] src, final byte[] accessPayload) {
        Log.d(TAG, "onUnknownPduReceived");
    }

    @Override
    public void onBlockAcknowledgementSent(final byte[] dst) {
        Log.d(TAG, "onBLockAcknoledgementSent");
    }

    @Override
    public void onBlockAcknowledgementReceived(final byte[] src) {
        Log.d(TAG, "onBlockAcknowledgementReceived");
    }

    @Override
    public void onMeshMessageSent(final byte[] dst, final MeshMessage meshMessage) {
        Log.d(TAG, "nMeshMessageSent");
    }

    @Override
    public void onMeshMessageReceived(final byte[] src, final MeshMessage meshMessage) {
        Log.d(TAG, "onMeshMessageReceived");
    }

    @Override
    public void onMessageDecryptionFailed(final String meshLayer, final String errorMessage) {
        Log.d(TAG, "onMessageDecryptionFailed");
    }

    @Override
    public void onDataReceived(final BluetoothDevice bluetoothDevice, final int mtu, final byte[] pdu) {
        mMeshManagerApi.handleNotifications(mtu, pdu);
    }

    @Override
    public void onDataSent(final BluetoothDevice device, final int mtu, final byte[] pdu) {
        mMeshManagerApi.handleWriteCallbacks(mtu, pdu);
    }

    @Override
    public void onDeviceConnecting(final BluetoothDevice device) {
        Log.d(TAG, "onDeviceConnecting");
    }

    @Override
    public void onDeviceConnected(final BluetoothDevice device) {
        Log.d(TAG, "onDeviceConnected");
    }

    @Override
    public void onDeviceDisconnecting(final BluetoothDevice device) {
        Log.v(TAG, "onDeviceDisconnecting");
    }

    @Override
    public void onDeviceDisconnected(final BluetoothDevice device) {
        Log.d(TAG, "onDeviceDisconnected");
    }

    @Override
    public void onLinklossOccur(final BluetoothDevice device) {
        Log.v(TAG, "onLinklossOccur");
    }

    @Override
    public void onServicesDiscovered(final BluetoothDevice device, final boolean optionalServicesFound) {
        Log.d(TAG, "onServiceDiscovered");
    }

    @Override
    public void onDeviceReady(final BluetoothDevice device) {
    }

    @Override
    public boolean shouldEnableBatteryLevelNotifications(final BluetoothDevice device) {
        return false;
    }

    @Override
    public void onBatteryValueReceived(final BluetoothDevice device, final int value) {

    }

    @Override
    public void onBondingRequired(final BluetoothDevice device) {

    }

    @Override
    public void onBonded(final BluetoothDevice device) {

    }

    @Override
    public void onError(final BluetoothDevice device, final String message, final int errorCode) {
        Log.d(TAG, "onError");
    }

    @Override
    public void onDeviceNotSupported(final BluetoothDevice device) {

    }

    @Override
    public void onProvisioningStateChanged(final UnprovisionedMeshNode meshNode, final ProvisioningState.States state, final byte[] data) {
        Log.d("MeshRepo", "onProvisioningStateChanged state: " + state.toString());
        mUnprovisionedMeshNode = meshNode;
    }

    @Override
    public void onProvisioningFailed(final UnprovisionedMeshNode meshNode, final ProvisioningState.States state, final byte[] data) {
        Log.d("MeshRepo", "onProvisioningFailed");
    }

    private void onProvisioningCompleted(final ProvisionedMeshNode node) {
        Log.d("MeshRepo", "onProvisioningComplete");
    }

    @Override
    public void onProvisioningCompleted(final ProvisionedMeshNode meshNode, final ProvisioningState.States state, final byte[] data) {
        Log.d("MeshRepo", "onProvisioningComplete");
    }

    @Override
    public void onNetworkLoaded(final MeshNetwork meshNetwork) {
        Log.d("MeshRepo", "onNetworkLoaded");
    }

    @Override
    public void onNetworkUpdated(final MeshNetwork meshNetwork) {
        Log.d("MeshRepo", "onNetworkUpdated");
    }

    @Override
    public void onNetworkLoadFailed(final String error) {
        Log.d("MeshRepo", "onNetworkLoadFailed");
    }

    @Override
    public void onNetworkImported(final MeshNetwork meshNetwork) {
        Log.d("MeshRepo", "onNetworkImported");
    }

    @Override
    public void onNetworkExported(final MeshNetwork meshNetwork) {
        Log.d("MeshRepo", "onNetworkExported");
    }

    @Override
    public void onNetworkExportFailed(final String error) {
        Log.d("MeshRepo", "onNetworkExportedFail");
    }

    @Override
    public void onNetworkImportFailed(final String error) {
        Log.d("MeshRepo", "onNetworkImportFailed");
    }

    @Override
    public void sendProvisioningPdu(final UnprovisionedMeshNode meshNode, final byte[] pdu) {
        Log.d("MeshRepo", "sendProvisioningPdu");
        mBleMeshManager.sendPdu(pdu);
    }

    @Override
    public void sendMeshPdu(final byte[] pdu) {
        Log.d("MeshRepo", "sendMeshPdu");
    }

    @Override
    public int getMtu() {
        Log.d("MeshRepo", "getMtu");
        return mtuSize;
    }

    private MeshManagerApi mMeshManagerApi;
    private BleMeshManager mBleMeshManager;
    Handler mHandler;

    public MeshRepo(Context context){
        mMeshManagerApi = new MeshManagerApi(context);
        mMeshManagerApi.setMeshManagerCallbacks(this);
        mMeshManagerApi.setProvisioningStatusCallbacks(this);
        mMeshManagerApi.setMeshStatusCallbacks(this);
        mMeshManagerApi.loadMeshNetwork();

        mBleMeshManager = new BleMeshManager( context);
        mBleMeshManager.setGattCallbacks(this);
        mHandler = new Handler();
    }

    public MeshManagerApi getMeshManagerApi(){
        return mMeshManagerApi;
    }

    public BleMeshManager getBleMeshManager(){
        return mBleMeshManager;
    }

    void connect(final BluetoothDevice device) {
        mBleMeshManager.connect(device);
    }

    public UnprovisionedMeshNode getUnprovisionedMeshNode(){
        return mUnprovisionedMeshNode;
    }
}
