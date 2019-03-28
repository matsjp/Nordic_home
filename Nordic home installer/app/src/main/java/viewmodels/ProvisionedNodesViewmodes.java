package viewmodels;

import android.arch.lifecycle.LiveData;
import android.arch.lifecycle.MutableLiveData;
import android.arch.lifecycle.ViewModel;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

import no.nordicsemi.android.meshprovisioner.transport.ProvisionedMeshNode;

public class ProvisionedNodesViewmodes extends ViewModel {
    private ArrayList<ProvisionedMeshNode> nodesArrayList = new ArrayList<ProvisionedMeshNode>();
    private MutableLiveData<List<ProvisionedMeshNode>> nodes;
    public LiveData<List<ProvisionedMeshNode>> getNodes() {
        if (nodes == null) {
            nodes = new MutableLiveData<List<ProvisionedMeshNode>>();
        }
        return nodes;
    }

    public void addNode(ProvisionedMeshNode node){
        if (!nodesArrayList.contains(node)) {
            nodesArrayList.add(node);
            nodes.setValue(nodesArrayList);
        }
    }

    public void setNodesArrayList(ArrayList<ProvisionedMeshNode> nodesList){
        nodesArrayList = nodesList;
        nodes.setValue(nodesArrayList);
    }
}
