package com.example.nordichome;

import android.arch.lifecycle.ViewModel;
import android.arch.lifecycle.ViewModelProvider;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import no.nordicsemi.android.meshprovisioner.transport.MeshModel;

public class LightActivity extends AppCompatActivity {

    private static final String TAG = LightActivity.class.getSimpleName();

    

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_light);

    }



}
