import java.lang.reflect.Array;
import java.util.*;
import com.google.gson.Gson;
import javafx.scene.Scene;

public class Network {
    private String $schema, id, version, meshUUID, meshName, timestamp;

    private ArrayList netKeys, appKeys, provisioners, nodes, scenes;
    private ArrayList <Groups> groups;


    public Network(String $schema, String id, String version, String meshUUID, String meshName, String timestamp,
                   String netKeysString, String appKeyString, String provisionersString, String nodeString,
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
        this.nodes = makeObjectArray(nodeString);
        this.groups = makeObjectArray(groupsString);
        this.scenes = makeObjectArray(scenesString);


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

    public ArrayList <Groups> getGroups() {
        return groups;
    }

    public ArrayList<String> getGroupsName() {
        ArrayList<String> nameOfGroups = new ArrayList<>();
        for (Groups g : groups) {
            nameOfGroups.add(g.getName());
        }
        return nameOfGroups;
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
            if (scene.getAddresses().contains(group.getAddress())) {
                sceneAndId.add(scene);
            }
        }
        return sceneAndId;
    }

}