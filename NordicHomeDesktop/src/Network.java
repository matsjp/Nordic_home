import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Network {
    private String name;
    private final String address;
    //private ArrayList<String> groups = new ArrayList<String>();

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



    /*
    public ArrayList<String> getGroups() {
        return groups;
    }

    public void setGroups(ArrayList groups) {
        this.groups = groups;
    }

    public void addGroup (String group){
        this.groups.add(group);
    }*/




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


    public static void main(String[] args) {
        Network n = new Network("Nettnett","75849398475");
        System.out.println("Nettverk: "+n.getName()+". Adresse: "+n.getAddress());
        n.addGroup("Bad");
        n.addGroup("Kjøkken");
        n.addGroup("Rom");
        System.out.println("Groups: "+n.getGroups());

        ArrayList<String> scenes = n.groups.get("Bad");
        ArrayList<String> scenesK = n.groups.get("Kjøkken");


        System.out.println("Scener til badet: "+ scenes);
        System.out.println("Scener til kjøkkenet: "+scenesK);


        scenesK.remove(2);
        System.out.println("Scener til kjøkkenet: "+scenesK);
        System.out.println("Scener til badet: "+scenes);


    }

}
