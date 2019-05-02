import java.util.*;

public class Provisioners {

    private String provisionerName, UUID, GroupRangeString, UnicastRangeString, SceneRangeString;
    private List<AllocGroupRange> allocatedGroupRange;
    private List <AllocUnicastRange> allocatedUnicastRange;
    //private List <AllocatedSceneRange> allocatedSceneRange;

    public Provisioners (String provisionerName, String UUID, String UnicastRangeString) {       //String SceneRangeString (Not using this) //String GroupRangeString
        this.provisionerName = provisionerName;
        this.UUID = UUID;
        this.allocatedGroupRange = Network.makeObjectArray(GroupRangeString);
        this.allocatedUnicastRange = Network.makeObjectArray(UnicastRangeString);
        //this.allocatedSceneRange = Network.makeObjectArray(SceneRangeString);
    }

    public Provisioners (String provisionerName, String UUID, List<AllocUnicastRange> allocatedUnicastRange) {   //Not using: List<AllocatedSceneRange> allocatedSceneRange //List<AllocGroupRange> allocatedGroupRange
        this.provisionerName = provisionerName;
        this.UUID = UUID;
        this.allocatedGroupRange = allocatedGroupRange;
        this.allocatedUnicastRange = allocatedUnicastRange;
        //this.allocatedSceneRange = allocatedScenerange;

    }


}