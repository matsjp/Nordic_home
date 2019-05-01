import java.util.*;

public class Provisioners {

    private String provisionerName, UUID, GroupRangeString, UnicastRangeString, SceneRangeString;
    private List<AllocGroupRange> allocatedGroupRange;
    private List <AllocUnicastRange> allocatedUnicastRange;
    //private List <AllocatedSceneRange> allocatedSceneRange;

    public Provisioners (String provisionerName, String UUID, String GroupRangeString, String UnicastRangeString) {       //String SceneRangeString (Not using this)
        this.provisionerName = provisionerName;
        this.UUID = UUID;
        this.allocatedGroupRange = Network.makeObjectArray(GroupRangeString);
        this.allocatedUnicastRange = Network.makeObjectArray(UnicastRangeString);
        //this.allocatedSceneRange = Network.makeObjectArray(SceneRangeString);
    }

    public Provisioners (String provisionerName, String UUID, List<AllocGroupRange> allocatedGroupRange, List<AllocUnicastRange> allocatedUnicastRange) {   //Not using: List<AllocatedSceneRange> allocatedSceneRange
        this.provisionerName = provisionerName;
        this.UUID = UUID;
        this.allocatedGroupRange = allocatedGroupRange;
        this.allocatedUnicastRange = allocatedUnicastRange;
        //this.allocatedSceneRange = allocatedScenerange;

    }


}