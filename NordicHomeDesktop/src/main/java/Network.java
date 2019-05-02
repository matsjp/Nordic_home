import java.lang.reflect.Array;
import java.util.*;
import com.google.gson.Gson;
import javafx.scene.Scene;
import javafx.scene.shape.Mesh;



public class Network {
    private String $schema, id, version, meshUUID, meshName, timestamp;

    private ArrayList netKeys, appKeys;
    private ArrayList<NetKeys> netKeyss;
    private ArrayList<AppKeys> appKeyss;
    private ArrayList <Groups> groups;
    private ArrayList <Scenes> scenes;
    private ArrayList <Nodes> nodes;
    private List<Provisioners> provisioners;


    public Network(String $schema, String id, String version, String meshUUID, String meshName, String timestamp,
                    String netKeysString, String appKeyString, String provisionersString, String nodesString,
                    String groupsString, String scenesString) {

        this.$schema = $schema;
        this.id = id;
        this.version = version;
        this.meshUUID = meshUUID;
        this.meshName = meshName;
        this.timestamp = timestamp;
        this.netKeys = makeObjectArray(netKeysString);
        this.appKeys = makeObjectArray(appKeyString);
        this.provisioners = makeObjectArray(provisionersString);
        this.nodes = makeObjectArray(nodesString);
        this.groups = makeObjectArray(groupsString);
        this.scenes = makeObjectArray(scenesString);
        this.provisioners = new ArrayList<>();

    }

    public Network (String $schema, String id, String version, String meshUUID, String meshName, String timestamp,
                    NetKeys netkeys, AppKeys appKeys, String provisionersString, String nodesString,
                    String groupsString, String scenesString) {

        ArrayList<NetKeys> netKeysTemp = new ArrayList<>();
        ArrayList<AppKeys> appKeysTemp = new ArrayList<>();
        netKeysTemp.add(netkeys);
        appKeysTemp.add(appKeys);

        this.$schema = $schema;
        this.id = id;
        this.version = version;
        this.meshUUID = meshUUID;
        this.meshName = meshName;
        this.timestamp = timestamp;
        this.netKeyss = netKeysTemp;
        this.appKeyss = appKeysTemp;
        this.provisioners = makeObjectArray(provisionersString);
        this.nodes = makeObjectArray(nodesString);
        this.groups = makeObjectArray(groupsString);
        this.scenes = makeObjectArray(scenesString);
        this.provisioners = new ArrayList<>();
    }

    public static ArrayList makeObjectArray(String json) {
        Gson g = new Gson();
        ArrayList arrayOfkeys = g.fromJson(json, ArrayList.class);
        return arrayOfkeys;
    }


    public String getMeshUUID() {
        return meshUUID;
    }

    public String getMeshName() {
        return meshName;
    }

    public ArrayList<Groups> getGroups() {
        return groups;
    }

    public ArrayList<String> getGroupsName() {
        ArrayList<String> nameOfGroups = new ArrayList<>();
        for (Groups g : groups) {
            nameOfGroups.add(g.getName());
        }
        return nameOfGroups;
    }

    public ArrayList<Nodes> getNodes () {return nodes;}

    public void setNodes (ArrayList<Nodes> newNodes) {
        this.nodes.addAll(newNodes);
    }

    public void setProvisioner(String username) {
        this.provisioners.add(generateProvisioner(username));
    }
    public List<Provisioners> getProvisioners(){
        return provisioners;
    }


    /**
     * Generates provisioner
     * @param username
     * @return
     */
    private Provisioners generateProvisioner(String username) {
        final String provisionerUuid = UUID.randomUUID().toString().toUpperCase(Locale.US);
        int installerCount = provisioners.size();
        int groupAddressRange = 49152;

        AllocGroupRange aGroupRange = new AllocGroupRange(Integer.toHexString(1 + groupAddressRange + installerCount*1000), Integer.toHexString(1000 + groupAddressRange + installerCount*1000));
        AllocUnicastRange aUnicastRange = new AllocUnicastRange(Integer.toHexString(1 + installerCount*1000),Integer.toHexString(installerCount*1000+1000));

        //final AllocatedGroupRange groupRange = new AllocatedGroupRange(MeshParserUtils.intToBytes(1 + groupAddressRange + installerCount*1000),MeshParserUtils.intToBytes(1000 + groupAddressRange + installerCount*1000));
        //final AllocatedUnicastRange unicastRange = new AllocatedUnicastRange(MeshParserUtils.intToBytes(1 + installerCount*1000), MeshParserUtils.intToBytes(installerCount*1000+1000));


        final List<AllocUnicastRange> uRanges = new ArrayList<>();
        final List<AllocGroupRange> gRanges = new ArrayList<>();
        gRanges.add(aGroupRange);
        uRanges.add(aUnicastRange);

        final Provisioners provisioner = new Provisioners(username, provisionerUuid, gRanges, uRanges);

        return provisioner;

    }

    /***
     * Just for a internal control check
     * @param scenes
     * @return names of scenes for a given group
     */
    public ArrayList<String> getScenesNameByGroup(ArrayList<Scenes> scenes) {
        ArrayList<String> nameOfScenes = new ArrayList<>();
        for (Scenes s : scenes) {
            nameOfScenes.add(s.getName());
        }
        return nameOfScenes;
    }


    public ArrayList <Scenes> getScenes () {
        return scenes;
    }

    public ArrayList <Scenes> getScenesById(Groups group) {
        ArrayList <Scenes> scenes = getScenes();
        ArrayList <Scenes> sceneAndId = new ArrayList<>();
        for (Scenes scene : scenes) {
            if (scene.getAddresses().contains(Integer.toHexString(group.getAddress()))) {
                sceneAndId.add(scene);
            }
        }
        return sceneAndId;
    }

}