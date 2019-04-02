package com.tabletapp.nordichome;

import android.util.Log;

public class Utils {

    private static final String TAG = Utils.class.getSimpleName();

    public static int addressBytesToInt(byte[] address){
        String binaryString = "";
        for (byte b : address){
            binaryString = binaryString + String.format("%8s", Integer.toBinaryString(b & 0xFF)).replace(' ', '0');
        }
        Log.d(TAG, binaryString);
        return Integer.parseInt(binaryString, 2);
    }
}
