import java.util.*;

public class Nodes {

    private String UUID, name, deviceKey, unicastAddress, security, cid, pid, vid, crpl, featuresString, elementsString,
            netKeysString, appKeysString, networkTransmitString, defaultTTLString;
    private boolean configComplete;
    private ArrayList features, elements, netKeys, appKeys, networkTransmit, defaultTTL;

    public Nodes (String UUID, String name, String deviceKey, String unicastAddress, String security, String cid,
                  String pid, String vid, String crpl, ArrayList features, ArrayList elements, ArrayList netKeys, ArrayList appKeys,
                  ArrayList networkTransmit, ArrayList defaultTTL, String featuresString, String elementsString, String netKeysString,
                  String appKeysString, String networkTransmitString, String defaultTTLString) {

        this.UUID = UUID;
        this.name = name;
        this.deviceKey = deviceKey;
        this.unicastAddress = unicastAddress;
        this.security = security;
        this.cid = cid;
        this.pid = pid;
        this.vid = vid;
        this.crpl = crpl;
        this.features = Network.makeObjectArray(featuresString);
        this.elements = Network.makeObjectArray(elementsString);
        this.netKeys = Network.makeObjectArray(netKeysString);
        this.appKeys = Network.makeObjectArray(appKeysString);
        this.networkTransmit = Network.makeObjectArray(networkTransmitString);
        this.defaultTTL = Network.makeObjectArray(defaultTTLString);
    }
}