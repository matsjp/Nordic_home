import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Network {
    private String name;
    private final String address;

    private HashMap<String, ArrayList<String>> groups = new HashMap<String, ArrayList<String>>();
    private HashMap<String, ArrayList<String>> parentGroups = new HashMap<>();


    public Network(String name, String address) {
        this.name = name;
        this.address = address;
        this.groups = this.getFullGroups();
        this.parentGroups = this.getFullParentGroups();
    }

    public String getName() {
        return this.name;
    }

    public String getAddress() {
        return this.address;
    }

    public void setName(String name) {
        this.name = name;
    }


    public void addGroup(String group){
       //Default scenes
       ArrayList<String> scenes = new ArrayList<>();
       scenes.add("On");
       scenes.add("Off");
       scenes.add("Dim");

       this.groups.put(group,scenes);
    }

    public HashMap<String, ArrayList<String>> getFullGroups() {
        return groups;
    }

    public Set<String> getGroups() {
        Set<String> keys = groups.keySet();
        return keys;
    }

    public HashMap<String, ArrayList<String>> getFullParentGroups() {
        return parentGroups;
    }

    public Set<String> getParentGroups(){
        Set<String> keys = parentGroups.keySet();
        return keys;
    }

    public void addParentGroup(String groupName, ArrayList<String> groups){
        this.parentGroups.put(groupName, groups);
    }
}
