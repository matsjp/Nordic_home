package com.tabletapp.nordichome;

import android.app.Application;


import no.nordicsemi.android.meshprovisioner.MeshNetwork;

public class ApplicationExtension extends Application {
    //Be careful when editing this file.
    //Read this before touching anything https://github.com/codepath/android_guides/wiki/Understanding-the-Android-Application-Class
    private ScannerRepo scannerRepo;
    private DriveServiceRepo driveServiceRepo;

    @Override
    public void onCreate() {
        super.onCreate();
        //Creating a scanner repo instance that is accessible from all
        scannerRepo = new ScannerRepo(this);
    }

    public ScannerRepo getScannerRepo(){
        return scannerRepo;
    }

    public void setDriveServiceRepo(DriveServiceRepo driveServiceRepo){
        this.driveServiceRepo = driveServiceRepo;
        ApplicationExtension application = (ApplicationExtension) getApplicationContext();
        ScannerRepo scannerRepo = application.getScannerRepo();
        MeshNetwork network = scannerRepo.getMeshManagerApi().getMeshNetwork();



    }

    public DriveServiceRepo getDriveServiceRepo(){
        return driveServiceRepo;
    }
}
