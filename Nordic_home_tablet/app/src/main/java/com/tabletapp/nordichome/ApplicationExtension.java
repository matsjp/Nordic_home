package com.tabletapp.nordichome;

import android.app.Application;
import android.util.Log;


public class ApplicationExtension extends Application {
    //Be careful when editing this file.
    //Read this before touching anything https://github.com/codepath/android_guides/wiki/Understanding-the-Android-Application-Class
    private ScannerRepo scannerRepo;
    private DriveServiceRepo driveServiceRepo;
    public final static String TAG = ApplicationExtension.class.getSimpleName();

    @Override
    public void onCreate() {
        super.onCreate();
        //Creating a scanner repo instance that is accessible from all
        scannerRepo = new ScannerRepo(this);
        Log.d(TAG, scannerRepo.getMeshManagerApi().toString());
        Log.d(TAG, "Creating scanner repo");
    }

    public ScannerRepo getScannerRepo(){
        return scannerRepo;
    }

    public void setDriveServiceRepo(DriveServiceRepo driveServiceRepo){
        this.driveServiceRepo = driveServiceRepo;

    }

    public DriveServiceRepo getDriveServiceRepo(){
        return driveServiceRepo;
    }
}
