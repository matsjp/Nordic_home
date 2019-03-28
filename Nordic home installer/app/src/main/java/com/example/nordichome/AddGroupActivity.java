package com.example.nordichome;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import no.nordicsemi.android.meshprovisioner.Group;

public class AddGroupActivity extends AppCompatActivity {
    private Button addGroup;
    private EditText groupName;
    private ApplicationExtension application;
    public final static String TAG = AddGroupActivity.class.getSimpleName();


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_group);
        application = (ApplicationExtension) getApplication();
        groupName = findViewById(R.id.set_group_name);
        addGroup = findViewById(R.id.add_group);

        addGroup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                int address = Integer.decode("0xC100");
                while(!application.getScannerRepo().getMeshManagerApi().getMeshNetwork().addGroup(address, groupName.getText().toString())){
                    address++;
                }
                Log.d(TAG, "Group added");
                Log.d(TAG, Integer.toString(application.getScannerRepo().getMeshManagerApi().getMeshNetwork().getGroups().size()));
                Intent intent = new Intent(AddGroupActivity.this, GroupsActivity.class);
                startActivity(intent);
            }
        });
    }
}
