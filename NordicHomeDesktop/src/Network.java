import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Network {
    private String name;
    private final String address;

    private HashMap<String, ArrayList<String>> groups = new HashMap<String, ArrayList<String>>();


    public Network(String name, String address) {
        this.name = name;
        this.address = address;
        this.groups = this.getFullGroups();
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
       scenes.add("Dimmed");

       this.groups.put(group,scenes);
    }

    public HashMap<String, ArrayList<String>> getFullGroups() {
        return groups;
    }

    public Set<String> getGroups() {
        Set<String> keys = groups.keySet();
        return keys;
    }

}
