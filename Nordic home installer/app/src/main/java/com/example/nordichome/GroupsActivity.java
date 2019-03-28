package com.example.nordichome;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.example.nordichome.adapter.GroupAdapter;
import com.example.nordichome.adapter.ProvisionedNodesAdapter;

import java.util.ArrayList;

import no.nordicsemi.android.meshprovisioner.Group;

public class GroupsActivity extends AppCompatActivity {
    private FloatingActionButton button;
    private GroupAdapter adapter;
    public static final String TAG = GroupsActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groups);
        button = findViewById(R.id.add_group);
        RecyclerView recyclerView = findViewById(R.id.recycler_view_groups);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new GroupAdapter(this);
        recyclerView.setAdapter(adapter);
        TextView noGroupsText = findViewById(R.id.no_groups_text);
        noGroupsText.setVisibility(View.VISIBLE);

        ApplicationExtension application = (ApplicationExtension) getApplication();
        for (Group group : application.getScannerRepo().getMeshManagerApi().getMeshNetwork().getGroups()){
            adapter.addData(group);
        }
        Log.d(TAG, Integer.toString(adapter.getItemCount()));

        if (adapter.getItemCount() > 0){
            noGroupsText.setVisibility(View.GONE);
        }

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupsActivity.this, AddGroupActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);
    }
}
