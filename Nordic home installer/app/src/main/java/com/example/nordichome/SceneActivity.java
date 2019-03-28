package com.example.nordichome;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import no.nordicsemi.android.meshprovisioner.Group;
import no.nordicsemi.android.meshprovisioner.transport.GenericOnOffSet;
import no.nordicsemi.android.meshprovisioner.transport.SceneRecall;
import no.nordicsemi.android.meshprovisioner.transport.SceneStore;
import viewmodels.ScannerRepo;

public class SceneActivity extends AppCompatActivity {
    private Group group;
    private ScannerRepo scannerRepo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_scene);

        group = getIntent().getParcelableExtra("group");

        ApplicationExtension application = (ApplicationExtension) getApplication();
        scannerRepo = application.getScannerRepo();

        Button addScene = findViewById(R.id.add_scene);
        Button lightsOn = findViewById(R.id.lights_on);
        Button lightsOff = findViewById(R.id.lights_off);
        Button addOffScene = findViewById(R.id.add_off_scene);

        addScene.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] appKey = scannerRepo.getMeshManagerApi().getMeshNetwork().getAppKey(0).getKey();
                GenericOnOffSet genericOnOffSet = new GenericOnOffSet(appKey, true, 500);
                scannerRepo.presetScene(group, genericOnOffSet, 1);
            }
        });

        lightsOn.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                byte[] appKey = scannerRepo.getMeshManagerApi().getMeshNetwork().getAppKey(0).getKey();
                SceneRecall sceneRecall = new SceneRecall(appKey, 1, 500);
                scannerRepo.getMeshManagerApi().sendMeshMessage(group.getGroupAddress(), sceneRecall);
            }
        });

        lightsOff.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v){
                byte[] appKey = scannerRepo.getMeshManagerApi().getMeshNetwork().getAppKey(0).getKey();
                SceneRecall sceneRecall = new SceneRecall(appKey, 2, 500);
                scannerRepo.getMeshManagerApi().sendMeshMessage(group.getGroupAddress(), sceneRecall);
            }
        });

        addOffScene.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                byte[] appKey = scannerRepo.getMeshManagerApi().getMeshNetwork().getAppKey(0).getKey();
                SceneStore sceneStore = new SceneStore(appKey, 2);
                scannerRepo.getMeshManagerApi().sendMeshMessage(group.getGroupAddress(), sceneStore);
            }
        });
    }
}
