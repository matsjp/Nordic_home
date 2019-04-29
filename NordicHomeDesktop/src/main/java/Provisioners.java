import java.util.*;

public class Provisioners {

    private String provisionerName, UUID, aGroupRangeString, aUnicastRangeString, aSceneRangeString;
    private ArrayList allocatedGroupRange, allocatedUnicastRange, allocatedSceneRange;

    public Provisioners (String provisionerName, String UUID, ArrayList allocatedGroupRange, ArrayList allocatedUnicastRange,
                         ArrayList allocatedSceneRange, String aGroupRangeString, String aUnicastRangeString, String aSceneRangeString) {
        this.provisionerName = provisionerName;
        this.UUID = UUID;
        this.allocatedGroupRange = Network.makeObjectArray(aGroupRangeString);
        this.allocatedUnicastRange = Network.makeObjectArray(aUnicastRangeString);
        this.allocatedSceneRange = Network.makeObjectArray(aSceneRangeString);
    }
}