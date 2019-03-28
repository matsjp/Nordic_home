package com.example.nordichome;

import android.arch.lifecycle.ViewModelProviders;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.nordichome.adapter.ProvisionedNodesAdapter;

import java.util.ArrayList;
import java.util.Map;

import no.nordicsemi.android.meshprovisioner.Group;
import no.nordicsemi.android.meshprovisioner.transport.ConfigModelSubscriptionAdd;
import no.nordicsemi.android.meshprovisioner.transport.Element;
import no.nordicsemi.android.meshprovisioner.transport.GenericOnOffSet;
import no.nordicsemi.android.meshprovisioner.transport.MeshModel;
import no.nordicsemi.android.meshprovisioner.transport.ProvisionedMeshNode;
import viewmodels.ProvisionedNodesViewmodes;
import viewmodels.ScannerRepo;

public class GroupConfigActivity extends AppCompatActivity implements ProvisionedNodesAdapter.OnItemClickListener{
    private Group group;
    private ProvisionedNodesViewmodes view;
    private ProvisionedNodesAdapter adapter;
    public final static String TAG = GroupConfigActivity.class.getSimpleName();
    private Button button;
    private Boolean lightState = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_group_config);
        Intent intent = getIntent();
        group = intent.getParcelableExtra("group");

        TextView noProvisionedNodes = findViewById(R.id.no_provisioned_nodes);

        final ProvisionedNodesViewmodes view = ViewModelProviders.of(this).get(ProvisionedNodesViewmodes.class);
        this.view = view;
        RecyclerView recyclerView = findViewById(R.id.recycler_view_devices);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ArrayList<String> data = new ArrayList<String>();
        adapter = new ProvisionedNodesAdapter(this, view);
        adapter.setOnItemClickListener(this);
        recyclerView.setAdapter(adapter);

        ApplicationExtension application = (ApplicationExtension) getApplication();
        ArrayList<ProvisionedMeshNode> groupNodes;
        ArrayList<ProvisionedMeshNode> nodes = new ArrayList<>(application.getScannerRepo().getMeshManagerApi().getMeshNetwork().getProvisionedNodes());
        Boolean nodeAdded;
        for (ProvisionedMeshNode node : new ArrayList<>(nodes)){
            nodeAdded = false;
            for (Element element : new ArrayList<>(node.getElements().values())){
                for (MeshModel model : new ArrayList<>(element.getMeshModels().values())){
                    if (model.getSubscribedAddresses().contains(group.getGroupAddress())){
                        nodes.add(node);
                        nodeAdded = true;
                        break;
                    }
                }
                if (nodeAdded){
                    break;
                }
            }
        }
        view.setNodesArrayList(new ArrayList<>(application.getScannerRepo().getMeshManagerApi().getMeshNetwork().getProvisionedNodes()));

        if (adapter.getItemCount() > 0){
            noProvisionedNodes.setVisibility(View.GONE);
        }

        FloatingActionButton addNodeButton = findViewById(R.id.add_node);
        addNodeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupConfigActivity.this, ScannerActivity.class);
                intent.putExtra("group", group);
                startActivity(intent);
            }
        });

        button = findViewById(R.id.group_message);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ScannerRepo scannerRepo = application.getScannerRepo();
                byte[] appKey = scannerRepo.getMeshManagerApi().getMeshNetwork().getAppKey(0).getKey();
                GenericOnOffSet genericOnOffSet = new GenericOnOffSet(appKey, lightState, 500);
                lightState = !lightState;
                BleMeshManager meshManager = scannerRepo.getBleMeshManager();
                Log.d(TAG, Boolean.toString(meshManager.isConnected()));
                scannerRepo.getMeshManagerApi().sendMeshMessage(group.getGroupAddress(), genericOnOffSet);
            }
        });

        Button sceneButton = findViewById(R.id.scene_button);

        sceneButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GroupConfigActivity.this, SceneActivity.class);
                intent.putExtra("group", group);
                startActivity(intent);
            }
        });
    }

    @Override
    public void onItemClick(ProvisionedMeshNode node) {
        /*ApplicationExtension application = (ApplicationExtension) getApplication();
        node = application.getScannerRepo().getMeshManagerApi().getMeshNetwork().getProvisionedNode(node.getUnicastAddress());
        Log.d(TAG, Integer.toString(new ArrayList<Integer>(node.getElements().keySet()).get(0)));
        int elementAddress = new ArrayList<>(node.getElements().keySet()).get(0);
        Element element = node.getElements().get(elementAddress);
        MeshModel model = null;
        for (Map.Entry<Integer, MeshModel> meshModel : element.getMeshModels().entrySet()){
            if (meshModel.getValue().getModelName().equals("Generic On Off Server")){
                model = meshModel.getValue();
                Log.d(TAG, "Found the model");
                break;
            }
        }
        ConfigModelSubscriptionAdd configModelSubscriptionAdd = new ConfigModelSubscriptionAdd(element.getElementAddress(), group.getGroupAddress(), model.getModelId());
        application.getScannerRepo().getMeshManagerApi().sendMeshMessage(node.getUnicastAddress(), configModelSubscriptionAdd);*/
    }

    @Override
    public void onBackPressed(){
        Intent intent = new Intent(this, GroupsActivity.class);
        startActivity(intent);
    }
}
